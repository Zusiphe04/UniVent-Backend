package za.ac.cput.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.Venue;
import za.ac.cput.dtos.EventUpsertRequest;
import za.ac.cput.dtos.RegistrationResponse;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.OrganizerRepository;
import za.ac.cput.repository.VenueRepository;
import za.ac.cput.util.IsoDateTime;

import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class EventWorkflowService {
    private final EventRepository eventRepository;
    private final OrganizerRepository organizerRepository;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;

    public EventWorkflowService(EventRepository eventRepository,
                                OrganizerRepository organizerRepository,
                                VenueRepository venueRepository,
                                BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.organizerRepository = organizerRepository;
        this.venueRepository = venueRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Event create(EventUpsertRequest request, String organizerId) {
        Organizer organizer = requireActiveOrganizer(organizerId);
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        Event event = new Event.Builder()
                .setName(request.title().trim())
                .setDescription(request.description())
                .setDateTime(normalizeDateTime(request.dateTime(), "Event date and time"))
                .setEndDateTime(normalizeOptionalDateTime(request.endDateTime(), "Event end date and time"))
                .setCategory(request.category())
                .setImageUrl(request.imageUrl())
                .setMaxAttendees(request.maxAttendees())
                .setVenue(venue)
                .setOrganizer(organizer)
                .setStatus(EventStatusEnum.PENDING_APPROVAL)
                .build();
        return eventRepository.save(event);
    }

    @Transactional
    public Event update(int eventId, EventUpsertRequest request, String organizerId) {
        requireActiveOrganizer(organizerId);
        Event event = ownedEvent(eventId, organizerId);
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        event.setName(request.title().trim());
        event.setDescription(request.description());
        event.setDateTime(normalizeDateTime(request.dateTime(), "Event date and time"));
        event.setEndDateTime(normalizeOptionalDateTime(request.endDateTime(), "Event end date and time"));
        event.setCategory(request.category());
        event.setImageUrl(request.imageUrl());
        event.setMaxAttendees(request.maxAttendees());
        event.setVenue(venue);
        return eventRepository.save(event);
    }

    @Transactional
    public Event cancel(int eventId, String organizerId) {
        requireActiveOrganizer(organizerId);
        Event event = ownedEvent(eventId, organizerId);
        if (event.getStatus() == EventStatusEnum.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is already cancelled");
        }
        event.setStatus(EventStatusEnum.CANCELLED);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> registrations(int eventId, String requesterId, boolean admin) {
        if (admin) {
            if (!eventRepository.existsById(eventId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            }
            return bookingRepository.findByEvent_EventIdOrderByBookingTimeDesc(eventId).stream()
                    .map(RegistrationResponse::from)
                    .toList();
        }
        if (eventRepository.findByIdAndOrganizerId(eventId, requesterId).isEmpty()) {
            if (!eventRepository.existsById(eventId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this event");
        }
        return bookingRepository.findByEvent_EventIdAndEvent_Organizer_UserIdOrderByBookingTimeDesc(
                        eventId, requesterId).stream()
                .map(RegistrationResponse::from)
                .toList();
    }

    private Organizer requireActiveOrganizer(String organizerId) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organizer not found"));
        if (organizer.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Organizer account is pending approval");
        }
        return organizer;
    }

    private Event ownedEvent(int eventId, String organizerId) {
        return eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseGet(() -> {
                    if (!eventRepository.existsById(eventId)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
                    }
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this event");
                });
    }

    private String normalizeOptionalDateTime(String value, String field) {
        return value == null || value.isBlank() ? null : normalizeDateTime(value, field);
    }

    private String normalizeDateTime(String value, String field) {
        try {
            return IsoDateTime.format(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(field + " must be ISO 8601", exception);
        }
    }
}

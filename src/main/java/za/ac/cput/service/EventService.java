package za.ac.cput.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Venue;
import za.ac.cput.dtos.EventUpdateRequest;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.EventSpecifications;
import za.ac.cput.repository.VenueRepository;
import za.ac.cput.util.AuthenticatedUser;
import za.ac.cput.util.Helper;
import za.ac.cput.util.UnauthorizedException;

import java.util.List;

@Service
public class EventService implements IEventService {
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
    }

    @Override
    public Event create(Event event) {
        if (event == null) {
            return null;
        }
        // Every event must enter the administrator approval workflow. Forcing the
        // status here means no caller can create an already-published event, even
        // if they manage to reach this service directly.
        event.setStatus(EventStatusEnum.PENDING_APPROVAL);
        return eventRepository.save(event);
    }

    @Override
    public Event read(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Page<Event> search(String search, String status, String from, String to, Pageable pageable) {
        Pageable page = pageable == null ? PageRequest.of(0, 20) : pageable;
        String fromBound = EventFilter.normalizeFrom(from);
        String toBound = EventFilter.normalizeTo(to);
        EventFilter.validateRange(fromBound, toBound);
        return eventRepository.findAll(
                EventSpecifications.withFilters(search, parseStatus(status), fromBound, toBound),
                page);
    }

    @Override
    public Event update(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Integer id, EventUpdateRequest request, AuthenticatedUser organizer) {
        Event existing = requireEvent(id);
        verifyOwnership(existing, organizer);
        applyUpdate(existing, request);
        return eventRepository.save(existing);
    }

    @Override
    public Event cancelEvent(Integer id, AuthenticatedUser organizer) {
        Event existing = requireEvent(id);
        verifyOwnership(existing, organizer);
        existing.setStatus(EventStatusEnum.CANCELLED);
        return eventRepository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        eventRepository.deleteById(id);
    }

    private Event requireEvent(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    private void verifyOwnership(Event event, AuthenticatedUser organizer) {
        if (organizer == null || Helper.isNullOrEmpty(organizer.userId())) {
            throw new UnauthorizedException("Organizer authentication is required");
        }
        if (event.getOrganizer() == null
                || event.getOrganizer().getUserId() == null
                || !event.getOrganizer().getUserId().equals(organizer.userId())) {
            throw new UnauthorizedException("Organizer does not own this event");
        }
    }

    private void applyUpdate(Event existing, EventUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Event update body is required");
        }

        String name = request.resolvedName();
        if (Helper.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Event name is required");
        }

        String description = request.getDescription();
        if (Helper.isNullOrEmpty(description)) {
            throw new IllegalArgumentException("Description is required");
        }

        String dateTime = request.resolvedDateTime();
        if (!Helper.isValidDateTime(dateTime)) {
            throw new IllegalArgumentException("Invalid date and time");
        }

        Integer maxAttendees = request.resolvedMaxAttendees();
        if (maxAttendees == null || !Helper.isPositive(maxAttendees)) {
            throw new IllegalArgumentException("Maximum attendees must be greater than 0");
        }

        existing.setName(name.trim());
        existing.setDescription(description.trim());
        existing.setDateTime(dateTime.trim());
        existing.setMaxAttendees(maxAttendees);

        if (!Helper.isNullOrEmpty(request.getVenueId())) {
            Venue venue = venueRepository.findById(request.getVenueId().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Venue not found"));
            existing.setVenue(venue);
        }
    }

    private EventStatusEnum parseStatus(String status) {
        if (Helper.isNullOrEmpty(status)) {
            return null;
        }
        try {
            return EventStatusEnum.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid event status: " + status);
        }
    }
}

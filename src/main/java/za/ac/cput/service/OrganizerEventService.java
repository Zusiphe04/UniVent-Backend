package za.ac.cput.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.dtos.OrganizerEventSummaryDTO;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.util.IsoDateTime;

import java.util.List;

@Service
public class OrganizerEventService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public OrganizerEventService(EventRepository eventRepository,
                                 BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizerEventSummaryDTO> list(String organizerId, EventStatusEnum status) {
        return eventRepository.findByOrganizerIdAndOptionalStatus(organizerId, status)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    private OrganizerEventSummaryDTO toSummary(Event event) {
        return new OrganizerEventSummaryDTO(
                Integer.toString(event.getEventId()),
                event.getName(),
                IsoDateTime.format(event.getDateTime()),
                event.getVenue() == null ? null : event.getVenue().getVenueName(),
                event.getStatus() == null ? null : event.getStatus().name(),
                bookingRepository.countByEvent(event));
    }
}

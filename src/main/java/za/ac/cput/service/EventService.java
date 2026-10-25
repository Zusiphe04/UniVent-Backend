package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.repository.EventRepository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class EventService implements IEventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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

    public List<Event> findApproved(String search, String category, String date, String status) {
        if (status != null && !status.isBlank()) {
            EventStatusEnum requestedStatus;
            try {
                requestedStatus = EventStatusEnum.valueOf(status.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown event status: " + status);
            }
            if (requestedStatus != EventStatusEnum.APPROVED) {
                return List.of();
            }
        }
        return eventRepository.findApprovedFiltered(
                normalize(search), normalize(category), normalize(date));
    }

    public Optional<Event> findApprovedById(int id) {
        return eventRepository.findByEventIdAndStatus(id, EventStatusEnum.APPROVED);
    }

    @Override
    public Event update(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void delete(Integer id) {
        eventRepository.deleteById(id);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase(Locale.ROOT);
    }
}

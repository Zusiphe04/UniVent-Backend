package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Event;
import za.ac.cput.dtos.EventSummaryResponse;
import za.ac.cput.service.EventService;

/**
 * Read-only event endpoints. Event changes are handled by their authorized
 * workflows rather than accepting an organizer from the request body.
 */
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventSummaryResponse>> getEvents() {
        List<EventSummaryResponse> events = eventService.findAll().stream()
                .map(EventSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventSummaryResponse> getEventById(@PathVariable Integer id) {
        Event event = eventService.read(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EventSummaryResponse.from(event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

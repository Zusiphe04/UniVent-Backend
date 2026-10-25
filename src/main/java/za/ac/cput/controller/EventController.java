package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<List<EventSummaryResponse>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        if (date != null && !date.isBlank() && !date.trim().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must use YYYY-MM-DD");
        }
        List<EventSummaryResponse> events = eventService.findApproved(search, category, date, status).stream()
                .map(EventSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventSummaryResponse> getEventById(@PathVariable Integer id) {
        EventSummaryResponse response = eventService.findApprovedById(id)
                .map(EventSummaryResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Events are retained; use the cancel workflow");
    }
}

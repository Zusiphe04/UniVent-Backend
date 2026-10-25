package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.dtos.EventSummaryResponse;
import za.ac.cput.service.EventService;
import za.ac.cput.util.AuthenticatedUser;

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
    public ResponseEntity<EventSummaryResponse> getEventById(@PathVariable Integer id,
                                                             Authentication authentication) {
        String requesterId = authentication != null
                && authentication.getPrincipal() instanceof AuthenticatedUser principal
                ? principal.userId() : null;
        boolean admin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        EventSummaryResponse response = eventService.findVisibleById(id, requesterId, admin)
                .map(EventSummaryResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,
                "Events are retained; use the cancel workflow");
    }
}

package za.ac.cput.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Event;
import za.ac.cput.dtos.EventSummaryResponse;
import za.ac.cput.dtos.EventUpdateRequest;
import za.ac.cput.service.EventService;
import za.ac.cput.util.AuthenticatedUser;
import za.ac.cput.util.UnauthorizedException;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Browse events as {@link EventSummaryResponse} pages.
     * Filters {@code search}, {@code status}, {@code from} and {@code to} are combined with AND.
     * Without filters, all events are returned (paginated).
     * {@code category} is accepted for frontend compatibility but is not applied:
     * the Event model has no category column.
     */
    @GetMapping
    public ResponseEntity<Page<EventSummaryResponse>> getEvents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EventSummaryResponse> events = eventService
                .search(search, status, from, to, pageable)
                .map(EventSummaryResponse::from);
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

    @PutMapping("/{id}")
    public ResponseEntity<EventSummaryResponse> updateEvent(@PathVariable Integer id,
                                                            @RequestBody EventUpdateRequest request,
                                                            Authentication authentication) {
        Event updated = eventService.updateEvent(id, request, requireOrganizer(authentication));
        return ResponseEntity.ok(EventSummaryResponse.from(updated));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventSummaryResponse> cancelEvent(@PathVariable Integer id,
                                                            Authentication authentication) {
        Event cancelled = eventService.cancelEvent(id, requireOrganizer(authentication));
        return ResponseEntity.ok(EventSummaryResponse.from(cancelled));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Missing or anonymous authentication is rejected by the security filter as 401.
     * A signed-in non-organizer is rejected by {@code hasRole("ORGANIZER")} as 403.
     * This method covers the remaining case: an authenticated principal that is not
     * an {@link AuthenticatedUser}, which maps to 403 via {@code UnauthorizedException}.
     */
    private AuthenticatedUser requireOrganizer(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new UnauthorizedException("Organizer authentication is required");
        }
        if (principal.userId() == null || principal.userId().isBlank()) {
            throw new UnauthorizedException("Organizer authentication is required");
        }
        return principal;
    }
}

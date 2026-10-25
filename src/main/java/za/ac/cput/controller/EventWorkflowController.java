package za.ac.cput.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.Event;
import za.ac.cput.dtos.EventSummaryResponse;
import za.ac.cput.dtos.EventUpsertRequest;
import za.ac.cput.dtos.RegistrationResponse;
import za.ac.cput.service.EventWorkflowService;
import za.ac.cput.util.AuthenticatedUser;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventWorkflowController {
    private final EventWorkflowService eventWorkflowService;

    public EventWorkflowController(EventWorkflowService eventWorkflowService) {
        this.eventWorkflowService = eventWorkflowService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventSummaryResponse> create(@Valid @RequestBody EventUpsertRequest request,
                                                       Authentication authentication) {
        Event event = eventWorkflowService.create(request, currentUserId(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(EventSummaryResponse.from(event));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventSummaryResponse> update(@PathVariable int id,
                                                       @Valid @RequestBody EventUpsertRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(EventSummaryResponse.from(
                eventWorkflowService.update(id, request, currentUserId(authentication))));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<EventSummaryResponse> cancel(@PathVariable int id,
                                                       Authentication authentication) {
        return ResponseEntity.ok(EventSummaryResponse.from(
                eventWorkflowService.cancel(id, currentUserId(authentication))));
    }

    @GetMapping("/{eventId}/bookings")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public List<RegistrationResponse> registrations(@PathVariable int eventId,
                                                   Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return eventWorkflowService.registrations(eventId, user.userId(), admin);
    }

    private String currentUserId(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).userId();
    }
}

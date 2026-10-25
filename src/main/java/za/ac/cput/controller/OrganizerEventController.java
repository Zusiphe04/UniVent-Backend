package za.ac.cput.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.domain.EventStatus;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.dtos.OrganizerEventSummaryDTO;
import za.ac.cput.service.OrganizerEventService;
import za.ac.cput.util.AuthenticatedUser;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/organizers/me/events")
public class OrganizerEventController {
    private final OrganizerEventService organizerEventService;

    public OrganizerEventController(OrganizerEventService organizerEventService) {
        this.organizerEventService = organizerEventService;
    }

    @GetMapping
    public List<OrganizerEventSummaryDTO> list(
            @RequestParam(required = false) String status,
            Authentication authentication) {
        EventStatusEnum parsedStatus = parseStatus(status);
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return organizerEventService.list(isAdmin ? null : principal.userId(), parsedStatus);
    }

    private EventStatusEnum parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return null;
        }
        try {
            return EventStatusEnum.valueOf(
                    EventStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT)).name());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown event status: " + rawStatus);
        }
    }
}

package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.dtos.AdminEventSummaryDTO;
import za.ac.cput.service.IAdministratorService;

@RestController
@RequestMapping("/api/events")
public class EventModerationController {
    private final IAdministratorService administratorService;

    public EventModerationController(IAdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminEventSummaryDTO> approve(@PathVariable String id) {
        return ResponseEntity.ok(administratorService.approveEvent(id));
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminEventSummaryDTO> disable(@PathVariable String id) {
        return ResponseEntity.ok(administratorService.disableEvent(id));
    }
}

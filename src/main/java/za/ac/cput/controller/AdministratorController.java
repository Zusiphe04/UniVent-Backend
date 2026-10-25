package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Administrator;
import za.ac.cput.dtos.AdminEventSummaryDTO;
import za.ac.cput.dtos.PlatformStatsDTO;
import za.ac.cput.dtos.UserResponseDTO;
import za.ac.cput.service.IAdministratorService;
import za.ac.cput.util.AuthenticatedUser;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@RestController
@RequestMapping("/api/admin")
public class AdministratorController {
    private final IAdministratorService administratorService;

    public AdministratorController(IAdministratorService administratorService){
        this.administratorService = administratorService;
    }

    @GetMapping("/events")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminEventSummaryDTO>> getAllEvents(
            @RequestParam(defaultValue = "ALL") String status) {
        return ResponseEntity.ok(administratorService.getEvents(status));
    }

    @PatchMapping("/events/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminEventSummaryDTO> approveEvent(@PathVariable String id) {
        return ResponseEntity.ok(administratorService.approveEvent(id));
    }

    @PatchMapping("/events/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminEventSummaryDTO> disableEvent(@PathVariable String id) {
        return ResponseEntity.ok(administratorService.disableEvent(id));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid page or size");
        }
        return ResponseEntity.ok(administratorService.getUsers(search, role, PageRequest.of(page, size)));
    }

    @PatchMapping("/users/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> disableUser(
            @PathVariable String id,
            Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return ResponseEntity.ok(administratorService.disableUser(id, principal.userId()));
    }

    @PatchMapping("/users/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> approveOrganizer(@PathVariable String id) {
        return ResponseEntity.ok(administratorService.approveOrganizer(id));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformStatsDTO> getPlatformStats() {
        return ResponseEntity.ok(administratorService.getStats());
    }

    public ResponseEntity<Administrator> createAdministrator(@RequestBody Administrator administrator) {
        Administrator createdAdministrator = administratorService.create(administrator);
        return ResponseEntity.ok(createdAdministrator);
    }

    public ResponseEntity<Administrator> getAdministratorById(@PathVariable String id) {
        Administrator administrator = administratorService.read(id);
        return ResponseEntity.ok(administrator);
    }

    public ResponseEntity<Administrator> updateAdministrator(@RequestBody Administrator administrator) {
        Administrator updatedAdministrator = administratorService.update(administrator);
        return ResponseEntity.ok(updatedAdministrator);
    }

    public ResponseEntity<Void> delete(@PathVariable String id) {
        administratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

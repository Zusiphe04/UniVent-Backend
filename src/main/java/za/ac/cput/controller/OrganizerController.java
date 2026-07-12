package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Organizer;
import za.ac.cput.service.OrganizerService;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {
    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService){
        this.organizerService = organizerService;
    }
    @PostMapping
    public ResponseEntity<Organizer> createOrganizer(@RequestBody Organizer organizer) {
        Organizer createdOrganizer = organizerService.create(organizer);
        return ResponseEntity.ok(createdOrganizer);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Organizer> getOrganizerById(@PathVariable String id) {
        Organizer organizer = organizerService.read(id);
        return ResponseEntity.ok(organizer);
    }
    @PutMapping()
    public ResponseEntity<Organizer> updateOrganizer(@RequestBody Organizer organizer) {
        Organizer updatedOrganizer = organizerService.update(organizer);
        return ResponseEntity.ok(updatedOrganizer);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        organizerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

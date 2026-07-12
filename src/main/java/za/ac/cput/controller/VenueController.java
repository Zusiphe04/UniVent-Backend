package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Venue;
import za.ac.cput.service.VenueService;

/**
 * Student Name: Sesethu Nciti
 * Student Number: 231118384
 */

@RestController
@RequestMapping("/venue")
public class VenueController {

    private final VenueService service;

    public VenueController(VenueService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue createdVenue = service.create(venue);
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Integer id) {
        Venue venue = service.read(id);
        return ResponseEntity.ok(venue);
    }

    @PutMapping("/update")
    public ResponseEntity<Venue> updateVenue(@RequestBody Venue venue) {
        Venue updatedVenue = service.update(venue);
        return ResponseEntity.ok(updatedVenue);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

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

    @GetMapping("/read/{venueId}")
    public ResponseEntity<Venue> getVenueById(@PathVariable String venueId) {
        Venue venue = service.read(venueId);
        return ResponseEntity.ok(venue);
    }

    @PutMapping("/update")
    public ResponseEntity<Venue> updateVenue(@RequestBody Venue venue) {
        Venue updatedVenue = service.update(venue);
        return ResponseEntity.ok(updatedVenue);
    }

    @DeleteMapping("/delete/{venueId}")
    public ResponseEntity<Void> deleteVenue(@PathVariable String venueId) {
        service.delete(venueId);
        return ResponseEntity.noContent().build();
    }
}
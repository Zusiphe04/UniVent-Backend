package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Event;
import za.ac.cput.service.EventService;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event createdEvent = eventService.create(event);
        return ResponseEntity.ok(createdEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        Event event = eventService.read(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping
    public ResponseEntity<Event> updateEvent(@RequestBody Event event) {
        Event updatedEvent = eventService.update(event);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


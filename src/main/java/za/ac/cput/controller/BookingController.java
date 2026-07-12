package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.BookingStatusEnum;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Student;
import za.ac.cput.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Register a student for an event
    @PostMapping("/register")
    public ResponseEntity<?> registerForEvent(@RequestBody RegistrationRequest request) {
        try {
            Booking booking = bookingService.registerForEvent(
                    request.getStudent(),
                    request.getEvent()
            );
            return new ResponseEntity<>(booking, HttpStatus.CREATED);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Cancel a booking
    @PutMapping("/{bookingReference}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingReference) {
        try {
            Booking booking = bookingService.cancelBooking(bookingReference);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Confirm a booking
    @PutMapping("/{bookingReference}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable String bookingReference) {
        try {
            Booking booking = bookingService.confirmBooking(bookingReference);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        try {
            Booking booking = bookingService.getBookingById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get booking by reference
    @GetMapping("/reference/{bookingReference}")
    public ResponseEntity<?> getBookingByReference(@PathVariable String bookingReference) {
        try {
            Booking booking = bookingService.getBookingByReference(bookingReference)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));
            return ResponseEntity.ok(booking);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    // Get bookings by student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getBookingsByStudent(@PathVariable Integer studentId) {
        // You would need to fetch the student from StudentService
        // This is a placeholder - you'll need to inject StudentService
        // and fetch the student by ID first
        return ResponseEntity.ok(bookingService.getBookingsByStudent(null));
    }

    // Get bookings by event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getBookingsByEvent(@PathVariable Integer eventId) {
        // You would need to fetch the event from EventService
        // This is a placeholder - you'll need to inject EventService
        // and fetch the event by ID first
        return ResponseEntity.ok(bookingService.getBookingsByEvent(null));
    }

    // Get bookings by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBookingsByStatus(@PathVariable BookingStatusEnum status) {
        try {
            List<Booking> bookings = bookingService.getBookingsByStatus(status);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete booking by reference
    @DeleteMapping("/{bookingReference}")
    public ResponseEntity<?> deleteBooking(@PathVariable String bookingReference) {
        try {
            bookingService.deleteBooking(bookingReference);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Delete booking by ID
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteBookingById(@PathVariable Integer id) {
        try {
            bookingService.deleteBookingById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Check if student has booked event
    @GetMapping("/exists")
    public ResponseEntity<?> hasStudentBookedEvent(
            @RequestParam Integer studentId,
            @RequestParam Integer eventId) {
        // You would need to fetch student and event from their respective services
        // This is a placeholder
        return ResponseEntity.ok(bookingService.hasStudentBookedEvent(null, null));
    }

    // Get booking count for event
    @GetMapping("/count/event/{eventId}")
    public ResponseEntity<?> getBookingCountForEvent(@PathVariable Integer eventId) {
        // You would need to fetch the event from EventService
        // This is a placeholder
        return ResponseEntity.ok(bookingService.getBookingCountForEvent(null));
    }

    // Inner class for registration request
    public static class RegistrationRequest {
        private Student student;
        private Event event;

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }
    }
}
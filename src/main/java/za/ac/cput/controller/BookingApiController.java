package za.ac.cput.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.dtos.BookingResponse;
import za.ac.cput.service.BookingApiService;
import za.ac.cput.util.AuthenticatedUser;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {
    private final BookingApiService bookingApiService;

    public BookingApiController(BookingApiService bookingApiService) {
        this.bookingApiService = bookingApiService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody CreateBookingRequest request,
                                                  Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                bookingApiService.create(request.eventId(), currentUserId(authentication)));
    }

    @GetMapping({"/my", "/me"})
    public List<BookingResponse> mine(Authentication authentication) {
        return bookingApiService.mine(currentUserId(authentication));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(@RequestParam @Min(1) int eventId,
                                          Authentication authentication) {
        return ResponseEntity.ok(bookingApiService.exists(eventId, currentUserId(authentication)));
    }

    @PatchMapping("/{bookingReference}/cancel")
    public ResponseEntity<BookingResponse> cancel(@PathVariable String bookingReference,
                                                  Authentication authentication) {
        return ResponseEntity.ok(bookingApiService.cancel(bookingReference, currentUserId(authentication)));
    }

    private String currentUserId(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).userId();
    }

    public record CreateBookingRequest(@Min(1) int eventId) {
    }
}

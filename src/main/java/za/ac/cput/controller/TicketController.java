package za.ac.cput.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Ticket;
import za.ac.cput.domain.User;
import za.ac.cput.dtos.TicketDTO;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final UserRepository userRepository;

    public TicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.create(ticket);
        return ResponseEntity.ok(createdTicket);
    }
    public ResponseEntity<Ticket> getTicketById(@PathVariable String id) {
        Ticket ticket = ticketService.read(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }
    public ResponseEntity<Ticket> updateTicket(@RequestBody Ticket ticket) {
        Ticket updatedTicket = ticketService.update(ticket);
        return ResponseEntity.ok(updatedTicket);
    }
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Issue #23 ---

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getTicketByBooking(@PathVariable int bookingId) {
        // requestingUserId is now resolved from the authenticated JWT principal
        // (JwtAuthenticationFilter sets the principal to the user's email),
        // instead of a client-supplied param.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        String email = authentication.getName();
        User requestingUser = userRepository.findByEmail(email).orElse(null);
        if (requestingUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication is required");
        }

        TicketDTO ticket = ticketService.getTicketByBookingId(bookingId, requestingUser.getUserId());
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return ResponseEntity.ok(ticket);
    }

    public ResponseEntity<Ticket> getTicketByCode(@PathVariable String ticketCode) {
        // NOTE: issue #23 restricts this to ORGANIZER or ADMIN roles.
        // Role-based access isn't enforced yet in this project.
        Ticket ticket = ticketService.getTicketByCode(ticketCode);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    public ResponseEntity<Ticket> markTicketUsed(@PathVariable String ticketCode) {
        // NOTE: same ORGANIZER/ADMIN restriction as above, not yet enforced.
        Ticket updated = ticketService.markTicketUsed(ticketCode);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}
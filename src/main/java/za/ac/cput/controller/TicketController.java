package za.ac.cput.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Ticket;
import za.ac.cput.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketService ticketService;
    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.create(ticket);
        return ResponseEntity.ok(createdTicket);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Integer id) {
        Ticket ticket = ticketService.read(id);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }
    @PutMapping()
    public ResponseEntity<Ticket> updateTicket(@RequestBody Ticket ticket) {
        Ticket updatedTicket = ticketService.update(ticket);
        return ResponseEntity.ok(updatedTicket);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

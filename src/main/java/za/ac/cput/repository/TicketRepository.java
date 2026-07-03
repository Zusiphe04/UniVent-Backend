package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Ticket;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Optional<Ticket> findByBookingId(int bookingId);
}
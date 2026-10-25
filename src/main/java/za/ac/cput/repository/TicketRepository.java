package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import za.ac.cput.domain.Ticket;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    Optional<Ticket> findByTicketCode(String ticketCode);
    Optional<Ticket> findByBookingId(int bookingId);

    @Query("select ticket from Ticket ticket where ticket.bookingId = :bookingId " +
            "and exists (select booking.bookingId from Booking booking " +
            "where booking.bookingId = ticket.bookingId and booking.student.userId = :studentId)")
    Optional<Ticket> findOwnedByBookingId(@Param("bookingId") int bookingId,
                                          @Param("studentId") String studentId);
}
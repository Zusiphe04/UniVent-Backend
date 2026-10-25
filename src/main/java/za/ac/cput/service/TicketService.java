package za.ac.cput.service;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *title: Service Milestone
 * Date: 12 July 2026
 **/

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Ticket;
import za.ac.cput.dtos.TicketDTO;
import za.ac.cput.util.UnauthorizedException;
import za.ac.cput.factory.TicketFactory;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.TicketRepository;
import za.ac.cput.util.Helper;

import java.time.LocalDate;

@Service
public class TicketService implements ITicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;

    public TicketService(TicketRepository ticketRepository, BookingRepository bookingRepository) {
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Ticket findByBookingId(int bookingId) {
        return ticketRepository.findByBookingId(bookingId).orElse(null);
    }

    @Override
    public Ticket create(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket read(String id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Override
    public Ticket update(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(String id) {
        ticketRepository.deleteById(id);
    }

    // --- Issue #22 ---

    @Override
    public TicketDTO generateTicket(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking is required");
        }

        String ticketCode = Helper.generateId();
        LocalDate issueDate = LocalDate.now();

        Ticket ticket = TicketFactory.createTicket(booking.getBookingId(), issueDate, ticketCode);
        Ticket saved = ticketRepository.save(ticket);

        return toDTO(saved, booking);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO getTicketByBookingId(int bookingId, String requestingUserId) {
        Ticket ticket = ticketRepository.findOwnedByBookingId(bookingId, requestingUserId).orElse(null);
        if (ticket == null && bookingRepository.existsById(bookingId)
                && !bookingRepository.existsByBookingIdAndStudent_UserId(bookingId, requestingUserId)) {
            throw new UnauthorizedException("You do not own this booking");
        }
        if (ticket == null) {
            return null;
        }
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return null;
        }

        return toDTO(ticket, booking);
    }

    @Override
    public Ticket getTicketByCode(String ticketCode) {
        return ticketRepository.findByTicketCode(ticketCode).orElse(null);
    }

    @Override
    public Ticket markTicketUsed(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode).orElse(null);
        if (ticket == null) {
            return null;
        }

        ticket.markAsUsed();
        return ticketRepository.save(ticket);
    }

    private TicketDTO toDTO(Ticket ticket, Booking booking) {
        Event event = booking.getEvent();
        String eventName = event != null ? event.getName() : null;
        String eventDateTime = event != null ? event.getDateTime() : null;
        String venue = (event != null && event.getVenue() != null)
                ? event.getVenue().getVenueName() : null;
        String studentName = booking.getStudent() != null ? booking.getStudent().getName() : null;

        return new TicketDTO(
                ticket.getTicketId(),
                ticket.getTicketCode(),
                ticket.getIssueDate(),
                ticket.isUsed(),
                eventName,
                eventDateTime,
                venue,
                studentName
        );
    }
}
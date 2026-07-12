package za.ac.cput.service;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *title: Service Milestone
 * Date: 12 July 2026
 **/

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Ticket;
import za.ac.cput.repository.TicketRepository;

@Service
public class TicketService implements ITicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
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
    public Ticket read(Integer integer) {
        return ticketRepository.findById(integer).orElse(null);
    }

    @Override
    public Ticket update(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Integer integer) {
        ticketRepository.deleteById(integer);
    }
}
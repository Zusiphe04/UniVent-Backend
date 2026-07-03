package za.ac.cput.service;

import za.ac.cput.domain.Ticket;

public interface ITicketService extends IService<Ticket, Integer> {
    Ticket findByBookingId(int bookingId);
}
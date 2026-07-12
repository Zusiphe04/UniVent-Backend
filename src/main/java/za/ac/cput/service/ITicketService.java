package za.ac.cput.service;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *title: Service Milestone
 * Date: 12 July 2026
 **/

import za.ac.cput.domain.Ticket;

public interface ITicketService extends IService<Ticket, Integer> {
    Ticket findByBookingId(int bookingId);
}
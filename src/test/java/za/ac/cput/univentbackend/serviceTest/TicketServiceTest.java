package za.ac.cput.univentbackend.serviceTest;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *title: Service Milestone
 * Date: 12 July 2026
 **/

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.ac.cput.domain.Ticket;
import za.ac.cput.repository.TicketRepository;
import za.ac.cput.service.TicketService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository repository;

    @InjectMocks
    private TicketService service;

    @Test
    void testCreateTicket() {
        Ticket ticket = new Ticket.Builder()
                .setBookingId(1)
                .setIssueDate(20260705)
                .setTicketCode(1001)
                .build();

        when(repository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Ticket result = service.create(ticket);

        assertNotNull(result);
        assertEquals(1001, result.getTicketCode());
        verify(repository).save(any(Ticket.class));
    }

    @Test
    void testReadTicket() {
        Ticket ticket = new Ticket.Builder()
                .setBookingId(2)
                .setIssueDate(20260706)
                .setTicketCode(2002)
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(ticket));

        Ticket result = service.read(1);

        assertNotNull(result);
        assertEquals(2002, result.getTicketCode());
    }

    @Test
    void testUpdateTicket() {
        when(repository.save(any(Ticket.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Ticket updated = new Ticket.Builder()
                .setBookingId(3)
                .setIssueDate(20260707)
                .setTicketCode(3003)
                .build();

        Ticket result = service.update(updated);

        assertNotNull(result);
        assertEquals(3003, result.getTicketCode());
        verify(repository).save(any(Ticket.class));
    }

    @Test
    void testDeleteTicket() {
        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void testCreateNullTicketReturnsNull() {
        Ticket result = service.create(null);
        assertNull(result);
    }

    @Test
    void testFindByBookingId() {
        Ticket ticket = new Ticket.Builder()
                .setBookingId(4)
                .setIssueDate(20260708)
                .setTicketCode(4004)
                .build();

        when(repository.findByBookingId(4)).thenReturn(Optional.of(ticket));

        Ticket result = service.findByBookingId(4);

        assertNotNull(result);
        assertEquals(4004, result.getTicketCode());
    }
}
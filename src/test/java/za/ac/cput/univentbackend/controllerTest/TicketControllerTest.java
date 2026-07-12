package za.ac.cput.univentbackend.controllerTest;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 * Date: July 2026
 **/
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.ac.cput.controller.TicketController;
import za.ac.cput.domain.Ticket;
import za.ac.cput.service.TicketService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {

    @Mock
    private TicketService service;

    @InjectMocks
    private TicketController controller;

    @Test
    public void testCreateTicket() {

        Ticket ticket = new Ticket.Builder()
                .setBookingId(1)
                .setIssueDate(20260815)
                .setTicketCode(1001)
                .build();

        when(service.create(ticket)).thenReturn(ticket);

        ResponseEntity<Ticket> response = controller.createTicket(ticket);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getBookingId());
        assertEquals(1001, response.getBody().getTicketCode());

        verify(service).create(ticket);
    }

    @Test
    public void testGetTicketById() {

        Ticket ticket = new Ticket.Builder()
                .setBookingId(2)
                .setIssueDate(20260816)
                .setTicketCode(2002)
                .build();

        when(service.read(1)).thenReturn(ticket);

        ResponseEntity<Ticket> response = controller.getTicketById(1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(2002, response.getBody().getTicketCode());

        verify(service).read(1);
    }

    @Test
    public void testUpdateTicket() {

        Ticket ticket = new Ticket.Builder()
                .setBookingId(3)
                .setIssueDate(20260817)
                .setTicketCode(3003)
                .build();

        when(service.update(ticket)).thenReturn(ticket);

        ResponseEntity<Ticket> response = controller.updateTicket(ticket);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getBookingId());
        assertEquals(3003, response.getBody().getTicketCode());

        verify(service).update(ticket);
    }

    @Test
    public void testDeleteTicket() {

        ResponseEntity<Void> response = controller.delete(1);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());

        verify(service).delete(1);
    }
}

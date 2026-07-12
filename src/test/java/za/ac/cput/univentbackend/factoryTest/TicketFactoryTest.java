package za.ac.cput.univentbackend.factoryTest;
/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 * Date:  July 2026
 **/
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Ticket;
import za.ac.cput.factory.TicketFactory;

import static org.junit.jupiter.api.Assertions.*;

public class TicketFactoryTest {

    @Test
    public void shouldCreateValidTicket() {

        Ticket ticket = TicketFactory.createTicket(
                1,
                20260815,
                1001
        );

        assertNotNull(ticket);
        assertEquals(1, ticket.getBookingId());
        assertEquals(20260815, ticket.getIssueDate());
        assertEquals(1001, ticket.getTicketCode());
    }

    @Test
    public void shouldThrowIfBookingIdIsZero() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        0,
                        20260815,
                        1001));

        assertEquals("Booking ID is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfBookingIdIsNegative() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        -1,
                        20260815,
                        1001));

        assertEquals("Booking ID is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfIssueDateIsZero() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        0,
                        1001));

        assertEquals("Issue date is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfIssueDateIsNegative() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        -20260815,
                        1001));

        assertEquals("Issue date is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfTicketCodeIsZero() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        20260815,
                        0));

        assertEquals("Ticket code is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfTicketCodeIsNegative() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                TicketFactory.createTicket(
                        1,
                        20260815,
                        -1001));

        assertEquals("Ticket code is required", exception.getMessage());
    }
}
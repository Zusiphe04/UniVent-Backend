package za.ac.cput.factory;
import za.ac.cput.domain.Ticket;
import za.ac.cput.util.Helper;

public class TicketFactory {
    public static Ticket createTicket(int bookingId,
                                      int issueDate,
                                      int ticketCode) {
        if (!Helper.isPositive(bookingId)) {
            throw new IllegalArgumentException("Booking ID is required");
        }
        if (!Helper.isPositive(issueDate)) {
            throw new IllegalArgumentException("Issue date is required");
        }
        if (!Helper.isPositive(ticketCode)) {
            throw new IllegalArgumentException("Ticket code is required");
        }
        return new Ticket.Builder()
                .setBookingId(bookingId)
                .setIssueDate(issueDate)
                .setTicketCode(ticketCode)
                .build();
    }
}
package za.ac.cput.domain;

import jakarta.persistence.*;

/**
 *Name: Zusiphe
 *Surname: Mvovo
 *Student number: 230816851
 *Description: Ticket
 *Domain class - Spring Boot / JPA version
 **/

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ticketId;
    private int bookingId;
    private int issueDate;
    private int ticketCode;

    protected Ticket() {
        /* required by JPA */
    }

    private Ticket(Builder builder) {
        this.ticketId = builder.ticketId;
        this.bookingId = builder.bookingId;
        this.issueDate = builder.issueDate;
        this.ticketCode = builder.ticketCode;
    }

    public int getTicketId() {
        return ticketId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getIssueDate() {
        return issueDate;
    }

    public int getTicketCode() {
        return ticketCode;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", bookingId=" + bookingId +
                ", issueDate=" + issueDate +
                ", ticketCode=" + ticketCode +
                '}';
    }

    public static class Builder {
        private int ticketId;
        private int bookingId;
        private int issueDate;
        private int ticketCode;

        public Builder setTicketId(int ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder setBookingId(int bookingId) {
            this.bookingId = bookingId;
            return this;
        }

        public Builder setIssueDate(int issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder setTicketCode(int ticketCode) {
            this.ticketCode = ticketCode;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}
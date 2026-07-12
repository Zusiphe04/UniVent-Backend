package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    protected Booking() {}

    private Booking(Builder builder) {
        this.bookingReference = builder.bookingReference;
        this.bookingTime = builder.bookingTime;
        this.lastUpdated = builder.lastUpdated;
        this.status = builder.status;
        this.student = builder.student;
        this.event = builder.event;
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public String getBookingReference() { return bookingReference; }
    public LocalDateTime getBookingTime() { return bookingTime; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public BookingStatusEnum getStatus() { return status; }
    public Student getStudent() { return student; }
    public Event getEvent() { return event; }

    // Business methods
    public void cancel() {
        if (this.status == BookingStatusEnum.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        if (this.status == BookingStatusEnum.CONFIRMED) {
            // If you want to allow cancellation of confirmed bookings
            // You can remove this check or keep it based on business rules
        }
        this.status = BookingStatusEnum.CANCELLED;
        this.lastUpdated = LocalDateTime.now();
    }

    public void confirm() {
        if (this.status == BookingStatusEnum.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled booking");
        }
        this.status = BookingStatusEnum.CONFIRMED;
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == BookingStatusEnum.CONFIRMED || this.status == BookingStatusEnum.PENDING;
    }

    public boolean isPending() {
        return this.status == BookingStatusEnum.PENDING;
    }

    public boolean isConfirmed() {
        return this.status == BookingStatusEnum.CONFIRMED;
    }

    public boolean isCancelled() {
        return this.status == BookingStatusEnum.CANCELLED;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public static class Builder {
        private String bookingReference;
        private LocalDateTime bookingTime;
        private LocalDateTime lastUpdated;
        private BookingStatusEnum status;
        private Student student;
        private Event event;

        public Builder() {
            this.bookingReference = generateBookingReference();
            this.bookingTime = LocalDateTime.now();
            this.lastUpdated = LocalDateTime.now();
            this.status = BookingStatusEnum.PENDING;
        }

        public Builder setBookingReference(String bookingReference) {
            this.bookingReference = bookingReference;
            return this;
        }

        public Builder setBookingTime(LocalDateTime bookingTime) {
            this.bookingTime = bookingTime;
            return this;
        }

        public Builder setLastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder setStatus(BookingStatusEnum status) {
            this.status = status;
            return this;
        }

        public Builder setStudent(Student student) {
            this.student = student;
            return this;
        }

        public Builder setEvent(Event event) {
            this.event = event;
            return this;
        }

        private String generateBookingReference() {
            return "BKG-" + System.currentTimeMillis() + "-" +
                    String.format("%04d", (int)(Math.random() * 10000));
        }

        public Booking build() {
            validate();
            return new Booking(this);
        }

        private void validate() {
            if (student == null) {
                throw new IllegalArgumentException("Student cannot be null");
            }
            if (event == null) {
                throw new IllegalArgumentException("Event cannot be null");
            }
            if (bookingTime == null) {
                throw new IllegalArgumentException("Booking time cannot be null");
            }
            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }
        }
    }
}
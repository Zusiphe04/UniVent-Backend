package za.ac.cput.factory;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.BookingStatusEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BookingFactory {

    public static Booking createBooking(Student student, Event event) {
        return new Booking.Builder()
                .setStudent(student)
                .setEvent(event)
                .setBookingTime(LocalDateTime.now())
                .setStatus(BookingStatusEnum.PENDING)
                .setLastUpdated(LocalDateTime.now())
                .build();
    }

    public static Booking createBookingWithStatus(Student student, Event event, BookingStatusEnum status) {
        return new Booking.Builder()
                .setStudent(student)
                .setEvent(event)
                .setBookingTime(LocalDateTime.now())
                .setStatus(status)
                .setLastUpdated(LocalDateTime.now())
                .build();
    }

    public static Booking createBookingWithReference(String bookingReference, Student student,
                                                     Event event, BookingStatusEnum status,
                                                     LocalDateTime bookingTime) {
        return new Booking.Builder()
                .setBookingReference(bookingReference)
                .setStudent(student)
                .setEvent(event)
                .setBookingTime(bookingTime)
                .setStatus(status)
                .setLastUpdated(LocalDateTime.now())
                .build();
    }
}
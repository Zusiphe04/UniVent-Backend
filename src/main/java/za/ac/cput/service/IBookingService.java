package za.ac.cput.service;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.BookingStatusEnum;

import java.util.List;
import java.util.Optional;

public interface IBookingService {
    Booking registerForEvent(Student student, Event event);
    Booking cancelBooking(String bookingReference);
    Booking confirmBooking(String bookingReference);
    Optional<Booking> getBookingById(int id);
    Optional<Booking> getBookingByReference(String bookingReference);
    List<Booking> getAllBookings();
    List<Booking> getBookingsByStudent(Student student);
    List<Booking> getBookingsByEvent(Event event);
    List<Booking> getBookingsByStatus(BookingStatusEnum status);
    void deleteBooking(String bookingReference);
    void deleteBookingById(int id);
    boolean hasStudentBookedEvent(Student student, Event event);
    long getBookingCountForEvent(Event event);
}
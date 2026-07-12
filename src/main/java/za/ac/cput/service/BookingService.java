package za.ac.cput.service;

import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.BookingStatusEnum;
import za.ac.cput.factory.BookingFactory;
import za.ac.cput.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingFactory bookingFactory;

    public Booking registerForEvent(Student student, Event event) {
        // Business rule: Check for duplicate booking
        if (hasStudentBookedEvent(student, event)) {
            throw new IllegalStateException("Student already has a booking for this event");
        }

        // Business rule: Check event capacity
        long currentBookings = getBookingCountForEvent(event);
        if (currentBookings >= event.getMaxAttendees()) {
            throw new IllegalStateException("Event has reached maximum capacity");
        }

        // Business rule: Check if event is in the future
        LocalDateTime eventDateTime = LocalDateTime.parse(event.getDateTime());
        if (eventDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot register for past events");
        }

        // Create booking using factory
        Booking booking = bookingFactory.createBooking(student, event);

        // Save booking
        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));

        // Business rule: Check if booking can be cancelled
        if (booking.getStatus() == BookingStatusEnum.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        // Cancel booking
        booking.cancel();

        // Update repository
        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));

        // Business rule: Cannot confirm a cancelled booking
        if (booking.getStatus() == BookingStatusEnum.CANCELLED) {
            throw new IllegalStateException("Cannot confirm a cancelled booking");
        }

        booking.confirm();
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingById(int id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingRepository.findByBookingReference(bookingReference);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByStudent(Student student) {
        return bookingRepository.findByStudent(student);
    }

    public List<Booking> getBookingsByEvent(Event event) {
        return bookingRepository.findByEvent(event);
    }

    public List<Booking> getBookingsByStatus(BookingStatusEnum status) {
        return bookingRepository.findByStatus(status);
    }

    public void deleteBooking(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with reference: " + bookingReference));

        bookingRepository.deleteByBookingReference(bookingReference);
    }

    public void deleteBookingById(int id) {
        if (!bookingRepository.existsById(id)) {
            throw new IllegalArgumentException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    public boolean hasStudentBookedEvent(Student student, Event event) {
        return bookingRepository.existsByStudentAndEvent(student, event);
    }

    public long getBookingCountForEvent(Event event) {
        return bookingRepository.countByEvent(event);
    }
}
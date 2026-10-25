package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.BookingStatusEnum;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // Find booking by booking reference
    Optional<Booking> findByBookingReference(String bookingReference);
    boolean existsByBookingReference(String bookingReference);
    Optional<Booking> findByBookingReferenceAndStudent_UserId(String bookingReference, String userId);
    Optional<Booking> findByBookingIdAndStudent_UserId(int bookingId, String userId);
    boolean existsByBookingIdAndStudent_UserId(int bookingId, String userId);
    List<Booking> findByStudent_UserIdOrderByBookingTimeDesc(String userId);
    boolean existsByStudent_UserIdAndEvent_EventId(String userId, int eventId);
    List<Booking> findByEvent_EventIdOrderByBookingTimeDesc(int eventId);
    List<Booking> findByEvent_EventIdAndEvent_Organizer_UserIdOrderByBookingTimeDesc(int eventId, String organizerId);

    // Find bookings by student
    List<Booking> findByStudent(Student student);

    // Find bookings by event
    List<Booking> findByEvent(Event event);

    // Find bookings by status
    List<Booking> findByStatus(BookingStatusEnum status);

    // Check if booking exists for a student and event
    boolean existsByStudentAndEvent(Student student, Event event);

    // Count bookings for an event
    long countByEvent(Event event);

    @Query("select count(b) from Booking b")
    long countAllBookings();

    // Delete booking by reference
    void deleteByBookingReference(String bookingReference);
}
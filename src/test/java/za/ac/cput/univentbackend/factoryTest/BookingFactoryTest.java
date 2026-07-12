package za.ac.cput.univentbackend.factoryTest;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.BookingStatusEnum;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Student;
import za.ac.cput.factory.BookingFactory;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BookingFactoryTest {

    private final Student student = mock(Student.class);
    private final Event event = mock(Event.class);

    @Test
    public void shouldCreateValidBooking() {
        // When
        Booking booking = BookingFactory.createBooking(student, event);

        // Then
        assertNotNull(booking);
        assertNotNull(booking.getBookingReference());
        assertTrue(booking.getBookingReference().startsWith("BKG-"));
        assertEquals(student, booking.getStudent());
        assertEquals(event, booking.getEvent());
        assertEquals(BookingStatusEnum.PENDING, booking.getStatus());
        assertNotNull(booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
    }

    @Test
    public void shouldCreateBookingWithStatus() {
        // When
        Booking booking = BookingFactory.createBookingWithStatus(
                student, event, BookingStatusEnum.CONFIRMED
        );

        // Then
        assertNotNull(booking);
        assertNotNull(booking.getBookingReference());
        assertEquals(student, booking.getStudent());
        assertEquals(event, booking.getEvent());
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
        assertNotNull(booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
    }

    @Test
    public void shouldCreateBookingWithStatusCancelled() {
        // When
        Booking booking = BookingFactory.createBookingWithStatus(
                student, event, BookingStatusEnum.CANCELLED
        );

        // Then
        assertNotNull(booking);
        assertEquals(student, booking.getStudent());
        assertEquals(event, booking.getEvent());
        assertEquals(BookingStatusEnum.CANCELLED, booking.getStatus());
        assertNotNull(booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
    }

    @Test
    public void shouldCreateBookingWithReference() {
        // Given
        String bookingReference = "BKG-TEST-001";
        LocalDateTime bookingTime = LocalDateTime.now().minusDays(1);

        // When
        Booking booking = BookingFactory.createBookingWithReference(
                bookingReference, student, event, BookingStatusEnum.CONFIRMED, bookingTime
        );

        // Then
        assertNotNull(booking);
        assertEquals(bookingReference, booking.getBookingReference());
        assertEquals(student, booking.getStudent());
        assertEquals(event, booking.getEvent());
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
        assertEquals(bookingTime, booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
    }

    @Test
    public void shouldThrowIfStudentIsNull() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBooking(null, event));

        assertEquals("Student cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfEventIsNull() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBooking(student, null));

        assertEquals("Event cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfStudentIsNullWithStatus() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithStatus(null, event, BookingStatusEnum.PENDING));

        assertEquals("Student cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfEventIsNullWithStatus() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithStatus(student, null, BookingStatusEnum.PENDING));

        assertEquals("Event cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfStatusIsNull() {
        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithStatus(student, event, null));

        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfStudentIsNullWithReference() {
        // Given
        String reference = "BKG-TEST-002";
        LocalDateTime time = LocalDateTime.now();

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithReference(reference, null, event,
                        BookingStatusEnum.PENDING, time));

        assertEquals("Student cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfEventIsNullWithReference() {
        // Given
        String reference = "BKG-TEST-002";
        LocalDateTime time = LocalDateTime.now();

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithReference(reference, student, null,
                        BookingStatusEnum.PENDING, time));

        assertEquals("Event cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfStatusIsNullWithReference() {
        // Given
        String reference = "BKG-TEST-002";
        LocalDateTime time = LocalDateTime.now();

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithReference(reference, student, event,
                        null, time));

        assertEquals("Status cannot be null", exception.getMessage());
    }

    @Test
    public void shouldThrowIfBookingTimeIsNull() {
        // Given
        String reference = "BKG-TEST-002";

        // When & Then
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                BookingFactory.createBookingWithReference(reference, student, event,
                        BookingStatusEnum.PENDING, null));

        assertEquals("Booking time cannot be null", exception.getMessage());
    }

    @Test
    public void shouldGenerateUniqueBookingReferences() {
        // Given
        Booking booking1 = BookingFactory.createBooking(student, event);
        Booking booking2 = BookingFactory.createBooking(student, event);

        // Then
        assertNotNull(booking1.getBookingReference());
        assertNotNull(booking2.getBookingReference());
        assertNotEquals(booking1.getBookingReference(), booking2.getBookingReference());
        assertTrue(booking1.getBookingReference().startsWith("BKG-"));
        assertTrue(booking2.getBookingReference().startsWith("BKG-"));
    }

    @Test
    public void shouldSetCurrentDateTimeForBooking() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        Booking booking = BookingFactory.createBooking(student, event);

        // Then
        assertNotNull(booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
        assertTrue(booking.getBookingTime().isAfter(beforeCreation) ||
                booking.getBookingTime().equals(beforeCreation));
        assertTrue(booking.getLastUpdated().isAfter(beforeCreation) ||
                booking.getLastUpdated().equals(beforeCreation));
    }

    @Test
    public void shouldSetLastUpdatedForBookingWithReference() {
        // Given
        String reference = "BKG-TEST-003";
        LocalDateTime bookingTime = LocalDateTime.now().minusDays(2);
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        Booking booking = BookingFactory.createBookingWithReference(
                reference, student, event, BookingStatusEnum.PENDING, bookingTime
        );

        // Then
        assertNotNull(booking);
        assertEquals(reference, booking.getBookingReference());
        assertEquals(bookingTime, booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
        assertTrue(booking.getLastUpdated().isAfter(beforeCreation) ||
                booking.getLastUpdated().equals(beforeCreation));
    }

    @Test
    public void shouldCreateBookingWithReferenceFormat() {
        // When
        Booking booking = BookingFactory.createBooking(student, event);
        String reference = booking.getBookingReference();

        // Then
        assertNotNull(reference);
        assertTrue(reference.startsWith("BKG-"));
        assertTrue(reference.length() > 10);
        assertTrue(reference.matches("BKG-\\d+-\\d{4}"));
    }

    @Test
    public void shouldCreateBookingWithPendingStatusByDefault() {
        // When
        Booking booking = BookingFactory.createBooking(student, event);

        // Then
        assertEquals(BookingStatusEnum.PENDING, booking.getStatus());
    }

    @Test
    public void shouldCreateBookingWithConfirmedStatus() {
        // When
        Booking booking = BookingFactory.createBookingWithStatus(
                student, event, BookingStatusEnum.CONFIRMED
        );

        // Then
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
    }

    @Test
    public void shouldCreateBookingWithCancelledStatus() {
        // When
        Booking booking = BookingFactory.createBookingWithStatus(
                student, event, BookingStatusEnum.CANCELLED
        );

        // Then
        assertEquals(BookingStatusEnum.CANCELLED, booking.getStatus());
    }

    @Test
    public void shouldSetAllFieldsForBooking() {
        // Given
        String reference = "BKG-TEST-004";
        LocalDateTime bookingTime = LocalDateTime.now().minusHours(5);

        // When
        Booking booking = BookingFactory.createBookingWithReference(
                reference, student, event, BookingStatusEnum.CONFIRMED, bookingTime
        );

        // Then
        assertNotNull(booking);
        assertEquals(reference, booking.getBookingReference());
        assertEquals(student, booking.getStudent());
        assertEquals(event, booking.getEvent());
        assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
        assertEquals(bookingTime, booking.getBookingTime());
        assertNotNull(booking.getLastUpdated());
    }

    @Test
    public void shouldCreateBookingWithDifferentStudentsAndEvents() {
        // Given
        Student student2 = mock(Student.class);
        Event event2 = mock(Event.class);

        // When
        Booking booking1 = BookingFactory.createBooking(student, event);
        Booking booking2 = BookingFactory.createBooking(student2, event2);

        // Then
        assertNotNull(booking1);
        assertNotNull(booking2);
        assertNotEquals(booking1.getStudent(), booking2.getStudent());
        assertNotEquals(booking1.getEvent(), booking2.getEvent());
        assertNotEquals(booking1.getBookingReference(), booking2.getBookingReference());
    }
}
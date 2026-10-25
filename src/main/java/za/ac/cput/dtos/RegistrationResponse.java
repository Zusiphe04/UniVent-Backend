package za.ac.cput.dtos;

import za.ac.cput.domain.Booking;

import java.time.Instant;
import java.time.LocalDateTime;

public record RegistrationResponse(
        int id,
        String bookingReference,
        String attendeeName,
        String email,
        String studentNumber,
        String status,
        String bookingTime
) {
    public static RegistrationResponse from(Booking booking) {
        return new RegistrationResponse(
                booking.getBookingId(),
                booking.getBookingReference(),
                booking.getStudent().getName(),
                booking.getStudent().getEmail(),
                booking.getStudent().getStudentNumber(),
                booking.getStatus().name(),
                booking.getBookingTime() == null ? null : booking.getBookingTime().toString()
        );
    }
}

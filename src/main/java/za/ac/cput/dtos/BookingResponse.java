package za.ac.cput.dtos;

import za.ac.cput.domain.Booking;
import za.ac.cput.util.IsoDateTime;

public record BookingResponse(
        int id,
        String bookingReference,
        String bookingTime,
        String lastUpdated,
        String status,
        int eventId,
        String eventTitle,
        String eventDateTime,
        String venueName
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getBookingReference(),
                booking.getBookingTime() == null ? null : booking.getBookingTime().toString(),
                booking.getLastUpdated() == null ? null : booking.getLastUpdated().toString(),
                booking.getStatus().name(),
                booking.getEvent().getEventId(),
                booking.getEvent().getName(),
                IsoDateTime.format(booking.getEvent().getDateTime()),
                booking.getEvent().getVenue() == null ? null : booking.getEvent().getVenue().getVenueName()
        );
    }
}

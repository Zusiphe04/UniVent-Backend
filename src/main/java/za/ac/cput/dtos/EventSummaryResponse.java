package za.ac.cput.dtos;

import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;

/** Stable event read model shared with the frontend. */
public record EventSummaryResponse(
        int id,
        String title,
        String description,
        String dateTime,
        String endDateTime,
        String venueName,
        String category,
        String imageUrl,
        int maxAttendees,
        EventStatusEnum status
) {
    public static EventSummaryResponse from(Event event) {
        String venueName = event.getVenue() == null ? null : event.getVenue().getVenueName();
        return new EventSummaryResponse(
                event.getEventId(), event.getName(), event.getDescription(),
                event.getDateTime(), null, venueName, null, null,
                event.getMaxAttendees(), event.getStatus()
        );
    }
}

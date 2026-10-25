package za.ac.cput.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EventUpsertRequest(
        @NotBlank(message = "Event title is required") @Size(max = 255) String title,
        @Size(max = 5000) String description,
        @NotBlank(message = "Event date and time are required") String dateTime,
        String endDateTime,
        @NotBlank(message = "Venue is required") String venueId,
        @Size(max = 100) String category,
        @Size(max = 2048) String imageUrl,
        @Min(value = 1, message = "Maximum attendees must be greater than 0") int maxAttendees
) {
}

package za.ac.cput.dtos;

public record OrganizerEventSummaryDTO(
        String id,
        String name,
        String dateTime,
        String venueName,
        String status,
        long registrationCount
) {
}

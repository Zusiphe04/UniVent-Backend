package za.ac.cput.dtos;

public record AdminEventSummaryDTO(
        String id,
        String name,
        String title,
        String description,
        String organizerName,
        String dateTime,
        String venueName,
        String category,
        String status,
        long registrationCount
) {
}

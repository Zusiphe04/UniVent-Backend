package za.ac.cput.dtos;

public record AdminEventSummaryDTO(
        String id,
        String name,
        String organizerName,
        String dateTime,
        String status,
        long registrationCount
) {
}

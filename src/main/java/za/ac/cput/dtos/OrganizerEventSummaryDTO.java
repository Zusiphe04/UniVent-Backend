package za.ac.cput.dtos;

public record OrganizerEventSummaryDTO(
        String id,
        String name,
        String title,
        String category,
        String dateTime,
        String venueName,
        String status,
        long registrationCount,
        int capacity
) {
    public OrganizerEventSummaryDTO(String id, String name, String dateTime,
                                    String venueName, String status, long registrationCount) {
        this(id, name, name, null, dateTime, venueName, status, registrationCount, 0);
    }
}

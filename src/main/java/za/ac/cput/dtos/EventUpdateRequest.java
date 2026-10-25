package za.ac.cput.dtos;

/**
 * Permitted fields for {@code PUT /api/events/{id}}.
 * Organizer, status and eventId are intentionally absent so they cannot be bound from the body.
 * Frontend aliases ({@code title}, {@code startsAt}, {@code capacity}) are accepted for compatibility.
 */
public class EventUpdateRequest {
    private String name;
    private String title;
    private String description;
    private String dateTime;
    private String startsAt;
    private Integer maxAttendees;
    private Integer capacity;
    private String venueId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public Integer getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(Integer maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String resolvedName() {
        return firstNonBlank(name, title);
    }

    public String resolvedDateTime() {
        return firstNonBlank(dateTime, startsAt);
    }

    public Integer resolvedMaxAttendees() {
        return maxAttendees != null ? maxAttendees : capacity;
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return null;
    }
}

package za.ac.cput.domain;

import jakarta.persistence.*;

/**Student name: Uyathandwa Ngomana
 * Student number: 231173229
 * Group: 3H
 * Date: 05 July 2026
 * **/

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;
    private String name;
    @Column(length = 5000)
    private String description;
    private String dateTime;
    private String endDateTime;
    private String category;
    @Column(length = 2048)
    private String imageUrl;
    private int maxAttendees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatusEnum status = EventStatusEnum.UPCOMING;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @ManyToOne
    @JoinColumn(name = "venue_id", columnDefinition = "varchar(36)")
    private Venue venue;

    protected Event() {}

    private Event(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.dateTime = builder.dateTime;
        this.endDateTime = builder.endDateTime;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.maxAttendees = builder.maxAttendees;
        this.status = builder.status;
        this.organizer = builder.organizer;
        this.venue = builder.venue;
    }

    public int getEventId() {
        return eventId; }
    public String getName() {
        return name; }
    public String getDescription() {
        return description; }
    public String getDateTime() {
        return dateTime; }
    public String getEndDateTime() { return endDateTime; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public int getMaxAttendees() {
        return maxAttendees; }
    public EventStatusEnum getStatus() {
        return status; }
    public Organizer getOrganizer() {
        return organizer; }
    public Venue getVenue() {
        return venue; }

    public void setStatus(EventStatusEnum status) {
        this.status = status;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }
    public void setVenue(Venue venue) { this.venue = venue; }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", maxAttendees=" + maxAttendees +
                ", organizer=" + organizer +
                ", venue=" + venue + '}';
    }



    public static class Builder {
        private String name;
        private String description;
        private String dateTime;
        private String endDateTime;
        private String category;
        private String imageUrl;
        private int maxAttendees;
        private EventStatusEnum status = EventStatusEnum.UPCOMING;
        private Organizer organizer;
        private Venue venue;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        public Builder setDateTime(String dateTime) {
            this.dateTime = dateTime;
            return this;
        }
        public Builder setEndDateTime(String endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }
        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }
        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        public Builder setMaxAttendees(int maxAttendees) {
            this.maxAttendees = maxAttendees;
            return this;
        }
        public Builder setStatus(EventStatusEnum status) {
            this.status = status;
            return this;
        }
        public Builder setOrganizer(Organizer organizer) {
            this.organizer = organizer;
            return this;
        }
        public Builder setVenue(Venue venue) {
            this.venue = venue;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }
}

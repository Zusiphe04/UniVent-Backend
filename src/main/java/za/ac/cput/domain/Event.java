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
    private String description;
    private String dateTime;
    private int maxAttendees;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    protected Event() {}

    private Event(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.dateTime = builder.dateTime;
        this.maxAttendees = builder.maxAttendees;
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
    public int getMaxAttendees() {
        return maxAttendees; }
    public Organizer getOrganizer() {
        return organizer; }
    public Venue getVenue() {
        return venue; }

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
        private int maxAttendees;
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
        public Builder setMaxAttendees(int maxAttendees) {
            this.maxAttendees = maxAttendees;
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
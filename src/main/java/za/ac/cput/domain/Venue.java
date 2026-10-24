package za.ac.cput.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 *Name: Sesethu
 *Surname: Nciti
 *Student number: 231118384
 *Description: Venue
 **/
@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @Column(length = 36)
    private String venueId;

    private String venueName;
    private String campus;
    private String locationDetails;
    private int capacity;

    @OneToMany(mappedBy = "venue")
    private List<Event> events;

    protected Venue() {
    }

    private Venue(Builder builder) {
        this.venueId = builder.venueId;
        this.venueName = builder.venueName;
        this.campus = builder.campus;
        this.locationDetails = builder.locationDetails;
        this.capacity = builder.capacity;
        this.events = builder.events;
    }

    @PrePersist
    protected void onCreate() {
        if (this.venueId == null || this.venueId.isEmpty()) {
            this.venueId = UUID.randomUUID().toString();
        }
    }

    public String getVenueId() {
        return venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getCampus() {
        return campus;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Event> getEvents() {
        return events;
    }

    public boolean checkAvailability(LocalDateTime dateTime) {
        if (this.events == null) {
            return true;
        }
        return this.events.stream()
                .noneMatch(event -> event.getDateTime() != null
                        && event.getDateTime().equals(dateTime));
    }

    public void updateVenueDetails() {
        // Intended to be handled via the Builder + Service update flow
        // (kept here to satisfy the UML contract; actual persistence
        // update logic lives in VenueService.update()).
    }

    @Override
    public String toString() {
        return "Venue{" +
                "venueId='" + venueId + '\'' +
                ", venueName='" + venueName + '\'' +
                ", campus='" + campus + '\'' +
                ", locationDetails='" + locationDetails + '\'' +
                ", capacity=" + capacity +
                '}';
    }

    public static class Builder {

        private String venueId;
        private String venueName;
        private String campus;
        private String locationDetails;
        private int capacity;
        private List<Event> events;

        public Builder setVenueId(String venueId) {
            this.venueId = venueId;
            return this;
        }

        public Builder setVenueName(String venueName) {
            this.venueName = venueName;
            return this;
        }

        public Builder setCampus(String campus) {
            this.campus = campus;
            return this;
        }

        public Builder setLocationDetails(String locationDetails) {
            this.locationDetails = locationDetails;
            return this;
        }

        public Builder setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder setEvents(List<Event> events) {
            this.events = events;
            return this;
        }

        public Builder copy(Venue venue) {
            this.venueId = venue.venueId;
            this.venueName = venue.venueName;
            this.campus = venue.campus;
            this.locationDetails = venue.locationDetails;
            this.capacity = venue.capacity;
            this.events = venue.events;
            return this;
        }

        public Venue build() {
            return new Venue(this);
        }
    }
}

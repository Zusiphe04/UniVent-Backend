package za.ac.cput.domain;

import jakarta.persistence.*;
import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int venueId;

    private String venueName;
    private String address;
    private int capacity;

    @OneToMany(mappedBy = "venue")
    private List<Event> events;

    protected Venue() {
    }

    private Venue(Builder builder) {
        this.venueId = builder.venueId;
        this.venueName = builder.venueName;
        this.address = builder.address;
        this.capacity = builder.capacity;
        this.events = builder.events;
    }

    public int getVenueId() {
        return venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getAddress() {
        return address;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "Venue{" +
                "venueId=" + venueId +
                ", venueName='" + venueName + '\'' +
                ", address='" + address + '\'' +
                ", capacity=" + capacity +
                '}';
    }

    public static class Builder {

        private int venueId;
        private String venueName;
        private String address;
        private int capacity;
        private List<Event> events;

        public Builder setVenueId(int venueId) {
            this.venueId = venueId;
            return this;
        }

        public Builder setVenueName(String venueName) {
            this.venueName = venueName;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
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

        public Venue build() {
            return new Venue(this);
        }
    }
}

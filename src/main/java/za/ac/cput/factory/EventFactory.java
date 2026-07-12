package za.ac.cput.factory;

import za.ac.cput.domain.Event;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.Venue;
import za.ac.cput.util.Helper;

/**Student name: Uyathandwa Ngomana
 * Student number: 231173229
 * Group: 3H
 * Date: 05 July 2026
 * **/

public class EventFactory {

    public static Event createEvent(String name,
                                    String description,
                                    String dateTime,
                                    int maxAttendees,
                                    Organizer organizer,
                                    Venue venue) {

        if (Helper.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Event name is required");
        }

        if (Helper.isNullOrEmpty(description)) {
            throw new IllegalArgumentException("Description is required");
        }

        if (!Helper.isValidDateTime(dateTime)) {
            throw new IllegalArgumentException("Invalid date and time");
        }

        if (!Helper.isPositive(maxAttendees)) {
            throw new IllegalArgumentException("Maximum attendees must be greater than 0");
        }

        if (organizer == null) {
            throw new IllegalArgumentException("Organizer is required");
        }

        if (venue == null) {
            throw new IllegalArgumentException("Venue is required");
        }

        return new Event.Builder()
                .setName(name)
                .setDescription(description)
                .setDateTime(dateTime)
                .setMaxAttendees(maxAttendees)
                .setOrganizer(organizer)
                .setVenue(venue)
                .build();
    }
}
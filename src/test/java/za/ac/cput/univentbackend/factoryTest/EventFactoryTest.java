package za.ac.cput.univentbackend.factoryTest;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.Venue;
import za.ac.cput.factory.EventFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class EventFactoryTest {

    private final Organizer organizer = mock(Organizer.class);
    private final Venue venue = mock(Venue.class);

    @Test
    public void shouldCreateValidEvent() {

        Event event = EventFactory.createEvent(
                "Tech Talk",
                "Java Workshop",
                "2026-08-15T10:00:00",
                100,
                organizer,
                venue
        );

        assertNotNull(event);
        assertEquals("Tech Talk", event.getName());
        assertEquals("Java Workshop", event.getDescription());
        assertEquals("2026-08-15T10:00:00", event.getDateTime());
        assertEquals(100, event.getMaxAttendees());
        assertEquals(organizer, event.getOrganizer());
        assertEquals(venue, event.getVenue());
    }

    @Test
    public void shouldThrowIfNameIsEmpty() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "",
                        "Java Workshop",
                        "2026-08-15T10:00:00",
                        100,
                        organizer,
                        venue));

        assertEquals("Event name is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfNameIsBlank() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "   ",
                        "Java Workshop",
                        "2026-08-15T10:00:00",
                        100,
                        organizer,
                        venue));

        assertEquals("Event name is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfDescriptionIsEmpty() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "",
                        "2026-08-15T10:00:00",
                        100,
                        organizer,
                        venue));

        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfDescriptionIsBlank() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "   ",
                        "2026-08-15T10:00:00",
                        100,
                        organizer,
                        venue));

        assertEquals("Description is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfDateTimeIsInvalid() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "Java Workshop",
                        "",
                        100,
                        organizer,
                        venue));

        assertEquals("Invalid date and time", exception.getMessage());
    }

    @Test
    public void shouldThrowIfMaxAttendeesIsZero() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "Java Workshop",
                        "2026-08-15T10:00:00",
                        0,
                        organizer,
                        venue));

        assertEquals("Maximum attendees must be greater than 0", exception.getMessage());
    }

    @Test
    public void shouldThrowIfMaxAttendeesIsNegative() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "Java Workshop",
                        "2026-08-15T10:00:00",
                        -10,
                        organizer,
                        venue));

        assertEquals("Maximum attendees must be greater than 0", exception.getMessage());
    }

    @Test
    public void shouldThrowIfOrganizerIsNull() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "Java Workshop",
                        "2026-08-15T10:00:00",
                        100,
                        null,
                        venue));

        assertEquals("Organizer is required", exception.getMessage());
    }

    @Test
    public void shouldThrowIfVenueIsNull() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                EventFactory.createEvent(
                        "Tech Talk",
                        "Java Workshop",
                        "2026-08-15T10:00:00",
                        100,
                        organizer,
                        null));

        assertEquals("Venue is required", exception.getMessage());
    }
}


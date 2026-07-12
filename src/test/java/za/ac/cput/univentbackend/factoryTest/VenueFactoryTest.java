package za.ac.cput.univentbackend.factoryTest;

import org.junit.jupiter.api.Test;
import za.ac.cput.domain.Venue;
import za.ac.cput.factory.VenueFactory;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Student Name: Sesethu Nciti
 * Student Number: 231118384
 */

public class VenueFactoryTest {

    @Test
    public void testCreateVenue() {

        Venue venue = VenueFactory.createVenue(
                "CPUT Hall",
                "Bellville Campus",
                500,
                new ArrayList<>()
        );

        assertNotNull(venue);
        assertEquals("CPUT Hall", venue.getVenueName());
        assertEquals("Bellville Campus", venue.getAddress());
        assertEquals(500, venue.getCapacity());
    }

    @Test
    public void testCreateVenueWithNullName() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        null,
                        "Bellville Campus",
                        500,
                        new ArrayList<>()
                ));

        assertEquals("Venue name is required", exception.getMessage());
    }

    @Test
    public void testCreateVenueWithNullAddress() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        "CPUT Hall",
                        null,
                        500,
                        new ArrayList<>()
                ));

        assertEquals("Address is required", exception.getMessage());
    }

    @Test
    public void testCreateVenueWithInvalidCapacity() {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                VenueFactory.createVenue(
                        "CPUT Hall",
                        "Bellville Campus",
                        0,
                        new ArrayList<>()
                ));

        assertEquals("Capacity must be greater than 0", exception.getMessage());
    }
}
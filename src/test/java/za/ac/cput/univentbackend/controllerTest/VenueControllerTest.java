package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import za.ac.cput.controller.VenueController;
import za.ac.cput.domain.Venue;
import za.ac.cput.service.VenueService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Student Name: Sesethu Nciti
 * Student Number: 231118384
 */

@ExtendWith(MockitoExtension.class)
public class VenueControllerTest {

    @Mock
    private VenueService service;

    @InjectMocks
    private VenueController controller;

    @Test
    public void testCreateVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("CPUT Hall")
                .setAddress("Bellville Campus")
                .setCapacity(500)
                .build();

        when(service.create(venue)).thenReturn(venue);

        ResponseEntity<Venue> response = controller.createVenue(venue);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("CPUT Hall", response.getBody().getVenueName());
        assertEquals("Bellville Campus", response.getBody().getAddress());
        assertEquals(500, response.getBody().getCapacity());

        verify(service).create(venue);
    }

    @Test
    public void testGetVenueById() {

        Venue venue = new Venue.Builder()
                .setVenueName("Main Hall")
                .setAddress("Cape Town")
                .setCapacity(300)
                .build();

        when(service.read(1)).thenReturn(venue);

        ResponseEntity<Venue> response = controller.getVenueById(1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("Main Hall", response.getBody().getVenueName());

        verify(service).read(1);
    }

    @Test
    public void testUpdateVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("Updated Hall")
                .setAddress("District Six Campus")
                .setCapacity(700)
                .build();

        when(service.update(venue)).thenReturn(venue);

        ResponseEntity<Venue> response = controller.updateVenue(venue);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("Updated Hall", response.getBody().getVenueName());
        assertEquals("District Six Campus", response.getBody().getAddress());
        assertEquals(700, response.getBody().getCapacity());

        verify(service).update(venue);
    }

    @Test
    public void testDeleteVenue() {

        ResponseEntity<Void> response = controller.deleteVenue(1);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());

        verify(service).delete(1);
    }
}

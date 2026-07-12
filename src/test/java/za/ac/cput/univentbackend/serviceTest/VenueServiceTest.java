package za.ac.cput.univentbackend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.Venue;
import za.ac.cput.repository.VenueRepository;
import za.ac.cput.service.VenueService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Student Name: Sesethu Nciti
 * Student Number: 231118384
 */

@ExtendWith(MockitoExtension.class)
public class VenueServiceTest {

    @Mock
    private VenueRepository repository;

    @InjectMocks
    private VenueService service;

    @Test
    void testCreateVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("CPUT Hall")
                .setAddress("Bellville Campus")
                .setCapacity(500)
                .build();

        when(repository.save(venue)).thenReturn(venue);

        Venue created = service.create(venue);

        assertNotNull(created);
        assertEquals("CPUT Hall", created.getVenueName());
        assertEquals("Bellville Campus", created.getAddress());

        verify(repository).save(venue);
    }

    @Test
    void testReadVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("Main Hall")
                .setAddress("Cape Town")
                .setCapacity(300)
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(venue));

        Venue found = service.read(1);

        assertNotNull(found);
        assertEquals("Main Hall", found.getVenueName());

        verify(repository).findById(1);
    }

    @Test
    void testUpdateVenue() {

        Venue venue = new Venue.Builder()
                .setVenueName("Updated Hall")
                .setAddress("District Six")
                .setCapacity(700)
                .build();

        when(repository.save(venue)).thenReturn(venue);

        Venue updated = service.update(venue);

        assertNotNull(updated);
        assertEquals("Updated Hall", updated.getVenueName());
        assertEquals(700, updated.getCapacity());

        verify(repository).save(venue);
    }

    @Test
    void testDeleteVenue() {

        service.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void testCreateVenueWithNull() {

        Venue venue = service.create(null);

        assertNull(venue);

        verify(repository, never()).save(any(Venue.class));
    }

    @Test
    void testUpdateVenueWithNull() {

        Venue venue = service.update(null);

        assertNull(venue);

        verify(repository, never()).save(any(Venue.class));
    }
}
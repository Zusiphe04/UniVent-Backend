package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Venue;
import za.ac.cput.repository.VenueRepository;

/**
 * Name: Sesethu Nciti
 * Student Number: 231118384
 */

@Service
public class VenueService implements IVenueService {

    private final VenueRepository venueRepository;

    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Override
    public Venue findVenue(String venueName) {
        return null;
    }

    @Override
    public Venue create(Venue venue) {
        if (venue == null) {
            return null;
        }
        return venueRepository.save(venue);
    }

    @Override
    public Venue read(Integer integer) {
        return venueRepository.findById(integer).orElse(null);
    }

    @Override
    public Venue update(Venue venue) {
        return venueRepository.save(venue);
    }

    @Override
    public void delete(Integer integer) {
        venueRepository.deleteById(integer);
    }
}
package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Venue;

/**
 * Name: Sesethu Nciti
 * Student Number: 231118384
 */
@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {
}

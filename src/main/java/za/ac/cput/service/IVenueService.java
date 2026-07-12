package za.ac.cput.service;

import za.ac.cput.domain.Venue;

public interface IVenueService extends IService<Venue, Integer> {

    Venue findVenue(String venueName);

}
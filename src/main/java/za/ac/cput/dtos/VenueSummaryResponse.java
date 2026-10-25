package za.ac.cput.dtos;

import za.ac.cput.domain.Venue;

public record VenueSummaryResponse(
        String venueId,
        String venueName,
        String campus,
        String locationDetails,
        int capacity
) {
    public static VenueSummaryResponse from(Venue venue) {
        return new VenueSummaryResponse(
                venue.getVenueId(),
                venue.getVenueName(),
                venue.getCampus(),
                venue.getLocationDetails(),
                venue.getCapacity()
        );
    }
}

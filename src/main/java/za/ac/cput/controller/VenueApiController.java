package za.ac.cput.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.dtos.VenueSummaryResponse;
import za.ac.cput.repository.VenueRepository;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueApiController {
    private final VenueRepository venueRepository;

    public VenueApiController(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @GetMapping
    public List<VenueSummaryResponse> list() {
        return venueRepository.findAll().stream()
                .map(VenueSummaryResponse::from)
                .toList();
    }
}

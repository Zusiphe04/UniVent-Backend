package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Organizer;
import za.ac.cput.repository.OrganizerRepository;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@Service
public class OrganizerService implements IOrganizerService{
    private final OrganizerRepository repository;

    public OrganizerService(OrganizerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Organizer findOrganizer(String organizerNumber) {
        return null;
    }

    @Override
    public Organizer create(Organizer organizer) {
        if (organizer == null) {
            return null;
        }
        return repository.save(organizer);
    }

    @Override
    public Organizer read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Organizer update(Organizer organizer) {
        if (organizer == null) {
            return null;
        }
        return repository.save(organizer);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}

package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Administrator;
import za.ac.cput.repository.AdministratorRepository;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@Service
public class AdministratorService implements IAdministratorService{
    private final AdministratorRepository repository;

    public AdministratorService(AdministratorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Administrator findByAdministratorNumber(String administratorNumber) {
        return null;
    }

    @Override
    public Administrator create(Administrator administrator) {
        if (administrator == null) {
            return null;
        }
        return  repository.save(administrator);
    }

    @Override
    public Administrator read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Administrator update(Administrator administrator) {
        if(administrator == null) {
            return null;
        }
        return repository.save(administrator);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);

    }
}

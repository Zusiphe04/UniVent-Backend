package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Administrator;
import za.ac.cput.service.AdministratorService;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@RestController
@RequestMapping("/api/administrators")
public class AdministratorController {
    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService){
        this.administratorService = administratorService;
    }
    @PostMapping
    public ResponseEntity<Administrator> createAdministrator(@RequestBody Administrator administrator) {
        Administrator createdAdministrator = administratorService.create(administrator);
        return ResponseEntity.ok(createdAdministrator);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Administrator> getAdministratorById(@PathVariable String id) {
        Administrator administrator = administratorService.read(id);
        return ResponseEntity.ok(administrator);
    }
    @PutMapping()
    public ResponseEntity<Administrator> updateAdministrator(@RequestBody Administrator administrator) {
        Administrator updatedAdministrator = administratorService.update(administrator);
        return ResponseEntity.ok(updatedAdministrator);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        administratorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

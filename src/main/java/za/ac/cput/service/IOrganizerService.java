package za.ac.cput.service;

import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.Student;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

public interface IOrganizerService extends IService<Organizer, String> {
    Organizer findOrganizer(String organizerNumber);
}

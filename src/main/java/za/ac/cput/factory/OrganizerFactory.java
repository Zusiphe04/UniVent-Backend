package za.ac.cput.factory;

//import za.ac.cput.domain.Event;
import za.ac.cput.domain.Organizer;
import za.ac.cput.util.Helper;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

public class OrganizerFactory {
    public static Organizer createOrganizer(String name,
                                            String email,
                                            String password,
                                            String phoneNumber,
                                            String organizationName,
                                            String organizationType,
                                            List<Event> events,
                                            String contactEmail) {
        if (Helper.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name is required");
        }

        if (!Helper.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (Helper.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("Password is required");
        }

        if (!Helper.isValidPhone(phoneNumber)) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        if(Helper.isNullOrEmpty(organizationName)){
            throw new IllegalArgumentException("Organization name is required");
        }

        if (Helper.isNullOrEmpty(organizationType)){
            throw new IllegalArgumentException("Organization type is required");
        }

        if (!Helper.isValidEmail(contactEmail)){
            throw new IllegalArgumentException("Invalid contact email");
        }
        return new Organizer.Builder()
                .setName(name)
                .setEmail(email)
                .setPassword(password)
                .setPhoneNumber(phoneNumber)
                .setOrganizationName(organizationName)
                .setOrganizationType(organizationType)
                .setEvents(events)
                .setContactEmail(contactEmail)
                .build();
    }
}
package za.ac.cput.factory;

import za.ac.cput.domain.Administrator;
//import za.ac.cput.domain.Event;
import za.ac.cput.domain.User;
import za.ac.cput.util.Helper;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

public class AdministratorFactory {
    public static Administrator createAdministrator(String name,
                                                    String email,
                                                    String password,
                                                    String phoneNumber,
                                                    String adminLevel,
                                                    List<User> managedUsers,
                                                    List<Event> managedEvents){
        if(Helper.isNullOrEmpty(name)){
            throw new IllegalArgumentException("Name is required");
        }

        if (!Helper.isValidEmail(email)){
            throw new IllegalArgumentException("Invalid email");
        }

        if (Helper.isNullOrEmpty(password)){
            throw new IllegalArgumentException("Password is required");
        }

        if (!Helper.isValidPhone(phoneNumber)){
            throw new IllegalArgumentException("Invalid phone number");
        }

        if (Helper.isNullOrEmpty(adminLevel)){
            throw new IllegalArgumentException("Admin level is required");
        }
        return new Administrator.Builder()
                .setName(name)
                .setEmail(email)
                .setPassword(password)
                .setPhoneNumber(phoneNumber)
                .setAdminLevel(adminLevel)
                .setManagedUsers(managedUsers)
                .setManagedEvents(managedEvents)
                .build();
    }
}

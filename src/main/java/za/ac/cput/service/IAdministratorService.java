package za.ac.cput.service;

import za.ac.cput.domain.Administrator;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

public interface IAdministratorService extends IService<Administrator, String> {
    Administrator findByAdministratorNumber(String administratorNumber);
}

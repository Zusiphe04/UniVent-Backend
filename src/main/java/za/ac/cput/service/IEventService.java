package za.ac.cput.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import za.ac.cput.domain.Event;
import za.ac.cput.dtos.EventUpdateRequest;
import za.ac.cput.util.AuthenticatedUser;

import java.util.List;

/**Student name: Uyathandwa Ngomana
 * Student number: 231173229
 * Group: 3H
 * Date: 05 July 2026
 * **/

public interface IEventService extends IService<Event, Integer> {
    List<Event> findAll();

    Page<Event> search(String search, String status, String from, String to, Pageable pageable);

    Event updateEvent(Integer id, EventUpdateRequest request, AuthenticatedUser organizer);

    Event cancelEvent(Integer id, AuthenticatedUser organizer);
}

package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;

import java.util.List;

/**Student name: Uyathandwa Ngomana
 * Student number: 231173229
 * Group: 3H
 * Date: 05 July 2026
 * **/

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("select count(e) from Event e")
    long countAllEvents();

    @Override
    @EntityGraph(attributePaths = "venue")
    List<Event> findAll();

    List<Event> findByOrganizer(Organizer organizer);
    List<Event> findByStatus(EventStatusEnum status);

    @EntityGraph(attributePaths = "venue")
    java.util.Optional<Event> findByEventIdAndStatus(int eventId, EventStatusEnum status);

    @Query("select e from Event e left join fetch e.venue " +
            "where e.status = za.ac.cput.domain.EventStatusEnum.APPROVED " +
            "and (:search is null or lower(e.name) like concat('%', lower(:search), '%') " +
            "or lower(e.description) like concat('%', lower(:search), '%')) " +
            "and (:category is null or lower(e.category) = lower(:category)) " +
            "and (:date is null or e.dateTime like concat(:date, '%')) " +
            "order by e.dateTime asc")
    List<Event> findApprovedFiltered(@Param("search") String search,
                                     @Param("category") String category,
                                     @Param("date") String date);

    @Query("select e from Event e left join fetch e.venue where e.eventId = :eventId " +
            "and e.organizer.userId = :organizerId")
    java.util.Optional<Event> findByIdAndOrganizerId(@Param("eventId") int eventId,
                                                     @Param("organizerId") String organizerId);

    @org.springframework.data.jpa.repository.Query(
            "select e from Event e where (:organizerId is null or e.organizer.userId = :organizerId) " +
                    "and (:status is null or e.status = :status) order by e.dateTime desc")
    List<Event> findByOrganizerIdAndOptionalStatus(
            @org.springframework.data.repository.query.Param("organizerId") String organizerId,
            @org.springframework.data.repository.query.Param("status") EventStatusEnum status);
}

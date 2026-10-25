package za.ac.cput.univentbackend.repositoryTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.OrganizerRepository;
import za.ac.cput.UniventBackendApplication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ContextConfiguration(classes = UniventBackendApplication.class)
class OrganizerEventRepositoryTest {
    @Autowired
    private EventRepository events;

    @Autowired
    private OrganizerRepository organizers;

    @Test
    void filtersByOwnerAndOptionalStatus() {
        Organizer owner = organizer("owner");
        Organizer other = organizer("other");
        organizers.saveAll(List.of(owner, other));
        events.saveAll(List.of(
                event("Owned approved", owner, EventStatusEnum.APPROVED),
                event("Owned pending", owner, EventStatusEnum.PENDING_APPROVAL),
                event("Other approved", other, EventStatusEnum.APPROVED)));

        assertEquals(
                List.of("Owned approved", "Owned pending"),
                events.findByOrganizerIdAndOptionalStatus("owner", null).stream().map(Event::getName).sorted().toList());
        assertEquals(
                List.of("Owned approved"),
                events.findByOrganizerIdAndOptionalStatus("owner", EventStatusEnum.APPROVED)
                        .stream().map(Event::getName).toList());
    }

    private Organizer organizer(String id) {
        return new Organizer.Builder()
                .setUserId(id)
                .setRole(RoleEnum.ORGANIZER)
                .setName(id)
                .build();
    }

    private Event event(String name, Organizer organizer, EventStatusEnum status) {
        return new Event.Builder()
                .setName(name)
                .setDateTime("2026-08-01T10:00:00")
                .setStatus(status)
                .setOrganizer(organizer)
                .build();
    }
}

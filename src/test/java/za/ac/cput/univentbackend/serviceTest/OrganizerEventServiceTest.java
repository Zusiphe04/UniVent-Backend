package za.ac.cput.univentbackend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.service.OrganizerEventService;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerEventServiceTest {
    @Mock
    private EventRepository events;
    @Mock
    private BookingRepository bookings;

    @InjectMocks
    private OrganizerEventService service;

    @Test
    void organizerEventsAreFilteredByOwnerIdAndStatus() {
        Organizer owner = new Organizer.Builder().setUserId("org-1").build();
        Event event = new Event.Builder()
                .setName("My event")
                .setDateTime("2026-08-01T10:00:00")
                .setOrganizer(owner)
                .setStatus(EventStatusEnum.APPROVED)
                .build();
        when(events.findByOrganizerIdAndOptionalStatus("org-1", EventStatusEnum.APPROVED))
                .thenReturn(List.of(event));
        when(bookings.countByEvent(event)).thenReturn(2L);

        var result = service.list("org-1", EventStatusEnum.APPROVED);

        verify(events).findByOrganizerIdAndOptionalStatus("org-1", EventStatusEnum.APPROVED);
        assertEquals(1, result.size());
        assertEquals("My event", result.get(0).name());
        assertEquals(2, result.get(0).registrationCount());
    }
}

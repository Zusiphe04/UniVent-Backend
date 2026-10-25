package za.ac.cput.univentbackend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.domain.*;
import za.ac.cput.exception.EventStateConflictException;
import za.ac.cput.repository.AdministratorRepository;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.service.AdministratorService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueAdminServiceTest {
    @Mock
    private AdministratorRepository administrators;
    @Mock
    private UserRepository users;
    @Mock
    private EventRepository events;
    @Mock
    private BookingRepository bookings;

    @InjectMocks
    private AdministratorService service;

    @Test
    void approvePendingEventUpdatesStatusAndReturnsSummary() {
        Event event = event(EventStatusEnum.PENDING_APPROVAL);
        when(events.findById(7)).thenReturn(Optional.of(event));
        when(events.save(event)).thenReturn(event);
        when(bookings.countByEvent(event)).thenReturn(3L);

        var result = service.approveEvent("7");

        assertEquals("Tech Talk", result.name());
        assertEquals("APPROVED", result.status());
        assertEquals(3, result.registrationCount());
        verify(events).save(event);
    }

    @Test
    void approvingEventThatIsNotPendingConflicts() {
        when(events.findById(7)).thenReturn(Optional.of(event(EventStatusEnum.APPROVED)));

        assertThrows(EventStateConflictException.class, () -> service.approveEvent("7"));
    }

    @Test
    void unknownEventStatusIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.getEvents("UPCOMING"));
    }

    @Test
    void disablingCurrentAdminIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.disableUser("admin-1", "admin-1"));
    }

    @Test
    void passwordHashIsNeverSerialized() throws Exception {
        Student student = new Student.Builder()
                .setUserId("student-1")
                .setPasswordHash("test-hash")
                .setRole(RoleEnum.STUDENT)
                .build();
        student.setCreatedAt(null);

        String json = new ObjectMapper().writeValueAsString(student);

        assertFalse(json.contains("passwordHash"));
        assertFalse(json.contains("test-hash"));
    }

    private Event event(EventStatusEnum status) {
        return new Event.Builder()
                .setName("Tech Talk")
                .setDateTime("2026-08-01T10:00:00")
                .setStatus(status)
                .build();
    }
}

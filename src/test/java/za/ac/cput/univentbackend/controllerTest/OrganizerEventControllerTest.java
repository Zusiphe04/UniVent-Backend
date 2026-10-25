package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.dtos.OrganizerEventSummaryDTO;
import za.ac.cput.service.OrganizerEventService;
import za.ac.cput.util.AuthenticatedUser;
import za.ac.cput.controller.OrganizerEventController;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerEventControllerTest {
    @Mock
    private OrganizerEventService service;

    @InjectMocks
    private OrganizerEventController controller;

    @Test
    void organizerIsScopedToAuthenticatedUserId() {
        var authentication = authentication("org-1", "ORGANIZER");
        when(service.list("org-1", EventStatusEnum.APPROVED))
                .thenReturn(List.of(new OrganizerEventSummaryDTO(
                        "1", "My event", "2026-08-01T10:00:00", "Hall", "APPROVED", 2)));

        var response = controller.list("APPROVED", authentication);

        assertEquals("My event", response.get(0).name());
        verify(service).list("org-1", EventStatusEnum.APPROVED);
    }

    @Test
    void adminSeesAllEvents() {
        var authentication = authentication("admin-1", "ADMIN");
        when(service.list(null, null)).thenReturn(List.of());

        controller.list(null, authentication);

        verify(service).list(null, null);
    }

    @Test
    void unknownStatusIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.list("UNKNOWN", authentication("org-1", "ORGANIZER")));
    }

    private UsernamePasswordAuthenticationToken authentication(String userId, String role) {
        return new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(userId, userId + "@example.test", role),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}

package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.controller.EventController;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.dtos.EventSummaryResponse;
import za.ac.cput.service.EventService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService service;

    @InjectMocks
    private EventController controller;

    // Event creation/update were intentionally removed from EventController so
    // that all organizer writes go through /api/organizers/**, where the
    // organizer is derived from the authenticated principal. See OrganizerControllerTest.

    @Test
    public void testGetEventById() {

        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .setStatus(EventStatusEnum.APPROVED)
                .build();

        when(service.findApprovedById(1)).thenReturn(Optional.of(event));

        ResponseEntity<EventSummaryResponse> response = controller.getEventById(1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("Tech Talk", response.getBody().title());

        verify(service).findApprovedById(1);
    }

    @Test
    public void testDeleteEvent() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> controller.deleteEvent(1));
        assertEquals(405, exception.getStatusCode().value());
        verify(service, org.mockito.Mockito.never()).delete(1);
    }
}

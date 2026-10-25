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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private EventService service;

    @InjectMocks
    private EventController controller;

    @Test
    void getApprovedEventById() {
        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .setStatus(EventStatusEnum.APPROVED)
                .build();
        when(service.findVisibleById(1, null, false)).thenReturn(Optional.of(event));

        ResponseEntity<EventSummaryResponse> response = controller.getEventById(1, null);

        assertNotNull(response.getBody());
        assertEquals("Tech Talk", response.getBody().title());
        verify(service).findVisibleById(1, null, false);
    }

    @Test
    void missingEventReturns404() {
        when(service.findVisibleById(99, null, false)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.getEventById(99, null));
        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void getEventsUsesFrontendArrayContract() {
        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setStatus(EventStatusEnum.APPROVED)
                .build();
        when(service.findApproved("tech", "Academic", "2026-08-01", "APPROVED"))
                .thenReturn(List.of(event));

        ResponseEntity<List<EventSummaryResponse>> response = controller.getEvents(
                "tech", "Academic", "2026-08-01", "APPROVED");

        assertEquals(1, response.getBody().size());
        assertEquals("Tech Talk", response.getBody().get(0).title());
    }

    @Test
    void deleteIsNotAllowed() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> controller.deleteEvent(1));
        assertEquals(405, exception.getStatusCode().value());
    }
}

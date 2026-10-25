package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import za.ac.cput.controller.EventController;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.dtos.EventSummaryResponse;
import za.ac.cput.dtos.EventUpdateRequest;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.service.EventService;
import za.ac.cput.util.AuthenticatedUser;
import za.ac.cput.util.UnauthorizedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @Mock
    private EventService service;

    @InjectMocks
    private EventController controller;

    @Test
    public void testGetEventById() {

        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .build();

        when(service.read(1)).thenReturn(event);

        ResponseEntity<EventSummaryResponse> response = controller.getEventById(1);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("Tech Talk", response.getBody().title());

        verify(service).read(1);
    }

    @Test
    public void testGetEventByIdMissingReturns404() {
        when(service.read(99)).thenReturn(null);

        ResponseEntity<EventSummaryResponse> response = controller.getEventById(99);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    public void testGetEventsReturnsPageAndForwardsFilters() {
        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .setStatus(EventStatusEnum.APPROVED)
                .build();
        Pageable pageable = PageRequest.of(0, 2);
        when(service.search("tech", "APPROVED", "2026-08-01", "2026-08-31", pageable))
                .thenReturn(new PageImpl<>(List.of(event), pageable, 1));

        ResponseEntity<Page<EventSummaryResponse>> response = controller.getEvents(
                "tech", "Academic", "2026-08-01", "2026-08-31", "APPROVED", pageable);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(2, response.getBody().getSize());
        assertEquals("Tech Talk", response.getBody().getContent().get(0).title());
        verify(service).search("tech", "APPROVED", "2026-08-01", "2026-08-31", pageable);
    }

    @Test
    public void testUpdateEventUsesAuthenticatedOrganizer() {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated");
        request.setDescription("Updated description");
        request.setDateTime("2026-09-01T10:00:00");
        request.setMaxAttendees(50);

        Event saved = new Event.Builder()
                .setName("Updated")
                .setDescription("Updated description")
                .setDateTime("2026-09-01T10:00:00")
                .setMaxAttendees(50)
                .build();
        AuthenticatedUser organizer = new AuthenticatedUser("org-1", "org@univent.com", "ORGANIZER");
        when(service.updateEvent(eq(1), any(EventUpdateRequest.class), eq(organizer))).thenReturn(saved);

        ResponseEntity<EventSummaryResponse> response = controller.updateEvent(1, request, authentication(organizer));

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated", response.getBody().title());
        verify(service).updateEvent(eq(1), any(EventUpdateRequest.class), eq(organizer));
    }

    @Test
    public void testUpdateEventRejectsMissingAuthentication() {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated");

        assertThrows(UnauthorizedException.class, () -> controller.updateEvent(1, request, null));
    }

    @Test
    public void testCancelEventUsesAuthenticatedOrganizer() {
        Event cancelled = new Event.Builder()
                .setName("Tech Talk")
                .setStatus(EventStatusEnum.CANCELLED)
                .build();
        AuthenticatedUser organizer = new AuthenticatedUser("org-1", "org@univent.com", "ORGANIZER");
        when(service.cancelEvent(1, organizer)).thenReturn(cancelled);

        ResponseEntity<EventSummaryResponse> response = controller.cancelEvent(1, authentication(organizer));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(EventStatusEnum.CANCELLED, response.getBody().status());
        verify(service).cancelEvent(1, organizer);
    }

    @Test
    public void testCancelMissingEventPropagatesNotFound() {
        AuthenticatedUser organizer = new AuthenticatedUser("org-1", "org@univent.com", "ORGANIZER");
        when(service.cancelEvent(99, organizer)).thenThrow(new EventNotFoundException("Event not found"));

        assertThrows(EventNotFoundException.class,
                () -> controller.cancelEvent(99, authentication(organizer)));
    }

    @Test
    public void testDeleteEvent() {

        ResponseEntity<Void> response = controller.deleteEvent(1);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());

        verify(service).delete(1);
    }

    private Authentication authentication(AuthenticatedUser organizer) {
        return new UsernamePasswordAuthenticationToken(organizer, null, List.of());
    }
}

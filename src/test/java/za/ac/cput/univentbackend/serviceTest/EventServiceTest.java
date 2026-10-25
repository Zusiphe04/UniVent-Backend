package za.ac.cput.univentbackend.serviceTest;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.Venue;
import za.ac.cput.dtos.EventUpdateRequest;
import za.ac.cput.exception.EventNotFoundException;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.VenueRepository;
import za.ac.cput.service.EventFilter;
import za.ac.cput.service.EventService;
import za.ac.cput.util.AuthenticatedUser;
import za.ac.cput.util.UnauthorizedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private EventService service;

    private final AuthenticatedUser owner = new AuthenticatedUser("org-1", "org@univent.com", "ORGANIZER");
    private final AuthenticatedUser otherOrganizer = new AuthenticatedUser("org-2", "other@univent.com", "ORGANIZER");

    @Test
    public void testCreateEvent() {

        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .setDateTime("2026-08-15T10:00:00")
                .setMaxAttendees(100)
                .build();

        when(repository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Event result = service.create(event);

        assertNotNull(result);
        assertEquals("Tech Talk", result.getName());
        assertEquals("Java Workshop", result.getDescription());
        // Creation must never bypass the administrator approval workflow.
        assertEquals(EventStatusEnum.PENDING_APPROVAL, result.getStatus());
        verify(repository).save(any(Event.class));
    }

    @Test
    public void testReadEvent() {

        Event event = new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .build();

        when(repository.findById(1)).thenReturn(Optional.of(event));

        Event result = service.read(1);

        assertNotNull(result);
        assertEquals("Tech Talk", result.getName());
    }

    @Test
    public void testUpdateEvent() {

        when(repository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Event updated = new Event.Builder()
                .setName("Updated Event")
                .setDescription("Updated Description")
                .build();

        Event result = service.update(updated);

        assertNotNull(result);
        assertEquals("Updated Event", result.getName());
        verify(repository).save(any(Event.class));
    }

    @Test
    public void testDeleteEvent() {

        doNothing().when(repository).deleteById(1);

        service.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    public void testCreateNullReturnsNull() {

        assertNull(service.create(null));
    }

    @Test
    public void testReadEventReturnsNull() {

        when(repository.findById(99)).thenReturn(Optional.empty());

        Event result = service.read(99);

        assertNull(result);
    }

    @Test
    public void testSearchAppliesFiltersTogetherAndPaginates() {
        Event event = ownedEvent();
        Pageable pageable = PageRequest.of(1, 2);
        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(event), pageable, 5));

        Page<Event> result = service.search("tech", "APPROVED", "2026-08-01", "2026-08-31", pageable);

        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getNumber());
        assertEquals(2, result.getSize());
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    public void testSearchRejectsFromAfterTo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.search(null, null, "2026-08-16", "2026-08-15", PageRequest.of(0, 10)));

        assertEquals("'from' must not be after 'to'", exception.getMessage());
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    public void testSearchRejectsInvalidStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.search(null, "NOT_A_STATUS", null, null, PageRequest.of(0, 10)));
    }

    @Test
    public void testDateOnlyToIncludesEntireDay() {
        assertEquals("2026-08-15T23:59:59", EventFilter.normalizeTo("2026-08-15"));
        assertEquals("2026-08-15T00:00:00", EventFilter.normalizeFrom("2026-08-15"));
        assertEquals("2026-08-15T10:00:00", EventFilter.normalizeTo("2026-08-15T10:00:00"));
    }

    @Test
    public void testUpdateEventOwnedFieldsOnly() {
        Event existing = ownedEvent();
        Venue newVenue = new Venue.Builder().setVenueId("venue-2").setVenueName("Hall B").build();
        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(venueRepository.findById("venue-2")).thenReturn(Optional.of(newVenue));
        when(repository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventUpdateRequest request = new EventUpdateRequest();
        request.setTitle("Renamed");
        request.setDescription("New description");
        request.setStartsAt("2026-09-01T10:00:00");
        request.setCapacity(40);
        request.setVenueId("venue-2");

        Event result = service.updateEvent(1, request, owner);

        assertEquals("Renamed", result.getName());
        assertEquals("New description", result.getDescription());
        assertEquals("2026-09-01T10:00:00", result.getDateTime());
        assertEquals(40, result.getMaxAttendees());
        assertEquals(newVenue, result.getVenue());
        assertEquals(EventStatusEnum.APPROVED, result.getStatus());
        assertEquals("org-1", result.getOrganizer().getUserId());
    }

    @Test
    public void testUpdateEventRejectsBlankName() {
        when(repository.findById(1)).thenReturn(Optional.of(ownedEvent()));

        EventUpdateRequest request = validUpdateRequest();
        request.setName("  ");
        request.setTitle(null);

        assertThrows(IllegalArgumentException.class, () -> service.updateEvent(1, request, owner));
        verify(repository, never()).save(any(Event.class));
    }

    @Test
    public void testUpdateEventRejectsNonPositiveCapacity() {
        when(repository.findById(1)).thenReturn(Optional.of(ownedEvent()));

        EventUpdateRequest request = validUpdateRequest();
        request.setMaxAttendees(0);

        assertThrows(IllegalArgumentException.class, () -> service.updateEvent(1, request, owner));
    }

    @Test
    public void testUpdateEventMissingReturnsNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> service.updateEvent(99, validUpdateRequest(), owner));
    }

    @Test
    public void testUpdateEventRequiresAuthenticatedOrganizer() {
        when(repository.findById(1)).thenReturn(Optional.of(ownedEvent()));

        assertThrows(UnauthorizedException.class,
                () -> service.updateEvent(1, validUpdateRequest(), null));
    }

    @Test
    public void testUpdateEventRejectsNonOwner() {
        when(repository.findById(1)).thenReturn(Optional.of(ownedEvent()));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> service.updateEvent(1, validUpdateRequest(), otherOrganizer));

        assertEquals("Organizer does not own this event", exception.getMessage());
        verify(repository, never()).save(any(Event.class));
    }

    @Test
    public void testCancelEventOwned() {
        Event existing = ownedEvent();
        when(repository.findById(1)).thenReturn(Optional.of(existing));
        when(repository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = service.cancelEvent(1, owner);

        assertEquals(EventStatusEnum.CANCELLED, result.getStatus());
        assertEquals("Tech Talk", result.getName());
        verify(repository).save(existing);
    }

    @Test
    public void testCancelEventRejectsNonOwner() {
        when(repository.findById(1)).thenReturn(Optional.of(ownedEvent()));

        assertThrows(UnauthorizedException.class, () -> service.cancelEvent(1, otherOrganizer));
        verify(repository, never()).save(any(Event.class));
    }

    @Test
    public void testCancelMissingEvent() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> service.cancelEvent(99, owner));
    }

    @Test
    public void testCancelRequiresAuthenticatedOrganizer() {
        when(repository.findById(1)).thenReturn(Optional.of(ownedEvent()));

        assertThrows(UnauthorizedException.class, () -> service.cancelEvent(1, null));
    }

    private Event ownedEvent() {
        Organizer organizer = new Organizer.Builder()
                .setUserId("org-1")
                .setName("Campus Events")
                .build();
        Venue venue = new Venue.Builder()
                .setVenueId("venue-1")
                .setVenueName("Hall A")
                .build();
        return new Event.Builder()
                .setName("Tech Talk")
                .setDescription("Java Workshop")
                .setDateTime("2026-08-15T10:00:00")
                .setMaxAttendees(100)
                .setStatus(EventStatusEnum.APPROVED)
                .setOrganizer(organizer)
                .setVenue(venue)
                .build();
    }

    private EventUpdateRequest validUpdateRequest() {
        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated Event");
        request.setDescription("Updated Description");
        request.setDateTime("2026-09-01T10:00:00");
        request.setMaxAttendees(80);
        return request;
    }
}

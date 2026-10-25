package za.ac.cput.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatus;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.User;
import za.ac.cput.dtos.AdminEventSummaryDTO;
import za.ac.cput.dtos.PlatformStatsDTO;
import za.ac.cput.dtos.UserResponseDTO;
import za.ac.cput.exception.EventStateConflictException;
import za.ac.cput.repository.AdministratorRepository;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.util.IsoDateTime;

import java.util.List;
import java.util.Locale;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorService.java
 * Date: 05 July 2026
 **/

@Service
public class AdministratorService implements IAdministratorService {
    private final AdministratorRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public AdministratorService(AdministratorRepository repository,
                                UserRepository userRepository,
                                EventRepository eventRepository,
                                BookingRepository bookingRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Administrator findByAdministratorNumber(String administratorNumber) {
        return null;
    }

    @Override
    public Administrator create(Administrator administrator) {
        if (administrator == null) {
            return null;
        }
        return repository.save(administrator);
    }

    @Override
    public Administrator read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Administrator update(Administrator administrator) {
        if (administrator == null) {
            return null;
        }
        return repository.save(administrator);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }


    @Override
    public List<User> viewAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public List<Event> viewPendingEvents() {
        return eventRepository.findByStatus(EventStatusEnum.PENDING_APPROVAL);
    }


    @Override
    public List<Event> monitorEvents() {
        return eventRepository.findAll();
    }


    @Override
    public Event approveEvent(Integer eventId) {
        return changeEventStatus(eventId, EventStatusEnum.APPROVED);
    }


    @Override
    public Event rejectEvent(Integer eventId) {
        // EventStatusEnum has no REJECTED value, so a rejected pending event
        // becomes CANCELLED. If the team adds REJECTED to the enum, use it here.
        return changeEventStatus(eventId, EventStatusEnum.CANCELLED);
    }


    @Override
    public Event disableEvent(Integer eventId) {
        return changeEventStatus(eventId, EventStatusEnum.DISABLED);
    }

    private Event changeEventStatus(Integer eventId, EventStatusEnum status) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return null;
        }
        event.setStatus(status);
        return eventRepository.save(event);
    }


    @Override
    public void deleteUser(String userId) {
        // NOTE: will fail with a FK constraint if the user has bookings/events.
        // Consider soft-disabling (disableUser) instead, or delete child rows first.
        userRepository.deleteById(userId);
    }

    // Soft alternative kept from previous version
    @Override
    public User disableUser(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setDisabled(true);
            return userRepository.save(user);
        }
        return null;
    }

    @Override
    public PlatformStatsDTO getPlatformStats() {
        return getStats();
    }

    @Override
    public PlatformStatsDTO getStats() {
        return new PlatformStatsDTO(eventRepository.countAllEvents(),
                userRepository.countAllUsers(),
                bookingRepository.countAllBookings());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminEventSummaryDTO> getEvents(String rawStatus) {
        EventStatusEnum status = parseAdminEventStatus(rawStatus);
        List<Event> events = status == null ? eventRepository.findAll() : eventRepository.findByStatus(status);
        return events.stream().map(this::toAdminEventSummary).toList();
    }

    @Override
    public AdminEventSummaryDTO approveEvent(String id) {
        Event event = findAdminEvent(id);
        if (event.getStatus() != EventStatusEnum.PENDING_APPROVAL) {
            throw new EventStateConflictException("Event is not pending approval");
        }
        event.setStatus(EventStatusEnum.APPROVED);
        return toAdminEventSummary(eventRepository.save(event));
    }

    @Override
    public AdminEventSummaryDTO disableEvent(String id) {
        Event event = findAdminEvent(id);
        if (event.getStatus() == EventStatusEnum.DISABLED) {
            throw new EventStateConflictException("Event is already disabled");
        }
        event.setStatus(EventStatusEnum.DISABLED);
        return toAdminEventSummary(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsers(String search, String rawRole, Pageable pageable) {
        RoleEnum role = null;
        if (rawRole != null && !rawRole.isBlank()) {
            try {
                role = RoleEnum.valueOf(rawRole.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException("Unknown role: " + rawRole);
            }
        }
        return userRepository.findByRoleAndSearch(role, search, pageable).map(this::toAdminUserResponse);
    }

    @Override
    public UserResponseDTO disableUser(String id, String currentUserId) {
        if (id.equals(currentUserId)) {
            throw new IllegalArgumentException("Administrators cannot disable their own account");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new za.ac.cput.exception.UserNotFoundException(id));
        user.setAccountStatus(AccountStatus.DISABLED);
        return toAdminUserResponse(userRepository.save(user));
    }

    @Override
    public UserResponseDTO approveOrganizer(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new za.ac.cput.exception.UserNotFoundException(id));
        if (user.getRole() != RoleEnum.ORGANIZER
                || user.getAccountStatus() != AccountStatus.PENDING_APPROVAL) {
            throw new EventStateConflictException("Only pending organizer accounts can be approved");
        }
        user.setAccountStatus(AccountStatus.ACTIVE);
        return toAdminUserResponse(userRepository.save(user));
    }

    private Event findAdminEvent(String id) {
        try {
            return eventRepository.findById(Integer.valueOf(id))
                    .orElseThrow(() -> new za.ac.cput.exception.EventNotFoundException(id));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Event id must be numeric");
        }
    }

    private EventStatusEnum parseAdminEventStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank() || rawStatus.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return EventStatusEnum.valueOf(
                    EventStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT)).name());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown event status: " + rawStatus);
        }
    }

    private AdminEventSummaryDTO toAdminEventSummary(Event event) {
        return new AdminEventSummaryDTO(
                Integer.toString(event.getEventId()),
                event.getName(),
                event.getOrganizer() == null ? null : event.getOrganizer().getName(),
                IsoDateTime.format(event.getDateTime()),
                event.getStatus() == null ? null : event.getStatus().name(),
                bookingRepository.countByEvent(event));
    }

    private UserResponseDTO toAdminUserResponse(User user) {
        return new UserResponseDTO(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole() == null ? null : user.getRole().name(),
                user.getAccountStatus().name(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().toString());
    }
}
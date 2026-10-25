package za.ac.cput.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.dtos.BookingResponse;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.StudentRepository;

import java.util.List;

@Service
public class BookingApiService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final StudentRepository studentRepository;
    private final BookingService bookingService;
    private final TicketService ticketService;

    public BookingApiService(BookingRepository bookingRepository,
                             EventRepository eventRepository,
                             StudentRepository studentRepository,
                             BookingService bookingService,
                             TicketService ticketService) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.studentRepository = studentRepository;
        this.bookingService = bookingService;
        this.ticketService = ticketService;
    }

    @Transactional
    public BookingResponse create(int eventId, String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        if (event.getStatus() != EventStatusEnum.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is not available for booking");
        }
        Booking booking = bookingService.registerForEvent(student, event);
        ticketService.generateTicket(booking);
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> mine(String studentId) {
        return bookingRepository.findByStudent_UserIdOrderByBookingTimeDesc(studentId).stream()
                .map(BookingResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean exists(int eventId, String studentId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        return bookingRepository.existsByStudent_UserIdAndEvent_EventId(studentId, eventId);
    }

    @Transactional
    public BookingResponse cancel(String bookingReferenceOrId, String studentId) {
        Booking booking = bookingRepository.findByBookingReferenceAndStudent_UserId(bookingReferenceOrId, studentId)
                .orElseGet(() -> {
                    if (bookingRepository.existsByBookingReference(bookingReferenceOrId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this booking");
                    }
                    return findOwnedByNumericId(bookingReferenceOrId, studentId);
                });
        try {
            booking.cancel();
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
        }
        return BookingResponse.from(bookingRepository.save(booking));
    }

    private Booking findOwnedByNumericId(String value, String studentId) {
        try {
            int id = Integer.parseInt(value);
            return bookingRepository.findByBookingIdAndStudent_UserId(id, studentId)
                    .orElseGet(() -> {
                        if (!bookingRepository.existsById(id)) {
                            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
                        }
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this booking");
                    });
        } catch (NumberFormatException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
    }
}

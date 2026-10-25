package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.UniventBackendApplication;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.NotificationType;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.Venue;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.NotificationRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.repository.VenueRepository;
import za.ac.cput.util.JwtUtil;
import za.ac.cput.univentbackend.utilTest.TestJwtTokenFactory;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UniventBackendApplication.class)
@AutoConfigureMockMvc
@Transactional
class FrontendContractHttpTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository users;
    @Autowired
    private EventRepository events;
    @Autowired
    private VenueRepository venues;
    @Autowired
    private NotificationRepository notifications;
    @Autowired
    private JwtUtil jwtUtil;

    private Student student;
    private Student otherStudent;
    private Organizer organizer;
    private Organizer otherOrganizer;
    private Administrator admin;
    private Venue venue;
    private Event approvedEvent;
    private String studentToken;
    private String otherStudentToken;
    private String organizerToken;
    private String otherOrganizerToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        student = (Student) users.save(new Student.Builder()
                .setUserId("contract-student")
                .setName("Student One")
                .setEmail("student.one@mycput.ac.za")
                .setPasswordHash("student-hash")
                .setRole(RoleEnum.STUDENT)
                .build());
        otherStudent = (Student) users.save(new Student.Builder()
                .setUserId("contract-student-two")
                .setName("Student Two")
                .setEmail("student.two@mycput.ac.za")
                .setPasswordHash("student-hash")
                .setRole(RoleEnum.STUDENT)
                .build());
        organizer = (Organizer) users.save(new Organizer.Builder()
                .setUserId("contract-organizer")
                .setName("Organizer One")
                .setEmail("organizer.one@mycput.ac.za")
                .setPasswordHash("organizer-hash")
                .setRole(RoleEnum.ORGANIZER)
                .build());
        otherOrganizer = (Organizer) users.save(new Organizer.Builder()
                .setUserId("contract-organizer-two")
                .setName("Organizer Two")
                .setEmail("organizer.two@mycput.ac.za")
                .setPasswordHash("organizer-hash")
                .setRole(RoleEnum.ORGANIZER)
                .build());
        admin = (Administrator) users.save(new Administrator.Builder()
                .setUserId("contract-admin")
                .setName("Administrator")
                .setEmail("admin@mycput.ac.za")
                .setPasswordHash("admin-hash")
                .setRole(RoleEnum.ADMIN)
                .build());

        venue = venues.save(new Venue.Builder()
                .setVenueId("contract-venue")
                .setVenueName("Main Hall")
                .setCampus("Bellville")
                .setLocationDetails("Block A")
                .setCapacity(300)
                .build());
        approvedEvent = events.save(event("Approved Campus Event", organizer, EventStatusEnum.APPROVED));

        studentToken = TestJwtTokenFactory.tokenFor(jwtUtil, student);
        otherStudentToken = TestJwtTokenFactory.tokenFor(jwtUtil, otherStudent);
        organizerToken = TestJwtTokenFactory.tokenFor(jwtUtil, organizer);
        otherOrganizerToken = TestJwtTokenFactory.tokenFor(jwtUtil, otherOrganizer);
        adminToken = TestJwtTokenFactory.tokenFor(jwtUtil, admin);
    }

    @Test
    void eventBrowseRequiresAuthFiltersApprovedEventsAndReturnsFrontendNames() throws Exception {
        events.save(event("Pending Campus Event", organizer, EventStatusEnum.PENDING_APPROVAL));

        mvc.perform(get("/api/events"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/events"));

        mvc.perform(get("/api/events").param("search", "campus").param("category", "technology")
                        .param("date", approvedEvent.getDateTime().substring(0, 10))
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(approvedEvent.getEventId()))
                .andExpect(jsonPath("$[0].title").value("Approved Campus Event"))
                .andExpect(jsonPath("$[0].category").value("Technology"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].organizer").doesNotExist());

        mvc.perform(get("/api/events/999999").header("Authorization", bearer(studentToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/events/999999"));
    }

    @Test
    void organizerEventListRequiresOrganizerRoleAndUsesAuthenticatedOwner() throws Exception {
        organizer.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        users.save(organizer);
        mvc.perform(get("/api/organizers/me/events").header("Authorization", bearer(organizerToken)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/organizers/me/events").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentBookingTicketAndCancellationAreOwnerScoped() throws Exception {
        String body = "{\"eventId\":" + approvedEvent.getEventId() + "}";
        String bookingResponse = mvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventTitle").value("Approved Campus Event"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        int bookingId = Integer.parseInt(bookingResponse.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));

        mvc.perform(get("/api/bookings/exists").param("eventId", Integer.toString(approvedEvent.getEventId()))
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        mvc.perform(get("/api/bookings/my").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingId));
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(body)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        mvc.perform(get("/api/tickets/booking/{id}", bookingId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketCode").exists())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        mvc.perform(get("/api/tickets/booking/{id}", bookingId)
                        .header("Authorization", bearer(otherStudentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        mvc.perform(patch("/api/bookings/{id}/cancel", bookingId)
                        .header("Authorization", bearer(otherStudentToken)))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/bookings/{id}/cancel", bookingId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
        mvc.perform(patch("/api/bookings/{id}/cancel", bookingId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isConflict());
    }

    @Test
    void eventManagementRequiresOrganizerOwnershipAndAdminModeration() throws Exception {
        otherOrganizer.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        users.save(otherOrganizer);
        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson()).header("Authorization", bearer(otherOrganizerToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Missing venue\"}")
                        .header("Authorization", bearer(organizerToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/api/events"));

        String createBody = eventJson();
        String createdResponse = mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody)
                        .header("Authorization", bearer(organizerToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Campus Event"))
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        int newEventId = Integer.parseInt(createdResponse.replaceAll(".*\\\"id\\\":(\\d+).*", "$1"));

        mvc.perform(put("/api/events/{id}", newEventId).contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson()).header("Authorization", bearer(organizerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Campus Event"));
        mvc.perform(put("/api/events/{id}", newEventId).contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson()).header("Authorization", bearer(otherOrganizerToken)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/events/{id}/bookings", approvedEvent.getEventId())
                        .header("Authorization", bearer(otherOrganizerToken)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/events/{id}/bookings", approvedEvent.getEventId())
                        .header("Authorization", bearer(organizerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mvc.perform(patch("/api/events/{id}/approve", newEventId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
        mvc.perform(patch("/api/events/{id}/approve", newEventId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        mvc.perform(patch("/api/events/{id}/approve", newEventId)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/venues").header("Authorization", bearer(organizerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].venueId").value("contract-venue"))
                .andExpect(jsonPath("$[0].venueName").value("Main Hall"));
    }

    @Test
    void authenticationUsesFrontendFieldsAndReturnsSafeResponses() throws Exception {
        String studentRegistration = """
                {"name":"Registered Student","email":"registered.student@mycput.ac.za",
                 "password":"securepass","phoneNumber":"0848882617","role":"STUDENT",
                 "studentNumber":"221234567","faculty":"Applied Sciences",
                 "department":"Chemistry","yearOfStudy":2}
                """;
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(studentRegistration))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"registered.student@mycput.ac.za\",\"password\":\"securepass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(studentRegistration))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("An account with this email already exists"));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Bad Role","email":"bad.role@mycput.ac.za","password":"securepass",
                                 "phoneNumber":"0848882617","role":"GUEST"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Organizer","email":"pending.organizer@mycput.ac.za",
                                 "password":"securepass","phoneNumber":"0848882617","role":"ORGANIZER",
                                 "staffNumber":"STF0050","organizationName":"Campus Society",
                                 "organizationType":"Academic society","contactEmail":"society@mycput.ac.za"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ORGANIZER"))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Organizer"))
                .andExpect(jsonPath("$.email").value("pending.organizer@mycput.ac.za"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.accountStatus").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.inviteCode").doesNotExist());

        Organizer registeredOrganizer = (Organizer) users.findByEmail("pending.organizer@mycput.ac.za")
                .orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("STF0050", registeredOrganizer.getStaffNumber());
        org.junit.jupiter.api.Assertions.assertEquals("Campus Society", registeredOrganizer.getOrganizationName());
        org.junit.jupiter.api.Assertions.assertEquals("Academic society", registeredOrganizer.getOrganizationType());
        org.junit.jupiter.api.Assertions.assertEquals("society@mycput.ac.za",
                registeredOrganizer.getOrganizationEmail());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"pending.organizer@mycput.ac.za","password":"securepass"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ORGANIZER"))
                .andExpect(jsonPath("$.accountStatus").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Second Organizer","email":"second.organizer@cput.ac.za",
                                 "password":"securepass","phoneNumber":"0848882617","role":"ORGANIZER",
                                 "staffNumber":"STF0050","organizationName":"Another Society",
                                 "organizationType":"Sports club","contactEmail":"sports@cput.ac.za"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("STAFF_NUMBER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("An account with this staff number already exists"));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Invalid Organizer","email":"invalid.organizer@mycput.ac.za",
                                 "password":"securepass","phoneNumber":"0848882617","role":"ORGANIZER",
                                 "staffNumber":"STF0051","organizationName":"Invalid Society",
                                 "organizationType":"Academic society","contactEmail":"society@example.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Contact email must be valid and end in .ac.za"));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Invalid Domain","email":"invalid.domain@other.ac.za",
                                 "password":"securepass","phoneNumber":"0848882617","role":"ORGANIZER",
                                 "staffNumber":"STF0052","organizationName":"Invalid Society",
                                 "organizationType":"Academic society","contactEmail":"society@mycput.ac.za"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email must end in @mycput.ac.za or @cput.ac.za"));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Invalid Phone","email":"invalid.phone@mycput.ac.za",
                                 "password":"securepass","phoneNumber":"0211234567","role":"ORGANIZER",
                                 "staffNumber":"STF0053","organizationName":"Invalid Society",
                                 "organizationType":"Academic society","contactEmail":"society@mycput.ac.za"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Phone number must be a valid 10-digit mobile number"));

        Student registered = (Student) users.findByEmail("registered.student@mycput.ac.za").orElseThrow();
        registered.setAccountStatus(AccountStatus.DISABLED);
        users.save(registered);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"registered.student@mycput.ac.za\",\"password\":\"securepass\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void frontendDevelopmentOriginCanCallRegistrationEndpoint() throws Exception {
        mvc.perform(options("/api/auth/register")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Methods",
                        org.hamcrest.Matchers.containsString("POST")));
    }

    @Test
    void profilePreferencesAndNotificationFieldsMatchFrontendUsage() throws Exception {
        Notification notification = notifications.save(new Notification(
                student, NotificationType.REGISTRATION_CONFIRMED, "Booking confirmed", "Your place is reserved."));

        mvc.perform(get("/api/users/me").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("contract-student"))
                .andExpect(jsonPath("$.preferences.emailReminders").value(true))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        mvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/users/me").header("Authorization", bearer(adminToken)))
                .andExpect(status().isForbidden());

        mvc.perform(patch("/api/users/me").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Updated","surname":"Student","phoneNumber":"0848882617",
                                 "residence":"Campus","faculty":"Applied Sciences","yearOfStudy":3}
                                """)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.yearOfStudy").value(3));
        mvc.perform(patch("/api/users/me/preferences").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailReminders\":false,\"eventUpdates\":true}")
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferences.emailReminders").value(false));

        mvc.perform(get("/api/notifications").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(notification.getNotificationId()))
                .andExpect(jsonPath("$[0].notificationId").doesNotExist())
                .andExpect(jsonPath("$[0].read").value(false));
        mvc.perform(get("/api/notifications/unread-count").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(1));
        mvc.perform(patch("/api/notifications/{id}/read", notification.getNotificationId())
                        .header("Authorization", bearer(otherStudentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
        mvc.perform(patch("/api/notifications/{id}/read", notification.getNotificationId())
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notification.getNotificationId()))
                .andExpect(jsonPath("$.read").value(true));
        mvc.perform(patch("/api/notifications/read-all").header("Authorization", bearer(studentToken)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/notifications/unread-count").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(0));
        mvc.perform(delete("/api/notifications/{id}", notification.getNotificationId())
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    void adminRegistrationDoesNotRequireAnInviteCode() throws Exception {
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"New Admin","email":"new.admin@mycput.ac.za",
                                 "password":"securepass","phoneNumber":"0848882617",
                                 "role":"ADMIN","staffNumber":"STF0001"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.inviteCode").doesNotExist());

        org.junit.jupiter.api.Assertions.assertTrue(users.existsByEmail("new.admin@mycput.ac.za"));
    }
    private Event event(String name, Organizer owner, EventStatusEnum status) {
        Event event = new Event.Builder()
                .setName(name)
                .setDescription("Campus technology gathering")
                .setDateTime(LocalDateTime.now().plusDays(5).withNano(0).toString())
                .setCategory("Technology")
                .setMaxAttendees(25)
                .setOrganizer(owner)
                .setVenue(venue)
                .setStatus(status)
                .build();
        return event;
    }

    private String eventJson() {
        return """
                {"title":"New Campus Event","description":"A campus workshop",
                 "dateTime":"%s","endDateTime":"%s","venueId":"%s",
                 "category":"Technology","imageUrl":"https://example.test/event.jpg","maxAttendees":25}
                """.formatted(
                LocalDateTime.now().plusDays(7).withNano(0),
                LocalDateTime.now().plusDays(7).plusHours(2).withNano(0),
                venue.getVenueId());
    }


    private String bearer(String token) {
        return "Bearer " + token;
    }
}

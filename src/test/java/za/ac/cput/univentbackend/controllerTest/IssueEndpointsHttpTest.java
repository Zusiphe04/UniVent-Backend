package za.ac.cput.univentbackend.controllerTest;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.UniventBackendApplication;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.Event;
import za.ac.cput.domain.EventStatusEnum;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.EventRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.util.JwtUtil;
import za.ac.cput.univentbackend.utilTest.TestJwtTokenFactory;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = UniventBackendApplication.class,
        properties = {
                "jwt.secret=univent-http-tests-secret-key-long-enough",
                "jwt.expiration=3600000"
        })
@AutoConfigureMockMvc
@Transactional
class IssueEndpointsHttpTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository users;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EventRepository events;
    @Autowired
    private JwtUtil jwtUtil;

    private Administrator admin;
    private Student student;
    private za.ac.cput.domain.Organizer organizer;
    private String adminToken;
    private String studentToken;
    private String organizerToken;
    private Event pendingEvent;
    private Event approvedEvent;

    @BeforeEach
    void setUp() {
        admin = (Administrator) users.save(new Administrator.Builder()
                .setUserId("http-admin")
                .setName("Admin")
                .setEmail("admin@example.test")
                .setPasswordHash("admin-hash")
                .setRole(RoleEnum.ADMIN)
                .build());
        student = (Student) users.save(new Student.Builder()
                .setUserId("http-student")
                .setName("Student")
                .setEmail("student@example.test")
                .setPasswordHash("student-hash")
                .setRole(RoleEnum.STUDENT)
                .build());
        organizer = new za.ac.cput.domain.Organizer.Builder()
                .setUserId("http-organizer")
                .setName("Organizer")
                .setEmail("organizer@example.test")
                .setPasswordHash("organizer-hash")
                .setRole(RoleEnum.ORGANIZER)
                .build();
        organizer.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        organizer = (za.ac.cput.domain.Organizer) users.save(organizer);
        adminToken = TestJwtTokenFactory.tokenFor(jwtUtil, admin);
        studentToken = TestJwtTokenFactory.tokenFor(jwtUtil, student);
        organizerToken = TestJwtTokenFactory.tokenFor(jwtUtil, organizer);
        pendingEvent = events.save(event("Pending", organizer, EventStatusEnum.PENDING_APPROVAL));
        approvedEvent = events.save(event("Approved", organizer, EventStatusEnum.APPROVED));
    }

    @Test
    void adminStatsEventsAndEventTransitionsHaveExpectedHttpContracts() throws Exception {
        mvc.perform(get("/api/admin/stats").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventCount").value(2))
                .andExpect(jsonPath("$.userCount").value(3))
                .andExpect(jsonPath("$.bookingCount").value(0));

        mvc.perform(get("/api/admin/events").param("status", "PENDING_APPROVAL")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pending"))
                .andExpect(jsonPath("$[0].dateTime").value("2026-08-01T10:00:00"))
                .andExpect(jsonPath("$[0].status").value("PENDING_APPROVAL"));

        mvc.perform(get("/api/admin/events").param("status", "UNKNOWN")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/admin/events"));

        mvc.perform(patch("/api/admin/events/{id}/approve", pendingEvent.getEventId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
        mvc.perform(patch("/api/admin/events/{id}/approve", approvedEvent.getEventId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isConflict());
        mvc.perform(patch("/api/admin/events/{id}/approve", 999999)
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNotFound());
        mvc.perform(patch("/api/admin/events/{id}/disable", approvedEvent.getEventId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DISABLED"));
        mvc.perform(patch("/api/admin/events/{id}/disable", approvedEvent.getEventId())
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isConflict());
    }

    @Test
    void adminUserSearchAndDisableNeverExposePasswordHash() throws Exception {
        mvc.perform(get("/api/admin/users").param("search", "Student").param("role", "STUDENT")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("http-student"))
                .andExpect(jsonPath("$.content[0].accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].passwordHash").doesNotExist())
                .andExpect(jsonPath("$.content[0].password").doesNotExist());

        mvc.perform(get("/api/admin/users").param("role", "UNKNOWN")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/admin/users/http-student/disable")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("http-student"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
        users.flush();
        entityManager.clear();
        mvc.perform(get("/api/admin/users").param("search", "Student")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountStatus").value("DISABLED"));
        mvc.perform(patch("/api/admin/users/http-admin/disable")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Administrators cannot disable their own account"));
        mvc.perform(patch("/api/admin/users/missing/disable")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found: missing"));
    }

    @Test
    void onlyAnAdministratorCanDisableAUser() throws Exception {
        mvc.perform(patch("/api/admin/users/http-student/disable"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        mvc.perform(patch("/api/admin/users/http-student/disable")
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        mvc.perform(get("/api/admin/users").param("search", "Student")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountStatus").value("ACTIVE"));
    }

    @Test
    void adminEndpointsRequireAdminRoleAndBearerAuthentication() throws Exception {
        mvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/admin/stats"));
        mvc.perform(get("/api/admin/stats").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void organizerEndpointScopesByJwtAndAdminCanSeeAll() throws Exception {
        za.ac.cput.domain.Organizer other = (za.ac.cput.domain.Organizer) users.save(
                new za.ac.cput.domain.Organizer.Builder()
                        .setUserId("http-other-organizer")
                        .setName("Other")
                        .setEmail("other@example.test")
                        .setRole(RoleEnum.ORGANIZER)
                        .build());
        events.save(event("Other event", other, EventStatusEnum.APPROVED));

        mvc.perform(get("/api/organizers/me/events")
                        .param("status", "APPROVED")
                        .header("Authorization", bearer(organizerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Approved"))
                .andExpect(jsonPath("$[0].dateTime").value("2026-08-01T10:00:00"));
        mvc.perform(get("/api/organizers/me/events")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'Other event')]").exists());
        mvc.perform(get("/api/organizers/me/events")
                        .param("status", "INVALID")
                        .header("Authorization", bearer(organizerToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value("/api/organizers/me/events"));
        mvc.perform(get("/api/organizers/me/events")
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/organizers/me/events"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passwordsNeverAppearInDirectUserEntityJson() throws Exception {
        mvc.perform(get("/api/admin/users")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertFalse(
                        result.getResponse().getContentAsString().contains("admin-hash")))
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertFalse(
                        result.getResponse().getContentAsString().contains("passwordHash")));
    }

    @Test
    void administratorCanApproveOnlyPendingOrganizerAccounts() throws Exception {
        mvc.perform(patch("/api/admin/users/http-organizer/approve")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        mvc.perform(patch("/api/admin/users/http-organizer/approve")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    private Event event(String name, za.ac.cput.domain.Organizer owner, EventStatusEnum status) {
        return new Event.Builder()
                .setName(name)
                .setDateTime("2026-08-01T10:00:00")
                .setOrganizer(owner)
                .setStatus(status)
                .build();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

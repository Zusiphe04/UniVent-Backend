package za.ac.cput.univentbackend.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.UniventBackendApplication;
import za.ac.cput.domain.Administrator;
import za.ac.cput.repository.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = UniventBackendApplication.class,
        properties = "app.auth.admin-registration-enabled=true")
@AutoConfigureMockMvc
@Transactional
class AdminDemoRegistrationHttpTest {
    private static final String PASSWORD = "demo-admin-password";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserRepository users;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    void demoModeRegistersAdminWithoutInviteAndGrantsAdminApiAccess() throws Exception {
        String registration = """
                {"name":"Demo Admin","email":"demo.admin@mycput.ac.za","password":"%s",
                 "phoneNumber":"0821234567","role":"ADMIN","staffNumber":"DEMO-ADMIN-1001"}
                """.formatted(PASSWORD);

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registration))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Demo Admin"))
                .andExpect(jsonPath("$.email").value("demo.admin@mycput.ac.za"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        Administrator saved = (Administrator) users.findByEmail("demo.admin@mycput.ac.za")
                .orElseThrow();
        org.junit.jupiter.api.Assertions.assertTrue(passwordEncoder.matches(PASSWORD, saved.getPasswordHash()));
        org.junit.jupiter.api.Assertions.assertNotEquals(PASSWORD, saved.getPasswordHash());

        org.springframework.test.web.servlet.MvcResult loginResult = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"demo.admin@mycput.ac.za","password":"%s"}
                                """.formatted(PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        Matcher tokenMatcher = Pattern.compile("\"token\":\"([^\"]+)\"")
                .matcher(loginResult.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(tokenMatcher.find());
        String token = tokenMatcher.group(1);
        mvc.perform(get("/api/admin/stats").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Another Demo Admin","email":"another.demo.admin@cput.ac.za",
                                 "password":"demo-admin-password","phoneNumber":"0821234567",
                                 "role":"ADMIN","staffNumber":"DEMO-ADMIN-1001"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("STAFF_NUMBER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value(
                        "An account with this staff number already exists"));
    }

    @Test
    void invalidAdminFieldsAndDuplicateStaffNumberReturnClearErrors() throws Exception {
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Invalid Admin","email":"invalid.admin@elsewhere.ac.za",
                                 "password":"demo-admin-password","phoneNumber":"0821234567",
                                 "role":"ADMIN","staffNumber":"DEMO-ADMIN-1002"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Email must end in @mycput.ac.za or @cput.ac.za"));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Invalid Phone","email":"invalid.phone.admin@cput.ac.za",
                                 "password":"demo-admin-password","phoneNumber":"0211234567",
                                 "role":"ADMIN","staffNumber":"DEMO-ADMIN-1003"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Phone number must be a valid 10-digit mobile number"));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Short Password","email":"short.password@cput.ac.za",
                                 "password":"short","phoneNumber":"0821234567",
                                 "role":"ADMIN","staffNumber":"DEMO-ADMIN-1004"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters"));
    }
}

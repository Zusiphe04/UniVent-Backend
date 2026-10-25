package za.ac.cput.univentbackend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Administrator;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.dtos.AuthResponse;
import za.ac.cput.dtos.LoginRequest;
import za.ac.cput.dtos.RegisterRequest;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.StaffNumberAlreadyExistsException;
import za.ac.cput.repository.OrganizerRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.repository.AdministratorRepository;
import za.ac.cput.service.AuthService;
import za.ac.cput.util.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private AdministratorRepository administratorRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest organizerRequest() {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "name", "Amanda");
        ReflectionTestUtils.setField(request, "email", "amanda@mycput.ac.za");
        ReflectionTestUtils.setField(request, "password", "securepass");
        ReflectionTestUtils.setField(request, "phoneNumber", "0848882617");
        ReflectionTestUtils.setField(request, "role", "ORGANIZER");
        ReflectionTestUtils.setField(request, "staffNumber", "STF0147");
        ReflectionTestUtils.setField(request, "organizationName", "Bitdevs");
        ReflectionTestUtils.setField(request, "organizationType", "NGO");
        ReflectionTestUtils.setField(request, "contactEmail", "bitdevs@mycput.ac.za");
        return request;
    }

    private LoginRequest loginRequest() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "amanda@mycput.ac.za");
        ReflectionTestUtils.setField(request, "password", "securepass");
        return request;
    }

    @Test
    void registerOrganizerAssignsUserIdAndPersists() {
        when(userRepository.existsByEmail("amanda@mycput.ac.za")).thenReturn(false);
        when(passwordEncoder.encode("securepass")).thenReturn("hashed");
        when(organizerRepository.save(any(Organizer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(Organizer.class))).thenReturn("token");

        AuthResponse response = authService.register(organizerRequest());

        ArgumentCaptor<Organizer> captor = ArgumentCaptor.forClass(Organizer.class);
        verify(organizerRepository).save(captor.capture());
        Organizer saved = captor.getValue();

        // Without a generated id the organizer cannot be persisted (userId is the @Id)
        assertNotNull(saved.getUserId(), "Organizer registration must assign a user id");
        assertFalse(saved.getUserId().isBlank());
        assertEquals(RoleEnum.ORGANIZER, saved.getRole());
        assertEquals("STF0147", saved.getStaffNumber());
        assertEquals("Bitdevs", saved.getOrganizationName());
        assertEquals("NGO", saved.getOrganizationType());
        assertEquals("bitdevs@mycput.ac.za", saved.getOrganizationEmail());
        assertEquals(AccountStatus.PENDING_APPROVAL, saved.getAccountStatus());
        assertEquals(saved.getUserId(), response.getUserId());
        assertEquals("Amanda", response.getName());
        assertEquals("amanda@mycput.ac.za", response.getEmail());
        assertEquals("ORGANIZER", response.getRole());
        assertEquals("token", response.getToken());
        assertEquals("PENDING_APPROVAL", response.getAccountStatus());
        verify(studentRepository, never()).save(any(Student.class));
        verify(administratorRepository, never()).save(any(Administrator.class));
    }

    @Test
    void loginRejectsDisabledUser() {
        Organizer organizer = new Organizer.Builder()
                .setUserId("org-1")
                .setEmail("amanda@mycput.ac.za")
                .setPasswordHash("hashed")
                .setRole(RoleEnum.ORGANIZER)
                .build();
        organizer.setDisabled(true);

        when(userRepository.findByEmail("amanda@mycput.ac.za")).thenReturn(Optional.of(organizer));
        when(passwordEncoder.matches("securepass", "hashed")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(loginRequest()));

        assertEquals(403, exception.getStatusCode().value());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginIssuesTokenForEnabledUser() {
        Organizer organizer = new Organizer.Builder()
                .setUserId("org-1")
                .setEmail("amanda@mycput.ac.za")
                .setPasswordHash("hashed")
                .setRole(RoleEnum.ORGANIZER)
                .build();

        when(userRepository.findByEmail("amanda@mycput.ac.za")).thenReturn(Optional.of(organizer));
        when(passwordEncoder.matches("securepass", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(organizer)).thenReturn("token");

        AuthResponse response = authService.login(loginRequest());

        assertEquals("token", response.getToken());
        assertEquals("org-1", response.getUserId());
        assertEquals("ORGANIZER", response.getRole());
    }

    @Test
    void pendingOrganizerCanLogInAndKeepsPendingStatusInResponse() {
        Organizer organizer = new Organizer.Builder()
                .setUserId("org-pending")
                .setEmail("amanda@mycput.ac.za")
                .setPasswordHash("hashed")
                .setRole(RoleEnum.ORGANIZER)
                .build();
        organizer.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        when(userRepository.findByEmail("amanda@mycput.ac.za")).thenReturn(Optional.of(organizer));
        when(passwordEncoder.matches("securepass", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(organizer)).thenReturn("token");

        AuthResponse response = authService.login(loginRequest());

        assertEquals("PENDING_APPROVAL", response.getAccountStatus());
        assertEquals("token", response.getToken());
    }

    @Test
    void adminRegistrationDoesNotRequireAnInviteCode() {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "name", "Admin");
        ReflectionTestUtils.setField(request, "email", "admin@mycput.ac.za");
        ReflectionTestUtils.setField(request, "password", "securepass");
        ReflectionTestUtils.setField(request, "phoneNumber", "0848882617");
        ReflectionTestUtils.setField(request, "role", "ADMIN");
        ReflectionTestUtils.setField(request, "staffNumber", "STF0001");
        when(userRepository.existsByEmail("admin@mycput.ac.za")).thenReturn(false);
        when(passwordEncoder.encode("securepass")).thenReturn("hashed");
        when(administratorRepository.save(any(Administrator.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(Administrator.class))).thenReturn("token");

        AuthResponse response = authService.register(request);

        assertEquals("ADMIN", response.getRole());
        assertEquals("ACTIVE", response.getAccountStatus());
        assertEquals("STF0001", captureSavedAdmin().getStaffNumber());
    }
    @Test
    void studentRegistrationPersistsOnlyAnEncodedPassword() {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "name", "Student Name");
        ReflectionTestUtils.setField(request, "email", "student@mycput.ac.za");
        ReflectionTestUtils.setField(request, "password", "securepass");
        ReflectionTestUtils.setField(request, "phoneNumber", "0848882617");
        ReflectionTestUtils.setField(request, "role", "STUDENT");
        ReflectionTestUtils.setField(request, "studentNumber", "221234567");
        ReflectionTestUtils.setField(request, "faculty", "Applied Sciences");
        ReflectionTestUtils.setField(request, "department", "Chemistry");
        ReflectionTestUtils.setField(request, "yearOfStudy", 2);
        when(userRepository.existsByEmail("student@mycput.ac.za")).thenReturn(false);
        when(passwordEncoder.encode("securepass")).thenReturn("bcrypt-hash");
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken(any(Student.class))).thenReturn("token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        assertEquals("bcrypt-hash", captor.getValue().getPasswordHash());
        assertEquals("ACTIVE", response.getAccountStatus());
        assertFalse(response.toString().contains("bcrypt-hash"));
    }

    @Test
    void duplicateEmailIsRejectedBeforeSaving() {
        when(userRepository.existsByEmail("amanda@mycput.ac.za")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(organizerRequest()));

        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    @Test
    void duplicateStaffNumberIsRejectedBeforeSaving() {
        when(userRepository.existsByEmail("amanda@mycput.ac.za")).thenReturn(false);
        when(userRepository.existsByStaffNumber("STF0147")).thenReturn(true);

        assertThrows(StaffNumberAlreadyExistsException.class,
                () -> authService.register(organizerRequest()));

        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    @Test
    void organizerContactEmailMustUseUniversityDomain() {
        RegisterRequest request = organizerRequest();
        ReflectionTestUtils.setField(request, "contactEmail", "society@example.com");
        when(userRepository.existsByEmail("amanda@mycput.ac.za")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Contact email must be valid and end in .ac.za", exception.getMessage());
        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    @Test
    void unknownRoleAndMissingOrganizerFieldsAreBadRequests() {
        RegisterRequest unknownRole = organizerRequest();
        ReflectionTestUtils.setField(unknownRole, "role", "GUEST");
        assertThrows(IllegalArgumentException.class, () -> authService.register(unknownRole));

        RegisterRequest missingFields = organizerRequest();
        ReflectionTestUtils.setField(missingFields, "staffNumber", null);
        assertThrows(IllegalArgumentException.class, () -> authService.register(missingFields));

        verify(organizerRepository, never()).save(any(Organizer.class));
    }

    private Administrator captureSavedAdmin() {
        ArgumentCaptor<Administrator> captor = ArgumentCaptor.forClass(Administrator.class);
        verify(administratorRepository).save(captor.capture());
        return captor.getValue();
    }

}

package za.ac.cput.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import za.ac.cput.domain.AccountStatus;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.User;
import za.ac.cput.domain.Administrator;
import za.ac.cput.dtos.AuthResponse;
import za.ac.cput.dtos.LoginRequest;
import za.ac.cput.dtos.RegisterRequest;
import za.ac.cput.factory.OrganizerFactory;
import za.ac.cput.factory.StudentFactory;
import za.ac.cput.repository.AdministratorRepository;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.StaffNumberAlreadyExistsException;
import za.ac.cput.exception.InvalidCredentialsException;
import za.ac.cput.repository.OrganizerRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.util.JwtUtil;
import za.ac.cput.util.Helper;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final OrganizerRepository organizerRepository;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       OrganizerRepository organizerRepository,
                       AdministratorRepository administratorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.organizerRepository = organizerRepository;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration details are required");
        }
        validateSharedFields(request);
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        RoleEnum role;
        try {
            role = RoleEnum.valueOf(request.getRole().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role: must be STUDENT, ORGANIZER, or ADMIN");
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User savedUser;

        if (role == RoleEnum.STUDENT) {
            requireValue(request.getStudentNumber(), "Student number is required for students");
            requireValue(request.getFaculty(), "Faculty is required for students");
            requireValue(request.getDepartment(), "Department is required for students");
            requireValue(request.getYearOfStudy(), "Year of study is required for students");
            if (request.getYearOfStudy() < 1 || request.getYearOfStudy() > 6) {
                throw new IllegalArgumentException("Year of study must be between 1 and 6");
            }
            Student student = StudentFactory.createStudent(
                    request.getName(),
                    email,
                    hashedPassword,
                    request.getPhoneNumber(),
                    request.getStudentNumber(),
                    request.getFaculty(),
                    request.getDepartment(),
                    request.getYearOfStudy()
            );
            savedUser = studentRepository.save(student);

        } else if (role == RoleEnum.ORGANIZER) {
            requireValue(request.getStaffNumber(), "Staff number is required for organizers");
            requireValue(request.getOrganizationName(), "Organization name is required");
            requireValue(request.getOrganizationType(), "Organization type is required");
            requireValue(request.getContactEmail(), "Contact email is required");
            validateContactEmail(request.getContactEmail());
            requireUniqueStaffNumber(request.getStaffNumber());
            Organizer organizer = OrganizerFactory.createOrganizer(
                    request.getName(),
                    email,
                    hashedPassword,
                    request.getPhoneNumber(),
                    request.getOrganizationName(),
                    request.getOrganizationType(),
                    request.getContactEmail().trim()
            );
            organizer.setStaffNumber(request.getStaffNumber().trim());
            organizer.setAccountStatus(AccountStatus.PENDING_APPROVAL);
            savedUser = organizerRepository.save(organizer);

        } else if (role == RoleEnum.ADMIN) {
            requireValue(request.getStaffNumber(), "Staff number is required for administrators");
            requireUniqueStaffNumber(request.getStaffNumber());
            Administrator administrator = new Administrator.Builder()
                    .setUserId(Helper.generateId())
                    .setName(request.getName().trim())
                    .setEmail(email)
                    .setPasswordHash(hashedPassword)
                    .setPhoneNumber(request.getPhoneNumber().trim())
                    .setRole(RoleEnum.ADMIN)
                    .build();
            administrator.setStaffNumber(request.getStaffNumber().trim());
            savedUser = administratorRepository.save(administrator);
        } else {
            throw new IllegalArgumentException("Invalid role");
        }

        String token = jwtUtil.generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getRole().name(),
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getAccountStatus()
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }


        // Administrators disable accounts by flagging User.disabled; honour that
        // flag here so a disabled user can never obtain a fresh token. Existing
        // tokens are also rejected by JwtAuthenticationFilter.
        if (user.getAccountStatus() == AccountStatus.DISABLED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "This account has been disabled. Please contact an administrator.");
        }


        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getAccountStatus()
        );
    }

    private void validateSharedFields(RegisterRequest request) {
        requireValue(request.getName(), "Name is required");
        requireValue(request.getEmail(), "Email is required");
        if (!request.getEmail().trim().matches("(?i)^[^@\\s]+@(?:mycput|cput)\\.ac\\.za$")) {
            throw new IllegalArgumentException("Email must end in @mycput.ac.za or @cput.ac.za");
        }
        requireValue(request.getPassword(), "Password is required");
        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        requireValue(request.getPhoneNumber(), "Phone number is required");
        if (!request.getPhoneNumber().trim().matches("^0[6-8]\\d{8}$")) {
            throw new IllegalArgumentException("Phone number must be a valid 10-digit mobile number");
        }
        requireValue(request.getRole(), "Role is required");
    }

    private void requireValue(Object value, String message) {
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void requireUniqueStaffNumber(String staffNumber) {
        if (userRepository.existsByStaffNumber(staffNumber.trim())) {
            throw new StaffNumberAlreadyExistsException(
                    "An account with this staff number already exists");
        }
    }

    private void validateContactEmail(String contactEmail) {
        if (!Helper.isValidEmail(contactEmail.trim())
                || !contactEmail.trim().matches("(?i)^[^@\\s]+@[^@\\s]+\\.ac\\.za$")) {
            throw new IllegalArgumentException("Contact email must be valid and end in .ac.za");
        }
    }

}

package za.ac.cput.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Organizer;
import za.ac.cput.domain.RoleEnum;
import za.ac.cput.domain.Student;
import za.ac.cput.domain.User;
import za.ac.cput.dtos.AuthResponse;
import za.ac.cput.dtos.LoginRequest;
import za.ac.cput.dtos.RegisterRequest;
import za.ac.cput.factory.OrganizerFactory;
import za.ac.cput.factory.StudentFactory;
import za.ac.cput.exception.EmailAlreadyExistsException;
import za.ac.cput.exception.InvalidCredentialsException;
import za.ac.cput.repository.OrganizerRepository;
import za.ac.cput.repository.StudentRepository;
import za.ac.cput.repository.UserRepository;
import za.ac.cput.util.JwtUtil;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       StudentRepository studentRepository,
                       OrganizerRepository organizerRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }


        String hashedPassword = passwordEncoder.encode(request.getPassword());

        RoleEnum role;
        try {
            role = RoleEnum.valueOf(request.getRole().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role: must be STUDENT or ORGANIZER");
        }


        User savedUser;

        if (role == RoleEnum.STUDENT) {
            if (request.getYearOfStudy() == null) {
                throw new IllegalArgumentException("Year of study is required for students");
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
            Organizer organizer = OrganizerFactory.createOrganizer(
                    request.getName(),
                    email,
                    hashedPassword,
                    request.getPhoneNumber(),
                    request.getOrganizationName(),
                    request.getOrganizationType(),
                    null,
                    request.getContactEmail()
            );
            savedUser = organizerRepository.save(organizer);

        } else {

            throw new IllegalArgumentException("Admin accounts cannot be self-registered");
        }

        String token = jwtUtil.generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getRole().name(),
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }


    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));


        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }


        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
                token,
                user.getRole().name(),
                user.getUserId(),
                user.getName(),
                user.getEmail()
        );
    }
}

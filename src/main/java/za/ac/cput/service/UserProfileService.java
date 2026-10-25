package za.ac.cput.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Student;
import za.ac.cput.dtos.StudentProfileResponse;
import za.ac.cput.dtos.StudentProfileUpdateRequest;
import za.ac.cput.dtos.NotificationPreferencesRequest;
import za.ac.cput.exception.UserNotFoundException;
import za.ac.cput.repository.StudentRepository;

@Service
public class UserProfileService {
    private final StudentRepository studentRepository;

    public UserProfileService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(String email) {
        Student student = findStudent(email);
        return StudentProfileResponse.from(student);
    }

    @Transactional
    public StudentProfileResponse updateStudentProfile(String email, StudentProfileUpdateRequest request) {
        Student student = findStudent(email);
        student.updateProfile(
                request.firstName(),
                request.surname(),
                request.phoneNumber(),
                request.residence(),
                request.faculty(),
                request.yearOfStudy()
        );
        return StudentProfileResponse.from(studentRepository.save(student));
    }

    @Transactional
    public StudentProfileResponse updatePreferences(String email, NotificationPreferencesRequest request) {
        Student student = findStudent(email);
        student.updateNotificationPreferences(request.emailReminders(), request.eventUpdates());
        return StudentProfileResponse.from(studentRepository.save(student));
    }

    private Student findStudent(String email) {
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Student profile not found"));
    }
}

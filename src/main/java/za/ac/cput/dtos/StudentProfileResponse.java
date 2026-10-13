package za.ac.cput.dtos;

import za.ac.cput.domain.Student;

public record StudentProfileResponse(
        String userId,
        String firstName,
        String surname,
        String name,
        String email,
        String phoneNumber,
        String studentNumber,
        String residence,
        String faculty,
        String department,
        int yearOfStudy,
        NotificationPreferencesResponse preferences
) {
    public static StudentProfileResponse from(Student student) {
        String[] names = student.getName().trim().split("\\s+", 2);
        String firstName = names.length > 0 ? names[0] : "";
        String surname = names.length > 1 ? names[1] : "";
        return new StudentProfileResponse(
                student.getUserId(),
                firstName,
                surname,
                student.getName(),
                student.getEmail(),
                student.getPhoneNumber(),
                student.getStudentNumber(),
                student.getResidence(),
                student.getFaculty(),
                student.getDepartment(),
                student.getYearOfStudy(),
                new NotificationPreferencesResponse(
                        student.isEmailReminders(),
                        student.isEventUpdates()
                )
        );
    }
}

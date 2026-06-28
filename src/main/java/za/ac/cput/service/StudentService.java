package za.ac.cput.service;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.Student;
import za.ac.cput.repository.StudentRepository;

@Service
public class StudentService implements IStudentService{
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student findByStudentNumber(String studentNumber) {
        return null;
    }

    @Override
    public Student create(Student student) {
        return null;
    }

    @Override
    public Student read(String string) {
        return null;
    }

    @Override
    public Student update(Student student) {
        return null;
    }

    @Override
    public void delete(String string) {

    }
}

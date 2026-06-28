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
        if (student == null){
            return null;
        }
        return studentRepository.save(student);
    }

    @Override
    public Student read(String string) {
        return studentRepository.findById(string).orElse(null);
    }

    @Override
    public Student update(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void delete(String string) {
        studentRepository.deleteById(string);
    }
}

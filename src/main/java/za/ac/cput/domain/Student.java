package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student extends User{
    private String studentNumber;
    private String faculty;
    private String department;
    private int yearOfStudy;

    protected Student(){}

    private Student(Builder builder){

    }

    public static class Builder{
        private Long userId;
        private String name;
        private String email;
        private String passwordHash;
        private String phoneNumber;
        private RoleEnum role;
        private String studentNumber;
        private String faculty;
        private String department;
        private int yearOfStudy;

        public Builder setUserId(Long userId){
            this.userId = userId;
            return this;
        }
        public Builder setName(String name){
            this.name = name;
            return this;
        }
        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }
        public Builder setPasswordHash(String passwordHash){
            this.passwordHash = passwordHash;
            return this;
        }
        public Builder setPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }
        public Builder setRole(RoleEnum role){
            this.role = role;
            return this;
        }
        public Builder setStudentNumber(String studentNumber){
            this.studentNumber = studentNumber;
            return this;
        }
        public Builder setFaculty(String faculty){
            this.faculty = faculty;
            return this;
        }
        public Builder setDepartment(String department){
            this.department = department;
            return this;
        }
        public Builder setYearOfStudy(int yearOfStudy){
            this.yearOfStudy = yearOfStudy;
            return this;
        }
    }
}

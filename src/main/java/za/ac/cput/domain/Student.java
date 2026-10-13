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
    private String residence;
    private boolean emailReminders = true;
    private boolean eventUpdates = true;

    protected Student(){}

    private Student(Builder builder){
        this.userId = builder.userId;
        this.name = builder.name;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.phoneNumber = builder.phoneNumber;
        this.role = builder.role;
        this.studentNumber = builder.studentNumber;
        this.faculty = builder.faculty;
        this.department = builder.department;
        this.yearOfStudy = builder.yearOfStudy;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public String getResidence() {
        return residence;
    }

    public boolean isEmailReminders() {
        return emailReminders;
    }

    public boolean isEventUpdates() {
        return eventUpdates;
    }

    public void updateProfile(String firstName,
                              String surname,
                              String phoneNumber,
                              String residence,
                              String faculty,
                              int yearOfStudy) {
        this.name = (firstName.trim() + " " + surname.trim()).trim();
        this.phoneNumber = phoneNumber;
        this.residence = residence;
        this.faculty = faculty;
        this.yearOfStudy = yearOfStudy;
    }

    public void updateNotificationPreferences(boolean emailReminders, boolean eventUpdates) {
        this.emailReminders = emailReminders;
        this.eventUpdates = eventUpdates;
    }

    public static class Builder{
        private String userId;
        private String name;
        private String email;
        private String passwordHash;
        private String phoneNumber;
        private RoleEnum role;
        private String studentNumber;
        private String faculty;
        private String department;
        private int yearOfStudy;

        public Builder setUserId(String userId){
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
        public Student build() {
            return new Student(this);
        }
    }
}

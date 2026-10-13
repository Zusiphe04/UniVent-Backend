package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@Entity
@Table(name="Organizer")
public class Organizer extends User{
    private String organizationName;
    private String organizationType;
    private String contactEmail;
    @OneToMany(mappedBy = "organizer")
    private List<Event> events;

    public Organizer() {
    }

    public Organizer(Builder builder) {
        this.userId = builder.userId;
        this.name = builder.name;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.phoneNumber = builder.phoneNumber;
        this.role = builder.role;
        this.organizationName = builder.organizationName;
        this.organizationType = builder.organizationType;
        this.contactEmail = builder.contactEmail;
        this.events = builder.events;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "organizationName='" + organizationName + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                '}';
    }

    public static class Builder{
        private String userId;
        private String name;
        private String email;
        private String passwordHash;
        private String phoneNumber;
        private RoleEnum role;
        private String organizationName;
        private String organizationType;
        private String contactEmail;
        private List<Event> events;

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setRole(RoleEnum role) {
            this.role = role;
            return this;
        }
        
        public Builder setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public Builder setOrganizationType(String organizationType) {
            this.organizationType = organizationType;
            return this;
        }

        public Builder setContactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public Builder setEvents(List<Event> events){
            this.events = events;
            return this;
        }


        public Organizer build() {
//            if (this.getUserId() == 0) {
//                this.setUserId(UserIdGenerator.getInstance().generateId());
//            }
            return new Organizer(this);
        }
    }
}

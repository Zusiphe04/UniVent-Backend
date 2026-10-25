package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;
import java.util.List;
/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * Organizer.java
 * Date: 05 July 2026
 **/

@Entity
@Table(name = "Organizer")
public class Organizer extends User {
    private String organizationName;
    private String organizationType;
    private String organizationEmail;
    @Transient
    private List<Event> events;

    public Organizer() {
        setRole(RoleEnum.ORGANIZER);
    }

    public Organizer(Builder builder) {
        setUserId(builder.userId);
        setName(builder.name);
        setEmail(builder.email);
        setPasswordHash(builder.passwordHash);
        setPhoneNumber(builder.phoneNumber);
        setRole(builder.role != null ? builder.role : RoleEnum.ORGANIZER);
        setEnabled(builder.enabled);
        setCreatedAt(builder.createdAt);
        this.organizationName = builder.organizationName;
        this.organizationType = builder.organizationType;
        this.organizationEmail = builder.organizationEmail;
        this.events = builder.events;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    public String getContactEmail() {
        return organizationEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.organizationEmail = contactEmail;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "organizationName='" + organizationName + '\'' +
                ", organizationType='" + organizationType + '\'' +
                ", contactEmail='" + organizationEmail + '\'' +
                '}';
    }

    public static class Builder {
        private String userId;
        private String name;
        private String email;
        private String passwordHash;
        private String phoneNumber;
        private RoleEnum role;
        private boolean enabled = true;
        private LocalDateTime createdAt;
        private String organizationName;
        private String organizationType;
        private String organizationEmail;
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

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
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

        public Builder setOrganizationEmail(String organizationEmail) {
            this.organizationEmail = organizationEmail;
            return this;
        }

        public Builder setContactEmail(String contactEmail) {
            this.organizationEmail = contactEmail;
            return this;
        }

        public Builder setEvents(List<Event> events) {
            this.events = events;
            return this;
        }

        public Organizer build() {
            return new Organizer(this);
        }
    }
}
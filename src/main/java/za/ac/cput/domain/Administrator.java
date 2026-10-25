package za.ac.cput.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 **/

@Entity
@Table(name = "administrator")
public class Administrator extends User {
    private String adminLevel;
    @jakarta.persistence.Transient private List<User> managedUsers;
    @jakarta.persistence.Transient private List<Event> managedEvents;

    public Administrator() {
        setRole(RoleEnum.ADMIN);
    }

    private Administrator(Builder builder) {
        setUserId(builder.userId);
        setName(builder.name);
        setEmail(builder.email);
        setPasswordHash(builder.passwordHash);
        setPhoneNumber(builder.phoneNumber);
        setRole(builder.role != null ? builder.role : RoleEnum.ADMIN);
        setEnabled(builder.enabled);
        setCreatedAt(builder.createdAt);
        this.adminLevel = builder.adminLevel;
        this.managedUsers = builder.managedUsers;
        this.managedEvents = builder.managedEvents;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public List<User> getManagedUsers() {
        return managedUsers;
    }

    public void setManagedUsers(List<User> managedUsers) {
        this.managedUsers = managedUsers;
    }

    public List<Event> getManagedEvents() {
        return managedEvents;
    }

    public void setManagedEvents(List<Event> managedEvents) {
        this.managedEvents = managedEvents;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "adminLevel='" + adminLevel + '\'' +
                ", managedUsers=" + managedUsers +
                ", managedEvents=" + managedEvents +
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
        private String adminLevel;
        private List<User> managedUsers;
        private List<Event> managedEvents;

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

        public Builder setPassword(String password) {
            this.passwordHash = password;
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

        public Builder setAdminLevel(String adminLevel) {
            this.adminLevel = adminLevel;
            return this;
        }

        public Builder setManagedUsers(List<User> managedUsers) {
            this.managedUsers = managedUsers;
            return this;
        }

        public Builder setManagedEvents(List<Event> managedEvents) {
            this.managedEvents = managedEvents;
            return this;
        }

        public Administrator build() {
            return new Administrator(this);
        }
    }
}
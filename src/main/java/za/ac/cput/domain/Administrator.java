package za.ac.cput.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

/**Student name: Amanda Msutu
 * Student number: 222428600
 * Group: 3H
 * AdministratorFactory.java
 * Date: 05 July 2026
 * **/

@Entity
@Table(name ="administrator")
public class Administrator extends User {
    private String adminLevel;
    private List<User> managedUsers;
    private List<Event> managedEvents;


    public Administrator() {
    }

    private Administrator(Builder builder) {
        //super(builder);
        this.adminLevel = builder.adminLevel;
        this.managedUsers = builder.managedUsers;
        this.managedEvents = builder.managedEvents;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public List<User> getManagedUsers() {
        return managedUsers;
    }

    public List<Event> getManagedEvents() {
        return managedEvents;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "adminLevel='" + adminLevel + '\'' +
                ", managedUsers=" + managedUsers +
                ", managedEvents=" + managedEvents +
                '}';
    }

    public static class Builder /*extends User.Builder<Builder>*/ {
        private String adminLevel;
        private List<User> managedUsers;
        private List<Event> managedEvents;

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
//            if (this.getUserId() == 0) {
//                this.setUserId(UserIdGenerator.getInstance().generateId());
//            }
            return new Administrator(this);
        }
    }
}


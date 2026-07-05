package za.ac.cput.domain;

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
@Table(name="Organizer")
public class Organizer extends User{
    private String organizationName;
    private String organizationType;
    private String contactEmail;
    private List<Event> events;

    public Organizer() {
    }

    public Organizer(Builder builder) {
        //super(builder);
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
        private String organizationName;
        private String organizationType;
        private String contactEmail;
        private List<Event> events;

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

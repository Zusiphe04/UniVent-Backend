package za.ac.cput.domain;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    @Id
    protected String userId;
    protected String name;
    protected String email;
    protected String passwordHash;
    protected String phoneNumber;
    @Enumerated(EnumType.STRING)
    protected RoleEnum role;
    @Column(nullable = false)
    protected boolean disabled = false;

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Backward-compatible alias used by current tests.
    public String getPassword() {
        return passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RoleEnum getRole() {
        return role;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}

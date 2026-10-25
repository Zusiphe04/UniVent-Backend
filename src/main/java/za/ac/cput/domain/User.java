package za.ac.cput.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {
    @Id
    protected String userId;
    protected String name;
    protected String email;
    @JsonIgnore
    protected String passwordHash;
    protected String phoneNumber;
    protected String staffNumber;
    @Enumerated(EnumType.STRING)
    protected RoleEnum role;
    @Column(nullable = false)
    protected boolean disabled = false;
    @Enumerated(EnumType.STRING)
    @Column
    protected AccountStatus accountStatus = AccountStatus.ACTIVE;
    protected LocalDateTime createdAt = LocalDateTime.now();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Backward-compatible alias used by current tests.
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) {
        this.staffNumber = staffNumber;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public boolean isDisabled() {
        return getAccountStatus() == AccountStatus.DISABLED;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        accountStatus = disabled ? AccountStatus.DISABLED : AccountStatus.ACTIVE;
    }

    public boolean isEnabled() {
        return getAccountStatus() == AccountStatus.ACTIVE;
    }

    public void setEnabled(boolean enabled) {
        setDisabled(!enabled);
    }

    public AccountStatus getAccountStatus() {
        return accountStatus == null
                ? (disabled ? AccountStatus.DISABLED : AccountStatus.ACTIVE)
                : accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        this.disabled = accountStatus == AccountStatus.DISABLED;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PostLoad
    private void syncAccountStatusFromLegacyColumn() {
        if (accountStatus == null) {
            accountStatus = disabled ? AccountStatus.DISABLED : AccountStatus.ACTIVE;
        } else {
            disabled = accountStatus == AccountStatus.DISABLED;
        }
    }
}

package za.ac.cput.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "admin_invite_codes")
public class AdminInviteCode {
    @Id
    @Column(name = "code_hash", length = 64, nullable = false, updatable = false)
    private String codeHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdminInviteCodeStatus status = AdminInviteCodeStatus.AVAILABLE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant usedAt;

    protected AdminInviteCode() {
    }

    public AdminInviteCode(String codeHash) {
        this.codeHash = codeHash;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public AdminInviteCodeStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUsedAt() {
        return usedAt;
    }

    public boolean markUsed() {
        if (status != AdminInviteCodeStatus.AVAILABLE) {
            return false;
        }
        status = AdminInviteCodeStatus.USED;
        usedAt = Instant.now();
        return true;
    }
}

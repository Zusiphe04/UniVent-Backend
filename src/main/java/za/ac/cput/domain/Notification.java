package za.ac.cput.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    private String notificationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean read;

    protected Notification() {
    }

    public Notification(User recipient, NotificationType type, String title, String message) {
        this.notificationId = UUID.randomUUID().toString();
        this.recipient = recipient;
        this.type = type;
        this.title = title;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markRead() {
        this.read = true;
    }
}

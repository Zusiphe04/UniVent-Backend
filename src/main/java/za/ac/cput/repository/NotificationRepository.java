package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByRecipient_EmailOrderByCreatedAtDesc(String email);
    Optional<Notification> findByNotificationIdAndRecipient_Email(String notificationId, String email);
    long countByRecipient_EmailAndReadFalse(String email);
}

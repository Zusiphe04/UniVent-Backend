package za.ac.cput.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Notification;
import za.ac.cput.dtos.NotificationResponse;
import za.ac.cput.exception.NotificationNotFoundException;
import za.ac.cput.repository.NotificationRepository;

import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(String email) {
        return notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(email).stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        return notificationRepository.countByRecipient_EmailAndReadFalse(email);
    }

    @Transactional
    public NotificationResponse markRead(String notificationId, String email) {
        Notification notification = findOwned(notificationId, email);
        notification.markRead();
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead(String email) {
        notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(email).stream()
                .filter(notification -> !notification.isRead())
                .forEach(Notification::markRead);
    }

    @Transactional
    public void dismiss(String notificationId, String email) {
        notificationRepository.delete(findOwned(notificationId, email));
    }

    private Notification findOwned(String notificationId, String email) {
        return notificationRepository.findByNotificationIdAndRecipient_Email(notificationId, email)
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found"));
    }
}

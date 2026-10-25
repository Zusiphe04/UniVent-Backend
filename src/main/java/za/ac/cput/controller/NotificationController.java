package za.ac.cput.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.dtos.NotificationResponse;
import za.ac.cput.dtos.UnreadCountResponse;
import za.ac.cput.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getNotifications(authentication.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(Authentication authentication) {
        return ResponseEntity.ok(new UnreadCountResponse(
                notificationService.getUnreadCount(authentication.getName())
        ));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(Authentication authentication) {
        notificationService.markAllRead(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable String notificationId,
                                                         Authentication authentication) {
        return ResponseEntity.ok(notificationService.markRead(notificationId, authentication.getName()));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> dismiss(@PathVariable String notificationId,
                                        Authentication authentication) {
        notificationService.dismiss(notificationId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

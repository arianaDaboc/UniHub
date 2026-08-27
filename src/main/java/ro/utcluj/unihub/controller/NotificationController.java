package ro.utcluj.unihub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.unihub.dto.ApiDtos.NotificationResponse;
import ro.utcluj.unihub.dto.ApiDtos.NotificationSummary;
import ro.utcluj.unihub.service.CurrentUserService;
import ro.utcluj.unihub.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notifications;
    private final CurrentUserService current;
    public NotificationController(NotificationService notifications, CurrentUserService current) {
        this.notifications = notifications;
        this.current = current;
    }

    @GetMapping
    NotificationSummary list(Authentication auth) { return notifications.list(current.get(auth)); }

    @PatchMapping("/{id}/read")
    NotificationResponse read(@PathVariable("id") Long id, Authentication auth) {
        return notifications.markRead(id, current.get(auth));
    }

    @PatchMapping("/read-all")
    void readAll(Authentication auth) { notifications.markAllRead(current.get(auth)); }

}

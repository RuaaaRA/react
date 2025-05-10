package lms.step1.Controller;

import lms.step1.DTO.NotificationDTO;
import lms.step1.Model.Course;
import lms.step1.Service.CourseService;
import lms.step1.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    
    private final CourseService courseService;





    @PostMapping("/course-update/{courseId}")
    public ResponseEntity<String> sendCourseUpdateNotification(@PathVariable Long courseId) {
        log.info("🔔 Sending course update notification for courseId={}", courseId);

        try {
            notificationService.notifyCourseUpdate(courseId);
            log.info("✅ Notification successfully sent for courseId={}", courseId);
            return ResponseEntity.ok("✅ Notification sent for course update.");
        } catch (Exception e) {
            log.error("❌ Failed to send notification for courseId={}: {}", courseId, e.getMessage());
            return ResponseEntity.internalServerError().body("❌ Failed to send notification.");
        }
    }

    @PreAuthorize("hasAnyAuthority('INSTRUCTOR','ADMIN')")
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    








    






}
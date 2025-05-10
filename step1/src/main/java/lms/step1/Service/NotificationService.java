package lms.step1.Service;

import lms.step1.DTO.NotificationDTO;
import lms.step1.Model.Course;
import lms.step1.Repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final CourseRepository courseRepository;
    

    private final List<NotificationDTO> notificationStore = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public void notifyCourseUpdate(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        String subject = "Course Update: " + course.getTitle();
        String text = "The course \"" + course.getTitle() + "\" has been updated.";

        emailService.sendEmail("user@example.com", subject, text);

        NotificationDTO notification = new NotificationDTO(
                idGenerator.getAndIncrement(),
                "Course Updated",
                text,
                false,
                "course",
                course.getTitle()
        );

        notificationStore.add(notification);
        log.info("✅ Notification stored: {}", notification);
    }

 

    public void markAsRead(Long id) {
        notificationStore.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .ifPresent(n -> n.setRead(true));
    }

    public void markAllAsRead() {
        notificationStore.forEach(n -> n.setRead(true));
    }
        
    
        public void notifyCourseUpdate(Course course) {
            String message = String.format("تم تحديث الكورس '%s' بواسطة المدرب", course.getTitle());
            
            NotificationDTO notification = new NotificationDTO(
                idGenerator.getAndIncrement(),
                "تحديث الكورس",
                message,
                false,
                "course",
                course.getTitle()
            );
            
            notificationStore.add(notification);
            log.info("تم إضافة إشعار جديد: {}", notification);
        }
    
        public List<NotificationDTO> getAllNotifications() {
            return new ArrayList<>(notificationStore);
        }
}
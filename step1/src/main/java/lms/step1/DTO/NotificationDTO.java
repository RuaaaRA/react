package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private boolean read;
    private String type;
    private String courseName; // 🔹 أضف هذا الحقل

    
}

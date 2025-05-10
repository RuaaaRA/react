package lms.step1.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionDTO {
    private Long id;
    private Long studentId; // 🔥 أضفنا حقل رقم الطالب
    private String solutionText;
    private String solutionFileUrl;
    private LocalDateTime submissionDate;
}

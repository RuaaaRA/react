package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressDTO {

    @NotNull(message = "Lesson ID must not be null")
    private Long lessonId;

    @NotNull(message = "Course ID must not be null")
    private Long courseId;

    @NotBlank(message = "Lesson title must not be blank")
    @Size(min = 3, max = 100, message = "Lesson title must be between 3 and 100 characters")
    private String lessonTitle;

    @NotBlank(message = "Student username must not be blank")
    @Size(min = 3, max = 50, message = "Student username must be between 3 and 50 characters")
    private String studentUsername;

    private boolean completed;
}

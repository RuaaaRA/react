package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentStatusDTO {

    @NotNull(message = "Course ID must not be null")
    private Long courseId;

    @NotBlank(message = "Course title must not be blank")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    private String courseTitle;

    private boolean completed;

    @NotBlank(message = "Enrolled date must not be blank")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Enrolled date must follow the format yyyy-MM-dd")
    private String enrolledAt;

    @NotBlank(message = "Student username must not be blank")
    @Size(min = 3, max = 50, message = "Student username must be between 3 and 50 characters")
    private String studentUsername;
}

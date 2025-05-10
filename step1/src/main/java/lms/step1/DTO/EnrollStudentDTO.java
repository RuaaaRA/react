package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EnrollStudentDTO {

    @NotBlank(message = "Student username must not be blank")
    @Size(min = 3, max = 50, message = "Student username must be between 3 and 50 characters")
    private String studentUsername;

    @NotNull(message = "Course ID must not be null")
    private Long courseId;
}

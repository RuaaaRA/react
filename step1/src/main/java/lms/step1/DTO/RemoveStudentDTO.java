package lms.step1.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RemoveStudentDTO {

    @NotBlank(message = "Student username must not be blank")
    private String studentUsername;

    @NotNull(message = "Course ID must not be null")
    private Long courseId;
}

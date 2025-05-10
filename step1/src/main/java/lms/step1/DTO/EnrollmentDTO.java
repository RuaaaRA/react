package lms.step1.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {

    @NotNull(message = "Course ID must not be null")
    private Long courseId;
}

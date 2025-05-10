package lms.step1.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignInstructorDTO {

    @NotNull(message = "Course ID must not be null")
    @Positive(message = "Course ID must be a positive number")
    private Long courseId;

    @NotNull(message = "Instructor ID must not be null")
    @Positive(message = "Instructor ID must be a positive number")
    private Long instructorId;
}

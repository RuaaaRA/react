package lms.step1.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import org.springframework.hateoas.RepresentationModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO extends RepresentationModel<AssignmentDTO> {

    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    @Min(value = 0, message = "Max score must be a non-negative value")
    private int maxScore; // تم التغيير من totalMarks إلى maxScore

    private Long courseId;
}
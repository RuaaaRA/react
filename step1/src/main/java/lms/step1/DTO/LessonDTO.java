package lms.step1.DTO;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDTO {

    @NotNull(message = "Lesson ID must not be null")
    private Long id;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @NotNull(message = "Course ID must not be null")
    private Long courseId;

    private String videoUrl;

    private String pdfUrl;

    private String audioUrl;

    private MultipartFile videoFile;

    private MultipartFile pdfFile;

    private MultipartFile audioFile;
}

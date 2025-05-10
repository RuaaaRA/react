package lms.step1.DTO;

import jakarta.validation.constraints.*;

public record GradeDTO(

    @NotNull(message = "Student ID must not be null")
    Long studentId,

    @NotNull(message = "Quiz or Assignment ID must not be null")
    Long quizOrAssignmentId,

    @DecimalMin(value = "0.0", inclusive = true, message = "Score must be at least 0.0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Score must not exceed 100.0")
    double score
) {}
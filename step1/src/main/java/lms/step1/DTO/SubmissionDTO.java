package lms.step1.DTO;

import java.time.LocalDateTime;

public record SubmissionDTO(
    Long studentId,
    String studentName,
    LocalDateTime submissionDate,
    Integer score,
    String quizTitle
) {}
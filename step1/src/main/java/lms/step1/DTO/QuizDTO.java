package lms.step1.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record QuizDTO(
        Long id,
        String title,
        String description,
        Long courseId,
        List<QuestionDTO> questions,
        Boolean isClosed,
        Boolean isSubmitted,
        Integer studentScore,
        List<SubmissionDTO> submissions,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
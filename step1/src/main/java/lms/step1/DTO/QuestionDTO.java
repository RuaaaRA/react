package lms.step1.DTO;

import lms.step1.Model.QuestionType;
import java.util.List;

public record QuestionDTO(
            Long id, // أضفنا حقل ID

    String text,
    String correctAnswer,
    List<String> options,
    QuestionType questionType,
    int points,
    String explanation
) {}
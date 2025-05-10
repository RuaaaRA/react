package lms.step1.Service;

import lms.step1.Model.Question;
import lms.step1.Model.QuestionType;
import lms.step1.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoGradingService {

    private final QuestionRepository questionRepository;

    public int gradeQuiz(Long quizId, Map<Long, String> userAnswers) {
        log.info("🔍 Grading quiz with ID: {}", quizId);

        int totalPoints = 0;
        int earnedPoints = 0;

        for (Map.Entry<Long, String> entry : userAnswers.entrySet()) {
            Long questionId = entry.getKey();
            String userAnswer = entry.getValue();

            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found: " + questionId));

            totalPoints += question.getPoints();

            if (isAnswerCorrect(question, userAnswer)) {
                earnedPoints += question.getPoints();
                log.info("✅ Correct answer for Question ID {}", questionId);
            } else {
                log.info("❌ Incorrect answer for Question ID {}", questionId);
            }
        }

        int score = (totalPoints > 0) ? (earnedPoints * 100) / totalPoints : 0;
        log.info("📝 Quiz grading complete. Final Score: {}%", score);
        return score;
    }

    private boolean isAnswerCorrect(Question question, String userAnswer) {
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }

        switch (question.getQuestionType()) {
            case TRUE_FALSE:
                return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
            
            case MULTIPLE_CHOICE:
                return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
            
            case SHORT_ANSWER:
                // For short answer, we can do case-insensitive comparison
                // or more advanced matching if needed
                return question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
            
            default:
                return false;
        }
    }
}
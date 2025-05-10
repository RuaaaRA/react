package lms.step1.Exception;

public class QuizEndedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QuizEndedException(Long quizId) {
        super("Quiz has ended (ID: " + quizId + ")");
    }
}
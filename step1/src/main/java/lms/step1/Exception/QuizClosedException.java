package lms.step1.Exception;

public class QuizClosedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QuizClosedException(Long quizId) {
        super("Quiz is closed (ID: " + quizId + ")");
    }
}
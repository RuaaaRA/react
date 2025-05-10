package lms.step1.Exception;

public class QuizNotStartedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QuizNotStartedException(Long quizId) {
        super("Quiz has not started yet (ID: " + quizId + ")");
    }
}
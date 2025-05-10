package lms.step1.Exception;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(Long id) {
        super("Quiz not found with ID: " + id);
    }
    public QuizNotFoundException(String message) {
        super(message);
    }
}
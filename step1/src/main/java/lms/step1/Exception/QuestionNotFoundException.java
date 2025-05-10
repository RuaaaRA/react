package lms.step1.Exception;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Long questionId) {
        super("Question not found with ID: " + questionId);
    }
}
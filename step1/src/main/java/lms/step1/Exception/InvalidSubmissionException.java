package lms.step1.Exception;

public class InvalidSubmissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidSubmissionException(String message) {
        super("Invalid submission: " + message);
    }
}
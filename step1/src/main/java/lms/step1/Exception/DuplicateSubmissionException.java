package lms.step1.Exception;

public class DuplicateSubmissionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateSubmissionException(Long studentId, Long quizId) {
        super("Student " + studentId + " already submitted quiz " + quizId);
    }
}
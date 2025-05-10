package lms.step1.Exception;

public class AssignmentNotFoundException extends RuntimeException {
    public AssignmentNotFoundException(Long id) {
        super("Assignment not found with ID: " + id);
    }
}
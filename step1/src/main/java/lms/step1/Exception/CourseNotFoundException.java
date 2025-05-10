package lms.step1.Exception;

public class CourseNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CourseNotFoundException(Long courseId) {
        super("Course not found with ID: " + courseId);
    }

    public CourseNotFoundException(String message) {
        super(message);
    }
}
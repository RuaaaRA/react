package lms.step1.Exception;

public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String title) {
        super("Course with title '" + title + "' already exists.");
    }
}

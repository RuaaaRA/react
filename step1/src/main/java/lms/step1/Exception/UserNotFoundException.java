package lms.step1.Exception;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }
}
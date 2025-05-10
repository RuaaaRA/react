package lms.step1.Exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // الدالة الموحدة لـ CourseNotFoundException
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<String> handleCourseNotFound(CourseNotFoundException ex) {
        log.warn("Course not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + ex.getMessage());
    }

    @ExceptionHandler(AssignmentNotFoundException.class)
    public ResponseEntity<String> handleAssignmentNotFound(AssignmentNotFoundException ex) {
        log.warn("Assignment not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + ex.getMessage());
    }

    @ExceptionHandler(QuizNotFoundException.class)
    public ResponseEntity<String> handleQuizNotFound(QuizNotFoundException ex) {
        log.warn("Quiz not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + ex.getMessage());
    }

    @ExceptionHandler(CourseAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleCourseAlreadyExists(CourseAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        String message = "Course title already exists.";
        error.put("title", message);
        log.warn("Validation error - Field: title | Message: {}", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        String field = "token";
        String message = "Access token is missing or invalid.";
        errors.put(field, message);
        log.warn("Validation error - Field: {} | Message: {}", field, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        String field = "username";
        String message = "Username is already taken.";
        error.put(field, message);
        log.warn("Validation error - Field: {} | Message: {}", field, message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTokenException(InvalidTokenException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        String field = "token";
        String message = ex.getMessage();
        log.warn("Validation error - Field: {} | Message: {}", field, message);
        errors.put(field, message);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Bad credentials provided.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("❌ Invalid username or password.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("🚫 Access Denied: You do not have permission to perform this action.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
            log.warn("Validation error - Field: {} | Message: {}", fieldName, message);
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error("Unexpected server error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("⚠️ An unexpected error occurred. Please try again later.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateSubmissionException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateSubmissionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
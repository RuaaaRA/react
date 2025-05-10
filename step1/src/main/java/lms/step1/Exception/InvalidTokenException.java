package lms.step1.Exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception thrown when a JWT token is invalid, malformed, expired, or unsupported.
 */
public class InvalidTokenException extends AuthenticationException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
}
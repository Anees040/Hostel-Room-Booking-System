package exceptions;

/**
 * Thrown when an unauthorized access attempt is detected.
 * This is a runtime exception — unchecked.
 */
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs with a detail message.
     *
     * @param message detail message
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    /**
     * Constructs with a detail message and cause.
     *
     * @param message detail message
     * @param cause   root cause
     */
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

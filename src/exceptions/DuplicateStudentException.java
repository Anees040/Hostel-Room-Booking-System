package exceptions;

/**
 * Thrown when attempting to register a student with an existing ID.
 */
public class DuplicateStudentException extends Exception {

    /**
     * Constructs with a detail message.
     *
     * @param message detail message
     */
    public DuplicateStudentException(String message) {
        super(message);
    }

    /**
     * Constructs with a detail message and cause.
     *
     * @param message detail message
     * @param cause   root cause
     */
    public DuplicateStudentException(String message, Throwable cause) {
        super(message, cause);
    }
}

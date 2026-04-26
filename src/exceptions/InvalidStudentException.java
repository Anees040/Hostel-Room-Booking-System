package exceptions;

/**
 * Raised when student registration input is invalid.
 */
public class InvalidStudentException extends Exception {

    /**
     * Constructs the exception with a validation message.
     *
     * @param message validation detail
     */
    public InvalidStudentException(String message) {
        super(message);
    }
}

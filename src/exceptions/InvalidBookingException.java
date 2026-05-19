package exceptions;

/**
 * Thrown when a booking operation contains invalid data.
 */
public class InvalidBookingException extends Exception {

    /**
     * Constructs with a detail message.
     *
     * @param message detail message
     */
    public InvalidBookingException(String message) {
        super(message);
    }

    /**
     * Constructs with a detail message and cause.
     *
     * @param message detail message
     * @param cause   root cause
     */
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}

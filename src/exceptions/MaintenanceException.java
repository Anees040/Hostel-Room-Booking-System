package exceptions;

/**
 * Thrown when a maintenance operation encounters an error.
 */
public class MaintenanceException extends Exception {

    /**
     * Constructs with a detail message.
     *
     * @param message detail message
     */
    public MaintenanceException(String message) {
        super(message);
    }

    /**
     * Constructs with a detail message and cause.
     *
     * @param message detail message
     * @param cause   root cause
     */
    public MaintenanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

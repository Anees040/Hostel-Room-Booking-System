package exceptions;

/**
 * Raised when a booking attempt targets an unavailable room.
 */
public class RoomNotAvailableException extends Exception {

    /**
     * Constructs with a detail message.
     *
     * @param message detail message
     */
    public RoomNotAvailableException(String message) {
        super(message);
    }
}

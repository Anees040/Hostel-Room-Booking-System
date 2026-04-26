package exceptions;

/**
 * Raised when a booking attempt targets an unavailable room.
 */
public class RoomNotAvailableException extends Exception {

    /**
     * Constructs the exception with room-specific context.
     *
     * @param roomNumber room identifier
     */
    public RoomNotAvailableException(String roomNumber) {
        super("Room " + roomNumber + " is not available for booking.");
    }
}

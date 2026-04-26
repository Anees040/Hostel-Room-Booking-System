package interfaces;

/**
 * Defines booking and cancellation behavior for booking-capable services.
 */
public interface Bookable {

    /**
     * Attempts to create a booking for a student and room.
     *
     * @param studentId the student identifier
     * @param roomNumber the room number
     * @return true if booking succeeds, otherwise false
     */
    boolean book(String studentId, String roomNumber);

    /**
     * Attempts to cancel an existing booking.
     *
     * @param bookingId the booking identifier
     * @return true if cancellation succeeds, otherwise false
     */
    boolean cancel(String bookingId);

    /**
     * Returns a current status summary.
     *
     * @return textual status
     */
    String getStatus();
}

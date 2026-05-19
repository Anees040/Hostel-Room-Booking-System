package utils;

/**
 * Utility class for generating formatted IDs.
 * Demonstrates static utility methods (Lab 02/03 concept).
 */
public class IdGenerator {

    private IdGenerator() {
        // Utility class — no instances needed
    }

    /**
     * Generates a booking ID in format BK001.
     *
     * @param counter the current counter value
     * @return formatted booking ID
     */
    public static String generateBookingId(int counter) {
        return "BK" + String.format("%03d", counter);
    }

    /**
     * Generates a maintenance request ID in format MR001.
     *
     * @param counter the current counter value
     * @return formatted request ID
     */
    public static String generateRequestId(int counter) {
        return "MR" + String.format("%03d", counter);
    }

    /**
     * Generates a review ID in format RV001.
     *
     * @param counter the current counter value
     * @return formatted review ID
     */
    public static String generateReviewId(int counter) {
        return "RV" + String.format("%03d", counter);
    }

    /**
     * Generates a notification ID in format NT001.
     *
     * @param counter the current counter value
     * @return formatted notification ID
     */
    public static String generateNotificationId(int counter) {
        return "NT" + String.format("%03d", counter);
    }

    /**
     * Generates a student ID based on department/program info.
     *
     * @param department department abbreviation
     * @param program    program abbreviation
     * @param year       enrollment year (e.g. 24)
     * @param counter    sequential counter
     * @return formatted student ID (e.g. SP24-BSE-001)
     */
    public static String generateStudentId(String department, String program, int year, int counter) {
        String prog = program == null ? "GEN" : program.replaceAll("\\s+", "").toUpperCase();
        if (prog.length() > 3) {
            prog = prog.substring(0, 3);
        }
        return "SP" + year + "-" + prog + "-" + String.format("%03d", counter);
    }
}

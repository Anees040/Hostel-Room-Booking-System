package utils;

/**
 * Utility class for input validation across the system.
 * Demonstrates encapsulated validation logic (Lab 03 concept).
 */
public class ValidationUtils {

    private ValidationUtils() {
        // Utility class — no instances needed
    }

    /**
     * Validates a Pakistani mobile number (11 digits).
     *
     * @param contact the contact number string
     * @return true if valid
     */
    public static boolean isValidContactNumber(String contact) {
        if (contact == null) {
            return false;
        }
        return contact.trim().matches("\\d{11}");
    }

    /**
     * Validates that a password has at least 8 characters.
     *
     * @param password the password string
     * @return true if valid
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    /**
     * Validates that semester is between 1 and 8 inclusive.
     *
     * @param semester the semester number
     * @return true if valid
     */
    public static boolean isValidSemester(int semester) {
        return semester >= 1 && semester <= 8;
    }

    /**
     * Validates that a string is non-null and non-blank.
     *
     * @param value the string to check
     * @return true if non-empty
     */
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates that a price is a positive number.
     *
     * @param price the price value
     * @return true if valid
     */
    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    /**
     * Validates that a floor number is between 1 and 10 inclusive.
     *
     * @param floor the floor number
     * @return true if valid
     */
    public static boolean isValidFloor(int floor) {
        return floor >= 1 && floor <= 10;
    }

    /**
     * Validates that a rating is between 1 and 5 inclusive.
     *
     * @param rating the rating value
     * @return true if valid
     */
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}

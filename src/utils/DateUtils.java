package utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for date operations used across the system.
 * Demonstrates use of java.time API (Lab 12 concept).
 */
public class DateUtils {

    private DateUtils() {
        // Utility class — no instances needed
    }

    /**
     * Returns today's date as a yyyy-MM-dd string.
     *
     * @return today's date string
     */
    public static String today() {
        return LocalDate.now().toString();
    }

    /**
     * Validates that a string is in yyyy-MM-dd format.
     *
     * @param date the date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(date.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if checkOut is strictly after checkIn.
     *
     * @param checkIn  check-in date string
     * @param checkOut check-out date string
     * @return true if check-out is after check-in
     */
    public static boolean isCheckOutAfterCheckIn(String checkIn, String checkOut) {
        try {
            return LocalDate.parse(checkOut).isAfter(LocalDate.parse(checkIn));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the number of days between two dates.
     *
     * @param checkIn  check-in date string
     * @param checkOut check-out date string
     * @return number of days
     */
    public static long daysBetween(String checkIn, String checkOut) {
        try {
            return ChronoUnit.DAYS.between(LocalDate.parse(checkIn), LocalDate.parse(checkOut));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculates total cost based on price per month and stay duration.
     * Cost = (days / 30.0) * pricePerMonth, rounded to 2 decimal places.
     *
     * @param pricePerMonth monthly room price
     * @param checkIn       check-in date string
     * @param checkOut      check-out date string
     * @return calculated total cost
     */
    public static double calculateTotalCost(double pricePerMonth, String checkIn, String checkOut) {
        long days = daysBetween(checkIn, checkOut);
        double cost = (days / 30.0) * pricePerMonth;
        return new BigDecimal(cost).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Checks whether two date ranges overlap.
     *
     * @param start1 first range start
     * @param end1   first range end
     * @param start2 second range start
     * @param end2   second range end
     * @return true if the ranges overlap
     */
    public static boolean hasDateOverlap(String start1, String end1, String start2, String end2) {
        try {
            LocalDate s1 = LocalDate.parse(start1);
            LocalDate e1 = LocalDate.parse(end1);
            LocalDate s2 = LocalDate.parse(start2);
            LocalDate e2 = LocalDate.parse(end2);
            return s1.isBefore(e2) && s2.isBefore(e1);
        } catch (Exception e) {
            return false;
        }
    }
}

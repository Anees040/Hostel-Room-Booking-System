package services;

import exceptions.InvalidBookingException;
import exceptions.RoomNotAvailableException;
import interfaces.Saveable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.AbstractRoom;
import models.Booking;
import models.Student;
import utils.DateUtils;
import utils.IdGenerator;

/**
 * Manages room booking lifecycle and persistence.
 * Implements Saveable (Lab 12) interface.
 */
public class BookingManager implements Saveable {
    private final List<Booking> bookings = new ArrayList<>();
    private final Map<String, Booking> bookingMap = new HashMap<>();
    private static final String FILE = "data/bookings.txt";
    private int bookingCounter = 1;

    private final RoomManager roomManager;
    private final StudentManager studentManager;
    private final NotificationManager notificationManager;

    /**
     * Constructs booking manager with all dependencies.
     *
     * @param roomManager         room manager dependency
     * @param studentManager      student manager dependency
     * @param notificationManager notification manager dependency
     */
    public BookingManager(RoomManager roomManager, StudentManager studentManager,
                          NotificationManager notificationManager) {
        this.roomManager = roomManager;
        this.studentManager = studentManager;
        this.notificationManager = notificationManager;
    }

    /**
     * Backwards-compatible constructor without NotificationManager.
     *
     * @param roomManager    room manager dependency
     * @param studentManager student manager dependency
     */
    public BookingManager(RoomManager roomManager, StudentManager studentManager) {
        this(roomManager, studentManager, new NotificationManager());
    }

    /**
     * Creates a booking and marks room unavailable.
     *
     * @param student  student entity
     * @param room     room entity
     * @param checkIn  check-in date (yyyy-MM-dd)
     * @param checkOut check-out date (yyyy-MM-dd)
     * @return created booking
     * @throws RoomNotAvailableException if room is already booked
     * @throws InvalidBookingException   if dates are invalid
     */
    public Booking createBooking(Student student, AbstractRoom room,
                                 String checkIn, String checkOut)
            throws RoomNotAvailableException, InvalidBookingException {
        if (student == null || room == null) {
            throw new InvalidBookingException("Student and room must not be null.");
        }

        // Validate dates
        if (!DateUtils.isValidDate(checkIn)) {
            throw new InvalidBookingException("Invalid check-in date: " + checkIn + ". Use yyyy-MM-dd format.");
        }
        if (!DateUtils.isValidDate(checkOut)) {
            throw new InvalidBookingException("Invalid check-out date: " + checkOut + ". Use yyyy-MM-dd format.");
        }
        if (!DateUtils.isCheckOutAfterCheckIn(checkIn, checkOut)) {
            throw new InvalidBookingException("Check-out date must be after check-in date.");
        }

        // Check for date overlap with existing active bookings for same room
        for (Booking existing : bookings) {
            if (existing.getRoom() != null
                    && existing.getRoom().getRoomNumber().equalsIgnoreCase(room.getRoomNumber())
                    && existing.isActive()) {
                if (DateUtils.hasDateOverlap(checkIn, checkOut,
                        existing.getCheckInDate(), existing.getCheckOutDate())) {
                    throw new RoomNotAvailableException("Room " + room.getRoomNumber()
                            + " is already booked for overlapping dates.");
                }
            }
        }

        if (!room.isAvailable()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not available.");
        }

        String bookingId = IdGenerator.generateBookingId(bookingCounter++);
        String bookingDate = DateUtils.today();
        Booking booking = new Booking(bookingId, student, room, bookingDate, checkIn, checkOut, "Active");

        bookings.add(booking);
        bookingMap.put(bookingId, booking);
        room.setAvailable(false);

        save();
        roomManager.save();

        notificationManager.sendNotification(student.getId(),
                "Your booking " + bookingId + " for room " + room.getRoomNumber() + " is confirmed.");

        return booking;
    }

    /**
     * Cancels an active booking.
     *
     * @param bookingId booking identifier
     * @return true if cancelled
     */
    public boolean cancelBooking(String bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking == null) {
            return false;
        }
        if (!"Active".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        booking.setStatus("Cancelled");
        if (booking.getRoom() != null) {
            booking.getRoom().setAvailable(true);
            roomManager.save();
        }

        save();
        notificationManager.sendNotification(
                booking.getStudent() != null ? booking.getStudent().getId() : "ADMIN",
                "Booking " + bookingId + " has been cancelled.");
        return true;
    }

    /**
     * Returns bookings for a specific student.
     *
     * @param studentId student identifier
     * @return booking list
     */
    public List<Booking> getBookingsByStudent(String studentId) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getStudent() != null
                    && booking.getStudent().getId().equalsIgnoreCase(studentId)) {
                result.add(booking);
            }
        }
        return result;
    }

    /**
     * Returns all bookings.
     *
     * @return list of all bookings
     */
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    /**
     * Finds a booking by id.
     *
     * @param bookingId booking id
     * @return booking or null
     */
    public Booking findBooking(String bookingId) {
        if (bookingId == null) {
            return null;
        }
        return bookingMap.get(bookingId);
    }

    /**
     * Returns the count of active bookings.
     *
     * @return active booking count
     */
    public int getActiveBookingsCount() {
        int count = 0;
        for (Booking b : bookings) {
            if (b.isActive()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates total revenue from all active bookings.
     *
     * @return total revenue in Rs.
     */
    public double getTotalRevenue() {
        double total = 0;
        for (Booking b : bookings) {
            if (b.isActive() && b.getRoom() != null) {
                total += DateUtils.calculateTotalCost(
                        b.getRoom().getPricePerMonth(),
                        b.getCheckInDate(),
                        b.getCheckOutDate());
            }
        }
        return total;
    }

    @Override
    public void save() {
        List<String> lines = new ArrayList<>();
        for (Booking booking : bookings) {
            String studentId = booking.getStudent() == null ? "" : booking.getStudent().getId();
            String roomNumber = booking.getRoom() == null ? "" : booking.getRoom().getRoomNumber();
            lines.add(booking.getBookingId() + "|"
                    + sanitize(studentId) + "|"
                    + sanitize(roomNumber) + "|"
                    + sanitize(booking.getBookingDate()) + "|"
                    + sanitize(booking.getCheckInDate()) + "|"
                    + sanitize(booking.getCheckOutDate()) + "|"
                    + sanitize(booking.getStatus()));
        }
        FileManager.writeToFile(FILE, lines);
    }

    @Override
    public void load() {
        bookings.clear();
        bookingMap.clear();

        List<String> lines = FileManager.readFromFile(FILE);
        int maxCounter = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                continue;
            }

            String bookingId = parts[0].trim();
            String studentId = parts[1].trim();
            String roomNumber = parts[2].trim();
            String bookingDate = parts[3].trim();
            String checkIn = parts[4].trim();
            String checkOut = parts[5].trim();
            String status = parts[6].trim();

            Student student = studentManager.findStudent(studentId);
            AbstractRoom room = roomManager.findRoom(roomNumber);

            if (student == null || room == null) {
                continue;
            }

            Booking booking = new Booking(bookingId, student, room, bookingDate, checkIn, checkOut, status);
            bookings.add(booking);
            bookingMap.put(bookingId, booking);

            if ("Active".equalsIgnoreCase(status)) {
                room.setAvailable(false);
            }

            int parsedCounter = parseCounter(bookingId);
            if (parsedCounter > maxCounter) {
                maxCounter = parsedCounter;
            }
        }
        bookingCounter = maxCounter + 1;
    }

    private int parseCounter(String bookingId) {
        try {
            String number = bookingId.replaceAll("[^0-9]", "");
            return number.isEmpty() ? 0 : Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/");
    }
}

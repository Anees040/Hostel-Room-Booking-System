package services;

import exceptions.RoomNotAvailableException;
import interfaces.Bookable;
import interfaces.Saveable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.AbstractRoom;
import models.Booking;
import models.Student;

/**
 * Manages room booking lifecycle and persistence.
 */
public class BookingManager implements Saveable, Bookable {
    private final List<Booking> bookings = new ArrayList<>();
    private final Map<String, Booking> bookingMap = new HashMap<>();
    private static final String FILE = "data/bookings.txt";
    private int bookingCounter = 1;

    private final RoomManager roomManager;
    private final StudentManager studentManager;

    /**
     * Constructs booking manager with dependencies.
     *
     * @param roomManager room manager dependency
     * @param studentManager student manager dependency
     */
    public BookingManager(RoomManager roomManager, StudentManager studentManager) {
        this.roomManager = roomManager;
        this.studentManager = studentManager;
        load();
    }

    /**
     * Creates a booking and marks room unavailable.
     *
     * @param student student entity
     * @param room room entity
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @return created booking
     * @throws RoomNotAvailableException if room is already booked
     */
    public Booking createBooking(Student student, AbstractRoom room,
                                 String checkIn, String checkOut)
            throws RoomNotAvailableException {
        if (student == null || room == null) {
            return null;
        }
        if (!room.isAvailable()) {
            throw new RoomNotAvailableException(room.getRoomNumber());
        }

        String bookingId = generateBookingId();
        String bookingDate = LocalDate.now().toString();
        Booking booking = new Booking(bookingId, student, room, bookingDate, checkIn, checkOut, "Active");

        bookings.add(booking);
        bookingMap.put(bookingId, booking);
        room.setAvailable(false);

        save();
        roomManager.save();
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
        }

        save();
        roomManager.save();
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

    @Override
    public boolean book(String studentId, String roomNumber) {
        Student student = studentManager.findStudent(studentId);
        AbstractRoom room = roomManager.findRoom(roomNumber);

        if (student == null || room == null) {
            return false;
        }

        try {
            createBooking(student, room,
                    LocalDate.now().toString(),
                    LocalDate.now().plusMonths(1).toString());
            return true;
        } catch (RoomNotAvailableException e) {
            return false;
        }
    }

    @Override
    public boolean cancel(String bookingId) {
        return cancelBooking(bookingId);
    }

    @Override
    public String getStatus() {
        int active = 0;
        for (Booking booking : bookings) {
            if ("Active".equalsIgnoreCase(booking.getStatus())) {
                active++;
            }
        }
        return "Total Bookings: " + bookings.size() + " | Active: " + active;
    }

    @Override
    public final void save() {
        List<String> lines = new ArrayList<>();
        for (Booking booking : bookings) {
            String studentId = booking.getStudent() == null ? "" : booking.getStudent().getId();
            String roomNumber = booking.getRoom() == null ? "" : booking.getRoom().getRoomNumber();
            String line = booking.getBookingId() + "|"
                    + studentId + "|"
                    + roomNumber + "|"
                    + nullSafe(booking.getBookingDate()) + "|"
                    + nullSafe(booking.getCheckInDate()) + "|"
                    + nullSafe(booking.getCheckOutDate()) + "|"
                    + nullSafe(booking.getStatus());
            lines.add(line);
        }
        FileManager.writeToFile(FILE, lines);
    }

    @Override
    public final void load() {
        bookings.clear();
        bookingMap.clear();

        List<String> lines = FileManager.readFromFile(FILE);
        int maxCounter = 0;

        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                continue;
            }

            String bookingId = parts[0];
            String studentId = parts[1];
            String roomNumber = parts[2];
            String bookingDate = parts[3];
            String checkIn = parts[4];
            String checkOut = parts[5];
            String status = parts[6];

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

            int parsedCounter = parseBookingCounter(bookingId);
            if (parsedCounter > maxCounter) {
                maxCounter = parsedCounter;
            }
        }

        bookingCounter = maxCounter + 1;
    }

    private String generateBookingId() {
        String id = String.format("BK%03d", bookingCounter);
        while (bookingMap.containsKey(id)) {
            bookingCounter++;
            id = String.format("BK%03d", bookingCounter);
        }
        bookingCounter++;
        return id;
    }

    private int parseBookingCounter(String bookingId) {
        try {
            String number = bookingId.replaceAll("[^0-9]", "");
            if (number.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String nullSafe(String value) {
        return value == null ? "" : value.replace("|", "/");
    }
}

package models;

/**
 * Booking record that composes a student and a room.
 * Demonstrates Composition (Lab 05).
 */
public class Booking {
    private String bookingId;
    private String bookingDate;
    private String checkIn;
    private String checkOut;
    private String status;
    private Student student;
    private AbstractRoom room;

    /**
     * Constructs a booking record.
     *
     * @param bookingId   booking identifier
     * @param bookingDate date of booking creation
     * @param checkIn     check-in date
     * @param checkOut    check-out date
     * @param status      booking status
     * @param student     linked student object
     * @param room        linked room object
     */
    public Booking(String bookingId, Student student, AbstractRoom room, String bookingDate,
                   String checkIn, String checkOut, String status) {
        this.bookingId = bookingId;
        this.student = student;
        this.room = room;
        this.bookingDate = bookingDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getCheckInDate() { return checkIn; }
    public void setCheckInDate(String checkIn) { this.checkIn = checkIn; }

    public String getCheckOutDate() { return checkOut; }
    public void setCheckOutDate(String checkOut) { this.checkOut = checkOut; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public AbstractRoom getRoom() { return room; }
    public void setRoom(AbstractRoom room) { this.room = room; }

    /**
     * Returns true if this booking is currently active.
     *
     * @return true if status equals "Active" (case-insensitive)
     */
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Booking: " + bookingId
                + " | Student: " + (student != null ? student.getId() : "N/A")
                + " | Room: " + (room != null ? room.getRoomNumber() : "N/A")
                + " | " + checkIn + " -> " + checkOut
                + " | Status: " + status;
    }
}

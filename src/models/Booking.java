package models;

/**
 * Booking record that composes a student and a room.
 */
public class Booking {
    private String bookingId;
    private Student student;
    private AbstractRoom room;
    private String bookingDate;
    private String checkInDate;
    private String checkOutDate;
    private String status;

    /**
     * Constructs a booking record.
     *
     * @param bookingId booking identifier
     * @param student linked student
     * @param room linked room
     * @param bookingDate date of booking creation
     * @param checkInDate check-in date
     * @param checkOutDate check-out date
     * @param status booking status
     */
    public Booking(String bookingId, Student student, AbstractRoom room, String bookingDate,
                   String checkInDate, String checkOutDate, String status) {
        this.bookingId = bookingId;
        this.student = student;
        this.room = room;
        this.bookingDate = bookingDate;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public AbstractRoom getRoom() {
        return room;
    }

    public void setRoom(AbstractRoom room) {
        this.room = room;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return bookingId + " | " + student.getId() + " | " + room.getRoomNumber()
                + " | " + checkInDate + " -> " + checkOutDate + " | " + status;
    }
}

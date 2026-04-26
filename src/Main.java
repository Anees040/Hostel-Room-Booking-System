import gui.LoginFrame;
import javax.swing.SwingUtilities;
import services.BookingManager;
import services.FileManager;
import services.RoomManager;
import services.StudentManager;

/**
 * Application entry point for the Hostel Room Booking System.
 */
public class Main {

    /**
     * Starts the desktop application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        FileManager.ensureDirectoryExists("data");
        System.out.println("Data directory ready: data/");

        RoomManager roomManager = new RoomManager();
        StudentManager studentManager = new StudentManager();
        BookingManager bookingManager = new BookingManager(roomManager, studentManager);

        System.out.println("Hostel Room Booking System initializing...");
        System.out.println("Rooms loaded: " + roomManager.getAllRooms().size());
        System.out.println("Students loaded: " + studentManager.getAllStudents().size());
        System.out.println("Bookings loaded: " + bookingManager.getAllBookings().size());

        SwingUtilities.invokeLater(() -> new LoginFrame(roomManager, studentManager, bookingManager));
    }
}

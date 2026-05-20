import gui.LoginFrame;
import gui.SplashScreen;
import gui.UITheme;
import java.io.File;
import java.util.ArrayList;
import models.AbstractRoom;
import models.DoubleRoom;
import models.SingleRoom;
import models.SuiteRoom;
import services.BookingManager;
import services.MaintenanceManager;
import services.NotificationManager;
import services.RoomManager;
import services.StudentManager;
import utils.PersonPrinter;
import utils.RoomPrinter;


public class Main {

    /**
     * Main entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // Apply Nimbus Look and Feel before any component creation
        UITheme.applyNimbusLookAndFeel();

        // Ensure data directory exists
        new File("data").mkdirs();

        // ---------------------------------------------------------------
        // Dependency chain: notification -> room -> student -> maintenance -> booking
        // ---------------------------------------------------------------
        NotificationManager notificationManager = new NotificationManager();
        RoomManager         roomManager         = new RoomManager();
        StudentManager      studentManager      = new StudentManager();
        MaintenanceManager  maintenanceManager  = new MaintenanceManager(roomManager, notificationManager);
        BookingManager      bookingManager      = new BookingManager(roomManager, studentManager, notificationManager);

        // Load persisted data
        notificationManager.load();
        roomManager.load();
        studentManager.load();
        maintenanceManager.load();
        bookingManager.load();

        // Preload sample data only if files are empty
        roomManager.preloadSampleData();
        studentManager.preloadSampleData();

        // Reload maintenance with proper student references after StudentManager is ready
        maintenanceManager.reloadWithStudentManager(studentManager);

        // ---------------------------------------------------------------
        // Polymorphism demo (Lab 08) — printed to console
        // ---------------------------------------------------------------
        System.out.println("=== Polymorphism Demo (RoomPrinter) ===");
        ArrayList<AbstractRoom> demoRooms = new ArrayList<>();
        demoRooms.add(new SingleRoom("DEMO-A01", 1, 8000, true, "WiFi, Fan"));
        demoRooms.add(new DoubleRoom("DEMO-B01", 2, 12000, true, "WiFi, AC, TV"));
        demoRooms.add(new SuiteRoom("DEMO-C01", 3, 20000, true, "WiFi, AC, TV, Kitchen"));
        RoomPrinter.categorizeRooms(demoRooms);
        RoomPrinter.printAllRooms(demoRooms);

        System.out.println("\n=== Polymorphism Demo (PersonPrinter) ===");
        PersonPrinter.printPersonInfo(studentManager.getAllStudents().isEmpty()
                ? new models.Student("SP23-BSE-000", "Demo Student", "03001234567", "test1234", "CS", "BSE", 1)
                : studentManager.getAllStudents().get(0));

        // Generics demo (Lab 11)
        System.out.println("\n=== Generics Demo (DataStore<AbstractRoom>) ===");
        System.out.println("Total rooms in system: " + roomManager.getAllRooms().size());
        System.out.println("Rooms sorted by price:");
        roomManager.getRoomsSortedByPrice().stream()
                .limit(3)
                .forEach(r -> System.out.println("  " + r.getRoomNumber() + " Rs." + r.getPricePerMonth()));

        // ---------------------------------------------------------------
        // Launch GUI with SplashScreen on EDT
        // ---------------------------------------------------------------
        javax.swing.SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.show(() -> {
                LoginFrame loginFrame = new LoginFrame(
                        studentManager, roomManager, bookingManager,
                        maintenanceManager, notificationManager);
                loginFrame.setVisible(true);
            });
        });
    }
}

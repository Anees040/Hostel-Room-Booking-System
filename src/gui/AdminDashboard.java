package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Booking;
import models.Student;
import services.BookingManager;
import services.RoomManager;
import services.StudentManager;

/**
 * Admin dashboard for room, booking, and student monitoring.
 */
public class AdminDashboard extends JFrame {
    private static final Color DARK_BLUE = new Color(25, 78, 140);
    private static final Color LIGHT_BLUE = new Color(52, 152, 219);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private final RoomManager roomManager;
    private final StudentManager studentManager;
    private final BookingManager bookingManager;

    private final CardLayout cardLayout = new CardLayout();
    private JPanel centerPanel;
    private RoomPanel roomPanel;

    private JLabel welcomeLabel;
    private DefaultTableModel bookingTableModel;
    private DefaultTableModel studentTableModel;

    /**
     * Constructs admin dashboard.
     *
     * @param roomManager room manager dependency
     * @param studentManager student manager dependency
     * @param bookingManager booking manager dependency
     */
    public AdminDashboard(RoomManager roomManager, StudentManager studentManager, BookingManager bookingManager) {
        this.roomManager = roomManager;
        this.studentManager = studentManager;
        this.bookingManager = bookingManager;

        initializeFrame();
        buildUi();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Admin Dashboard - COMSATS Hostel");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        add(createTopArea(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        centerPanel = new JPanel(cardLayout);
        centerPanel.setBackground(BG_COLOR);

        roomPanel = new RoomPanel(roomManager);
        centerPanel.add(roomPanel, "rooms");
        centerPanel.add(createBookingsPanel(), "bookings");
        centerPanel.add(createStudentsPanel(), "students");

        add(centerPanel, BorderLayout.CENTER);

        updateWelcomeCard();
        cardLayout.show(centerPanel, "rooms");
    }

    private JPanel createTopArea() {
        JPanel topContainer = new JPanel(new BorderLayout());

        JLabel titleBar = new JLabel("Admin Dashboard - COMSATS Hostel", SwingConstants.CENTER);
        titleBar.setOpaque(true);
        titleBar.setBackground(DARK_BLUE);
        titleBar.setForeground(WHITE);
        titleBar.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.setBorder(new EmptyBorder(12, 8, 12, 8));

        JPanel welcomeCard = new JPanel(new BorderLayout());
        welcomeCard.setBackground(WHITE);
        welcomeCard.setBorder(new EmptyBorder(12, 18, 12, 18));

        welcomeLabel = new JLabel();
        welcomeLabel.setForeground(TEXT_DARK);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        welcomeCard.add(welcomeLabel, BorderLayout.CENTER);

        topContainer.add(titleBar, BorderLayout.NORTH);
        topContainer.add(welcomeCard, BorderLayout.CENTER);
        return topContainer;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(190, 0));
        sidebar.setBackground(LIGHT_BLUE);
        sidebar.setBorder(new EmptyBorder(14, 12, 14, 12));

        JButton manageRooms = createSidebarButton("Manage Rooms");
        JButton viewBookings = createSidebarButton("View Bookings");
        JButton manageStudents = createSidebarButton("Manage Students");
        JButton logout = createSidebarButton("Logout");

        manageRooms.addActionListener(e -> {
            roomPanel.refreshTable();
            updateWelcomeCard();
            cardLayout.show(centerPanel, "rooms");
        });

        viewBookings.addActionListener(e -> {
            refreshBookingsTable();
            updateWelcomeCard();
            cardLayout.show(centerPanel, "bookings");
        });

        manageStudents.addActionListener(e -> {
            refreshStudentsTable();
            cardLayout.show(centerPanel, "students");
        });

        logout.addActionListener(e -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame(roomManager, studentManager, bookingManager);
            loginFrame.setVisible(true);
        });

        sidebar.add(manageRooms);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(viewBookings);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(manageStudents);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logout);

        return sidebar;
    }

    private JButton createSidebarButton(String title) {
        JButton button = new JButton(title);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setBackground(WHITE);
        button.setForeground(DARK_BLUE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        return button;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("All Bookings");
        title.setForeground(DARK_BLUE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        bookingTableModel = new DefaultTableModel(
                new Object[]{"Booking ID", "Student ID", "Room No", "Check-In", "Check-Out", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(bookingTableModel);
        table.setRowHeight(24);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBookingsTable();
        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Registered Students");
        title.setForeground(DARK_BLUE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        studentTableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Department", "Program", "Semester"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(studentTableModel);
        table.setRowHeight(24);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshStudentsTable();
        return panel;
    }

    private void refreshBookingsTable() {
        bookingTableModel.setRowCount(0);
        for (Booking booking : bookingManager.getAllBookings()) {
            bookingTableModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getStudent() == null ? "" : booking.getStudent().getId(),
                    booking.getRoom() == null ? "" : booking.getRoom().getRoomNumber(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus()
            });
        }
    }

    private void refreshStudentsTable() {
        studentTableModel.setRowCount(0);
        for (Student student : studentManager.getAllStudents()) {
            studentTableModel.addRow(new Object[]{
                    student.getId(),
                    student.getName(),
                    student.getDepartment(),
                    student.getProgram(),
                    student.getSemester()
            });
        }
    }

    private void updateWelcomeCard() {
        int totalRooms = roomManager.getAllRooms().size();
        int activeBookings = 0;
        for (Booking booking : bookingManager.getAllBookings()) {
            if ("Active".equalsIgnoreCase(booking.getStatus())) {
                activeBookings++;
            }
        }

        welcomeLabel.setText("Welcome, Admin | Total Rooms: " + totalRooms
                + " | Active Bookings: " + activeBookings);
    }
}

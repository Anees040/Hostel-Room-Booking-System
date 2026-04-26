package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
 * Student dashboard for browsing, booking, and managing personal bookings.
 */
public class StudentDashboard extends JFrame {
    private static final Color DARK_BLUE = new Color(25, 78, 140);
    private static final Color LIGHT_BLUE = new Color(52, 152, 219);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;

    private final Student student;
    private final RoomManager roomManager;
    private final StudentManager studentManager;
    private final BookingManager bookingManager;

    private final CardLayout cardLayout = new CardLayout();
    private JPanel centerPanel;
    private BookingPanel bookingPanel;

    private DefaultTableModel myBookingsModel;
    private DefaultTableModel cancelBookingsModel;

    /**
     * Constructs student dashboard.
     *
     * @param student authenticated student
     * @param roomManager room manager dependency
     * @param studentManager student manager dependency
     * @param bookingManager booking manager dependency
     */
    public StudentDashboard(Student student, RoomManager roomManager,
                            StudentManager studentManager, BookingManager bookingManager) {
        this.student = student;
        this.roomManager = roomManager;
        this.studentManager = studentManager;
        this.bookingManager = bookingManager;

        initializeFrame();
        buildUi();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Student Dashboard - Welcome " + student.getName());
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Student Dashboard - Welcome " + student.getName(), SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(DARK_BLUE);
        title.setForeground(WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(12, 8, 12, 8));
        add(title, BorderLayout.NORTH);

        add(createSidebar(), BorderLayout.WEST);

        centerPanel = new JPanel(cardLayout);
        centerPanel.setBackground(BG_COLOR);

        bookingPanel = new BookingPanel(roomManager, bookingManager, student);
        centerPanel.add(bookingPanel, "book");
        centerPanel.add(createMyBookingsPanel(), "myBookings");
        centerPanel.add(createCancelBookingPanel(), "cancel");

        add(centerPanel, BorderLayout.CENTER);

        refreshMyBookings();
        refreshCancelableBookings();
        cardLayout.show(centerPanel, "book");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBackground(LIGHT_BLUE);
        sidebar.setBorder(new EmptyBorder(12, 12, 12, 12));

        JButton bookRoom = createSideButton("Book a Room");
        JButton myBookings = createSideButton("My Bookings");
        JButton cancelBooking = createSideButton("Cancel Booking");
        JButton logout = createSideButton("Logout");

        bookRoom.addActionListener(e -> {
            bookingPanel.refreshRooms();
            cardLayout.show(centerPanel, "book");
        });

        myBookings.addActionListener(e -> {
            refreshMyBookings();
            cardLayout.show(centerPanel, "myBookings");
        });

        cancelBooking.addActionListener(e -> {
            refreshCancelableBookings();
            cardLayout.show(centerPanel, "cancel");
        });

        logout.addActionListener(e -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame(roomManager, studentManager, bookingManager);
            loginFrame.setVisible(true);
        });

        sidebar.add(bookRoom);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(myBookings);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(cancelBooking);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logout);

        return sidebar;
    }

    private JButton createSideButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setBackground(WHITE);
        button.setForeground(DARK_BLUE);
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BG_COLOR);

        JLabel label = new JLabel("My Bookings");
        label.setForeground(DARK_BLUE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));

        myBookingsModel = new DefaultTableModel(
                new Object[]{"Booking ID", "Room No", "Type", "Check-In", "Check-Out", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(myBookingsModel);
        table.setRowHeight(24);

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCancelBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(BG_COLOR);

        JLabel label = new JLabel("Cancel Booking");
        label.setForeground(DARK_BLUE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));

        cancelBookingsModel = new DefaultTableModel(
                new Object[]{"Booking ID", "Room No", "Type", "Check-In", "Check-Out", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(cancelBookingsModel);
        table.setRowHeight(24);

        JButton cancelSelected = new JButton("Cancel Selected Booking");
        cancelSelected.setBackground(DARK_BLUE);
        cancelSelected.setForeground(WHITE);

        cancelSelected.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an active booking first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String bookingId = String.valueOf(cancelBookingsModel.getValueAt(row, 0));
            boolean cancelled = bookingManager.cancelBooking(bookingId);
            if (cancelled) {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCancelableBookings();
                refreshMyBookings();
                bookingPanel.refreshRooms();
            } else {
                JOptionPane.showMessageDialog(this, "Unable to cancel booking.",
                        "Action Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(BG_COLOR);
        south.add(cancelSelected, BorderLayout.EAST);

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMyBookings() {
        myBookingsModel.setRowCount(0);
        List<Booking> list = bookingManager.getBookingsByStudent(student.getId());
        for (Booking booking : list) {
            myBookingsModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getRoom() == null ? "" : booking.getRoom().getRoomNumber(),
                    booking.getRoom() == null ? "" : booking.getRoom().getRoomType(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    booking.getStatus()
            });
        }
    }

    private void refreshCancelableBookings() {
        cancelBookingsModel.setRowCount(0);
        List<Booking> list = bookingManager.getBookingsByStudent(student.getId());
        for (Booking booking : list) {
            if ("Active".equalsIgnoreCase(booking.getStatus())) {
                cancelBookingsModel.addRow(new Object[]{
                        booking.getBookingId(),
                        booking.getRoom() == null ? "" : booking.getRoom().getRoomNumber(),
                        booking.getRoom() == null ? "" : booking.getRoom().getRoomType(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getStatus()
                });
            }
        }
    }
}

package gui;

import exceptions.MaintenanceException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.Admin;
import models.Booking;
import models.MaintenanceRequest;
import models.Notification;
import models.RoomReview;
import models.Student;
import services.BookingManager;
import services.MaintenanceManager;
import services.NotificationManager;
import services.RoomManager;
import services.StudentManager;
import utils.DateUtils;

/**
 * Admin dashboard with 7 tabbed views.
 * Demonstrates Layout Managers (Lab 13), Event-Driven Programming (Lab 14).
 */
public class AdminDashboard extends JFrame {

    private final Admin admin;
    private final RoomManager roomManager;
    private final StudentManager studentManager;
    private final BookingManager bookingManager;
    private final MaintenanceManager maintenanceManager;
    private final NotificationManager notificationManager;

    private DefaultTableModel bookingTableModel;
    private DefaultTableModel studentTableModel;
    private DefaultTableModel maintenanceTableModel;
    private DefaultTableModel reviewTableModel;
    private DefaultTableModel notifTableModel;
    private JLabel statsLabel;
    private JLabel avgRatingLabel;

    /**
     * Constructs admin dashboard.
     *
     * @param admin               logged-in admin
     * @param roomManager         room manager
     * @param studentManager      student manager
     * @param bookingManager      booking manager
     * @param maintenanceManager  maintenance manager
     * @param notificationManager notification manager
     */
    public AdminDashboard(Admin admin, RoomManager roomManager, StudentManager studentManager,
                          BookingManager bookingManager, MaintenanceManager maintenanceManager,
                          NotificationManager notificationManager) {
        this.admin = admin;
        this.roomManager = roomManager;
        this.studentManager = studentManager;
        this.bookingManager = bookingManager;
        this.maintenanceManager = maintenanceManager;
        this.notificationManager = notificationManager;

        UITheme.applyNimbusLookAndFeel();
        initFrame();
        buildUI();
    }

    private void initFrame() {
        setTitle("Admin Dashboard — " + admin.getName());
        setSize(1100, 720);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(AdminDashboard.this,
                        "Exit the application?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // North — gradient header
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(
                        0, 0, UITheme.HEADER_BG, getWidth(), 0, UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftHeader.setOpaque(false);
        JLabel titleLbl = new JLabel("COMSATS Hostel — Admin Panel");
        titleLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        titleLbl.setForeground(Color.WHITE);
        leftHeader.add(titleLbl);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        JLabel adminNameLbl = new JLabel(admin.getName());
        adminNameLbl.setFont(UITheme.BODY_FONT);
        adminNameLbl.setForeground(new Color(186, 211, 252));
        JButton logoutBtn = UITheme.dangerButton("Sign Out");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(studentManager, roomManager, bookingManager, maintenanceManager, notificationManager)
                    .setVisible(true);
        });
        rightHeader.add(adminNameLbl);
        rightHeader.add(logoutBtn);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center — tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BODY_FONT);
        tabs.addTab("Rooms",          new RoomPanel(roomManager));
        tabs.addTab("All Bookings",   buildBookingsTab());
        tabs.addTab("Students",       buildStudentsTab());
        tabs.addTab("Maintenance",    buildMaintenanceTab());
        tabs.addTab("Reviews",          buildReviewsTab());
        tabs.addTab("Notifications",  buildNotificationsTab());
        tabs.addTab("Reports",         new ReportsPanel(bookingManager, maintenanceManager));
        add(tabs, BorderLayout.CENTER);

        // South — stats bar
        statsLabel = new JLabel();
        statsLabel.setFont(UITheme.SMALL_FONT);
        statsLabel.setBorder(new EmptyBorder(5, 14, 5, 14));
        statsLabel.setForeground(Color.WHITE);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(new Color(15, 23, 42));
        southPanel.add(statsLabel);
        add(southPanel, BorderLayout.SOUTH);

        refreshStats();
    }

    // ------------------------------------------------------------------
    // All Bookings Tab
    // ------------------------------------------------------------------

    private JPanel buildBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Active", "Cancelled"});
        JButton refreshBtn = UITheme.primaryButton("Refresh");

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setBackground(UITheme.LIGHT_BG);
        north.add(new JLabel("Filter:"));
        north.add(filterCombo);
        north.add(refreshBtn);
        panel.add(north, BorderLayout.NORTH);

        bookingTableModel = new DefaultTableModel(
                new Object[]{"Booking ID", "Student ID", "Student Name", "Room", "Check-In", "Check-Out", "Status", "Total Cost"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(bookingTableModel);
        UITheme.styleTable(table);

        // Double-click for details
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        showBookingDetails(row);
                    }
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadBookings((String) filterCombo.getSelectedItem()));
        filterCombo.addActionListener(e -> loadBookings((String) filterCombo.getSelectedItem()));

        loadBookings("All");
        return panel;
    }

    private void loadBookings(String filter) {
        bookingTableModel.setRowCount(0);
        for (Booking b : bookingManager.getAllBookings()) {
            if (!"All".equalsIgnoreCase(filter) && !b.getStatus().equalsIgnoreCase(filter)) continue;
            double cost = 0;
            if (b.getRoom() != null) {
                cost = DateUtils.calculateTotalCost(b.getRoom().getPricePerMonth(), b.getCheckInDate(), b.getCheckOutDate());
            }
            bookingTableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getStudent() != null ? b.getStudent().getId() : "",
                    b.getStudent() != null ? b.getStudent().getName() : "",
                    b.getRoom() != null ? b.getRoom().getRoomNumber() : "",
                    b.getCheckInDate(), b.getCheckOutDate(), b.getStatus(),
                    String.format("Rs. %.2f", cost)
            });
        }
        refreshStats();
    }

    private void showBookingDetails(int row) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < bookingTableModel.getColumnCount(); col++) {
            sb.append(bookingTableModel.getColumnName(col)).append(": ")
              .append(bookingTableModel.getValueAt(row, col)).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Booking Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Students Tab
    // ------------------------------------------------------------------

    private JPanel buildStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField(18);
        JButton searchBtn  = UITheme.primaryButton("Search");
        JButton showAllBtn = UITheme.primaryButton("Show All");

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setBackground(UITheme.LIGHT_BG);
        north.add(new JLabel("Search:"));
        north.add(searchField);
        north.add(searchBtn);
        north.add(showAllBtn);
        panel.add(north, BorderLayout.NORTH);

        studentTableModel = new DefaultTableModel(
                new Object[]{"Student ID", "Name", "Contact", "Department", "Program", "Semester"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(studentTableModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        Runnable loadAll = () -> {
            studentTableModel.setRowCount(0);
            for (Student s : studentManager.getAllStudents()) {
                studentTableModel.addRow(new Object[]{
                        s.getId(), s.getName(), s.getContact(), s.getDepartment(), s.getProgram(), s.getSemester()
                });
            }
        };
        loadAll.run();

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            studentTableModel.setRowCount(0);
            for (Student s : studentManager.getAllStudents()) {
                if (s.getId().toLowerCase().contains(query) || s.getName().toLowerCase().contains(query)) {
                    studentTableModel.addRow(new Object[]{
                            s.getId(), s.getName(), s.getContact(), s.getDepartment(), s.getProgram(), s.getSemester()
                    });
                }
            }
        });
        showAllBtn.addActionListener(e -> loadAll.run());

        return panel;
    }

    // ------------------------------------------------------------------
    // Maintenance Tab
    // ------------------------------------------------------------------

    private JPanel buildMaintenanceTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Resolved"});
        JButton refreshBtn = UITheme.primaryButton("Refresh");

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setBackground(UITheme.LIGHT_BG);
        north.add(new JLabel("Filter:"));
        north.add(filterCombo);
        north.add(refreshBtn);
        panel.add(north, BorderLayout.NORTH);

        maintenanceTableModel = new DefaultTableModel(
                new Object[]{"Request ID", "Student", "Room", "Description", "Status", "Date", "Resolved Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(maintenanceTableModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton updateBtn = UITheme.primaryButton("Update Status");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.LIGHT_BG);
        south.add(updateBtn);
        panel.add(south, BorderLayout.SOUTH);

        Runnable loadMaintenance = () -> {
            String filter = (String) filterCombo.getSelectedItem();
            maintenanceTableModel.setRowCount(0);
            for (MaintenanceRequest req : maintenanceManager.getAllRequests()) {
                if (!"All".equalsIgnoreCase(filter) && !req.getStatus().equalsIgnoreCase(filter)) continue;
                String studentName = req.getRequestedBy() != null ? req.getRequestedBy().getName() : "N/A";
                String roomNo = req.getRoom() != null ? req.getRoom().getRoomNumber() : "N/A";
                maintenanceTableModel.addRow(new Object[]{
                        req.getRequestId(), studentName, roomNo,
                        req.getDescription(), req.getStatus(),
                        req.getRequestedDate(),
                        req.getResolvedDate() == null || req.getResolvedDate().isEmpty() ? "—" : req.getResolvedDate()
                });
            }
        };
        loadMaintenance.run();

        refreshBtn.addActionListener(e -> loadMaintenance.run());
        filterCombo.addActionListener(e -> loadMaintenance.run());

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a request first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String requestId = String.valueOf(maintenanceTableModel.getValueAt(row, 0));
            String[] options = {"Pending", "In Progress", "Resolved"};
            String newStatus = (String) JOptionPane.showInputDialog(this,
                    "Select new status for " + requestId, "Update Status",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (newStatus != null) {
                try {
                    maintenanceManager.updateRequestStatus(requestId, newStatus);
                    loadMaintenance.run();
                    JOptionPane.showMessageDialog(this, "Status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (MaintenanceException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    // ------------------------------------------------------------------
    // Reviews Tab
    // ------------------------------------------------------------------

    private JPanel buildReviewsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Populate room number combo
        JComboBox<String> roomCombo = new JComboBox<>();
        for (models.AbstractRoom r : roomManager.getAllRooms()) {
            roomCombo.addItem(r.getRoomNumber());
        }
        JButton loadBtn = UITheme.primaryButton("Load Reviews");

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setBackground(UITheme.LIGHT_BG);
        north.add(new JLabel("Room:"));
        north.add(roomCombo);
        north.add(loadBtn);
        panel.add(north, BorderLayout.NORTH);

        reviewTableModel = new DefaultTableModel(
                new Object[]{"Review ID", "Reviewer", "Rating", "Comment", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(reviewTableModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        avgRatingLabel = new JLabel("Average Rating: —");
        avgRatingLabel.setFont(UITheme.HEADER_FONT);
        avgRatingLabel.setForeground(UITheme.PRIMARY);
        avgRatingLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
        panel.add(avgRatingLabel, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> {
            String roomNo = (String) roomCombo.getSelectedItem();
            if (roomNo == null) return;
            reviewTableModel.setRowCount(0);
            List<RoomReview> reviews = roomManager.getReviewsForRoom(roomNo);
            for (RoomReview rv : reviews) {
                String reviewer = rv.getReviewer() != null ? rv.getReviewer().getName() : "Anonymous";
                reviewTableModel.addRow(new Object[]{
                        rv.getReviewId(), reviewer, rv.getRating(), rv.getComment(), rv.getReviewDate()
                });
            }
            double avg = roomManager.getAverageRating(roomNo);
            avgRatingLabel.setText(avg > 0
                    ? String.format("Average Rating: %.1f / 5.0", avg)
                    : "Average Rating: No reviews yet");
        });

        return panel;
    }

    // ------------------------------------------------------------------
    // Notifications Tab
    // ------------------------------------------------------------------

    private JPanel buildNotificationsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        notifTableModel = new DefaultTableModel(
                new Object[]{"ID", "Message", "Date", "Read"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(notifTableModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton markAllRead = UITheme.primaryButton("Mark All Read");
        JButton refresh     = UITheme.primaryButton("Refresh");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.LIGHT_BG);
        south.add(refresh);
        south.add(markAllRead);
        panel.add(south, BorderLayout.SOUTH);

        Runnable load = () -> {
            notifTableModel.setRowCount(0);
            for (Notification n : notificationManager.getNotificationsForUser("ADMIN")) {
                notifTableModel.addRow(new Object[]{
                        n.getNotificationId(), n.getMessage(), n.getDateCreated(), n.isRead() ? "Yes" : "No"
                });
            }
        };
        load.run();

        refresh.addActionListener(e -> load.run());
        markAllRead.addActionListener(e -> {
            for (Notification n : notificationManager.getNotificationsForUser("ADMIN")) {
                notificationManager.markNotificationRead(n.getNotificationId());
            }
            load.run();
        });

        return panel;
    }

    private void refreshStats() {
        if (statsLabel == null) return;
        int rooms = roomManager.getRoomCount();
        int students = studentManager.getStudentCount();
        int activeBookings = bookingManager.getActiveBookingsCount();
        double revenue = bookingManager.getTotalRevenue();
        statsLabel.setText(String.format("Rooms: %d  |  Students: %d  |  Active Bookings: %d  |  Revenue: Rs. %.2f",
                rooms, students, activeBookings, revenue));
    }
}

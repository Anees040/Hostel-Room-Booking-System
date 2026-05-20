package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.AbstractRoom;
import models.Booking;
import models.MaintenanceRequest;
import models.Notification;
import models.RoomReview;
import models.Student;
import services.BookingManager;
import services.MaintenanceManager;
import services.NotificationManager;
import services.RoomManager;
import utils.DateUtils;
import utils.IdGenerator;

/**
 * Student dashboard with 5 tabbed views.
 * Demonstrates Layout Managers (Lab 13), Event-Driven Programming (Lab 14).
 */
public class StudentDashboard extends JFrame {

    private final Student student;
    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final MaintenanceManager maintenanceManager;
    private final NotificationManager notificationManager;
    private final Runnable onLogout;

    private DefaultTableModel availableRoomsModel;
    private DefaultTableModel myBookingsModel;
    private DefaultTableModel maintenanceTableModel;
    private DefaultTableModel reviewTableModel;
    private DefaultTableModel notifTableModel;
    private JLabel unreadBadge;

    // Class-level combo boxes so they can be refreshed after a booking is made
    private JComboBox<String> maintenanceRoomCombo;
    private JComboBox<String> reviewRoomCombo;

    public StudentDashboard(Student student, RoomManager roomManager,
                            BookingManager bookingManager, MaintenanceManager maintenanceManager,
                            NotificationManager notificationManager, Runnable onLogout) {
        this.student = student;
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
        this.maintenanceManager = maintenanceManager;
        this.notificationManager = notificationManager;
        this.onLogout = onLogout;

        UITheme.applyNimbusLookAndFeel();
        initFrame();
        buildUI();
    }

    private void initFrame() {
        setTitle("Student Dashboard — " + student.getName());
        setSize(980, 680);
        setMinimumSize(new java.awt.Dimension(800, 560));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(StudentDashboard.this,
                        "Logout and exit?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    if (onLogout != null) {
                        onLogout.run();
                    }
                }
            }
        });
    }

    private void buildUI() {
        setLayout(new BorderLayout());

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

        JPanel leftH = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftH.setOpaque(false);
        JLabel titleLbl = new JLabel("Welcome, " + student.getName());
        titleLbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        titleLbl.setForeground(Color.WHITE);
        leftH.add(titleLbl);

        long unread = notificationManager.getUnreadCount(student.getId());
        unreadBadge = new JLabel(unread > 0 ? "  [ " + unread + " unread ]" : "");
        unreadBadge.setFont(UITheme.BODY_FONT);
        unreadBadge.setForeground(new Color(253, 224, 71));
        leftH.add(unreadBadge);

        JPanel rightH = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightH.setOpaque(false);
        JLabel idLabel = new JLabel("ID: " + student.getId());
        idLabel.setFont(UITheme.SMALL_FONT);
        idLabel.setForeground(new Color(186, 211, 252));
        JButton logoutBtn = UITheme.dangerButton("Sign Out");
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(StudentDashboard.this,
                    "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                if (onLogout != null) {
                    onLogout.run();
                }
            }
        });
        rightH.add(idLabel);
        rightH.add(logoutBtn);

        header.add(leftH,  BorderLayout.WEST);
        header.add(rightH, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Center — tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BODY_FONT);
        tabs.addTab("Browse Rooms",       buildBrowseRoomsTab());
        tabs.addTab("My Bookings",        buildMyBookingsTab());
        tabs.addTab("Submit Maintenance", buildMaintenanceTab());
        tabs.addTab("Reviews",            buildReviewsTab());
        tabs.addTab("Notifications",      buildNotificationsTab());

        // Refresh room combos whenever the user switches to those tabs
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 2) {
                refreshMaintenanceRooms();
                loadMyMaintenance();
            } else if (idx == 3) {
                refreshReviewRooms();
                loadMyReviews();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------
    // Tab 1 — Browse Rooms
    // ------------------------------------------------------------------

    private JPanel buildBrowseRoomsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        JTextField maxPriceField = new JTextField("0", 8);
        JButton searchBtn = UITheme.primaryButton("Search");
        JButton clearBtn  = UITheme.primaryButton("Clear Filter");

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.setBackground(UITheme.LIGHT_BG);
        north.add(new JLabel("Type:"));
        north.add(typeFilter);
        north.add(new JLabel("Max Price (0=any):"));
        north.add(maxPriceField);
        north.add(searchBtn);
        north.add(clearBtn);
        panel.add(north, BorderLayout.NORTH);

        availableRoomsModel = new DefaultTableModel(
                new Object[]{"Type", "Room No", "Floor", "Price/Month", "Amenities", "Avg Rating", "Max Occ."}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(availableRoomsModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton bookBtn = UITheme.primaryButton("Book Selected Room");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.LIGHT_BG);
        south.add(bookBtn);
        panel.add(south, BorderLayout.SOUTH);

        Runnable load = () -> {
            String filter = (String) typeFilter.getSelectedItem();
            double maxPrice = 0;
            try { maxPrice = Double.parseDouble(maxPriceField.getText().trim()); } catch (NumberFormatException ignored) {}
            final double mp = maxPrice;

            availableRoomsModel.setRowCount(0);
            List<AbstractRoom> rooms = roomManager.getAvailableRoomsByType(filter);
            for (AbstractRoom r : rooms) {
                if (mp > 0 && r.getPricePerMonth() > mp) continue;
                double avg = roomManager.getAverageRating(r.getRoomNumber());
                availableRoomsModel.addRow(new Object[]{
                        r.getRoomType(), r.getRoomNumber(), r.getFloor(), r.getPricePerMonth(),
                        r.getAmenities(), avg > 0 ? String.format("%.1f", avg) : "—", r.getMaxOccupancy()
                });
            }
        };
        load.run();

        searchBtn.addActionListener(e -> load.run());
        clearBtn.addActionListener(e -> {
            typeFilter.setSelectedIndex(0);
            maxPriceField.setText("0");
            load.run();
        });

        bookBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a room first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String roomNo = String.valueOf(availableRoomsModel.getValueAt(row, 1));
            AbstractRoom room = roomManager.findRoom(roomNo);
            if (room == null) { load.run(); return; }

            BookingDialog dlg = new BookingDialog(this, student, room, bookingManager);
            dlg.setVisible(true);
            load.run();
            refreshMyBookings();
            // Refresh maintenance and review combos so booked room appears immediately
            refreshMaintenanceRooms();
            refreshReviewRooms();
            refreshUnreadBadge();
        });

        return panel;
    }

    // ------------------------------------------------------------------
    // Tab 2 — My Bookings
    // ------------------------------------------------------------------

    private JPanel buildMyBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        myBookingsModel = new DefaultTableModel(
                new Object[]{"Booking ID", "Room", "Type", "Check-In", "Check-Out", "Status", "Total Cost"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(myBookingsModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton cancelBtn  = UITheme.dangerButton("Cancel Selected Booking");
        JButton refreshBtn = UITheme.primaryButton("Refresh");

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.LIGHT_BG);
        south.add(refreshBtn);
        south.add(cancelBtn);
        panel.add(south, BorderLayout.SOUTH);

        refreshMyBookings();

        refreshBtn.addActionListener(e -> refreshMyBookings());
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a booking first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String bookingId = String.valueOf(myBookingsModel.getValueAt(row, 0));
            int confirm = JOptionPane.showConfirmDialog(this, "Cancel booking " + bookingId + "?",
                    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (bookingManager.cancelBooking(bookingId)) {
                    JOptionPane.showMessageDialog(this, "Booking cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshMyBookings();
                    refreshMaintenanceRooms();
                    refreshReviewRooms();
                    refreshUnreadBadge();
                } else {
                    JOptionPane.showMessageDialog(this, "Cannot cancel this booking.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private void refreshMyBookings() {
        if (myBookingsModel == null) return;
        myBookingsModel.setRowCount(0);
        for (Booking b : bookingManager.getBookingsByStudent(student.getId())) {
            double cost = 0;
            if (b.getRoom() != null) {
                cost = DateUtils.calculateTotalCost(b.getRoom().getPricePerMonth(), b.getCheckInDate(), b.getCheckOutDate());
            }
            myBookingsModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getRoom() != null ? b.getRoom().getRoomNumber() : "",
                    b.getRoom() != null ? b.getRoom().getRoomType() : "",
                    b.getCheckInDate(), b.getCheckOutDate(), b.getStatus(),
                    String.format("Rs. %.2f", cost)
            });
        }
    }

    // ------------------------------------------------------------------
    // Tab 3 — Submit Maintenance
    // ------------------------------------------------------------------

    private JPanel buildMaintenanceTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        maintenanceRoomCombo = new JComboBox<>();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JTextArea descArea = new JTextArea(5, 30);
        descArea.setFont(UITheme.BODY_FONT);
        JButton submitBtn = UITheme.primaryButton("Submit Request");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1; formPanel.add(maintenanceRoomCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; formPanel.add(new JScrollPane(descArea), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(submitBtn, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        maintenanceTableModel = new DefaultTableModel(
                new Object[]{"Request ID", "Room", "Description", "Status", "Date", "Resolved Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(maintenanceTableModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshMaintenanceRooms();
        loadMyMaintenance();

        submitBtn.addActionListener(e -> {
            String roomNo = (String) maintenanceRoomCombo.getSelectedItem();
            String desc = descArea.getText().trim();
            if (roomNo == null) {
                JOptionPane.showMessageDialog(this,
                        "No active bookings found. Please book a room first.",
                        "No Active Booking", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a description.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AbstractRoom room = roomManager.findRoom(roomNo);
            String reqId = maintenanceManager.generateNextRequestId();
            MaintenanceRequest req = new MaintenanceRequest(reqId, desc, "Pending",
                    DateUtils.today(), "", student, room);
            maintenanceManager.submitMaintenanceRequest(req);
            descArea.setText("");
            loadMyMaintenance();
            JOptionPane.showMessageDialog(this, "Maintenance request submitted: " + reqId,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private void refreshMaintenanceRooms() {
        if (maintenanceRoomCombo == null) return;
        maintenanceRoomCombo.removeAllItems();
        for (Booking b : bookingManager.getBookingsByStudent(student.getId())) {
            if (b.isActive() && b.getRoom() != null) {
                maintenanceRoomCombo.addItem(b.getRoom().getRoomNumber());
            }
        }
    }

    private void loadMyMaintenance() {
        if (maintenanceTableModel == null) return;
        maintenanceTableModel.setRowCount(0);
        for (MaintenanceRequest req : maintenanceManager.getAllRequests()) {
            if (req.getRequestedBy() != null
                    && req.getRequestedBy().getId().equalsIgnoreCase(student.getId())) {
                String roomNo = req.getRoom() != null ? req.getRoom().getRoomNumber() : "N/A";
                maintenanceTableModel.addRow(new Object[]{
                        req.getRequestId(), roomNo, req.getDescription(), req.getStatus(),
                        req.getRequestedDate(),
                        req.getResolvedDate() == null || req.getResolvedDate().isEmpty() ? "—" : req.getResolvedDate()
                });
            }
        }
    }

    // ------------------------------------------------------------------
    // Tab 4 — Reviews
    // ------------------------------------------------------------------

    private JPanel buildReviewsTab() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        reviewRoomCombo = new JComboBox<>();

        JSlider ratingSlider = new JSlider(1, 5, 3);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setBackground(UITheme.LIGHT_BG);

        JTextArea commentArea = new JTextArea(3, 30);
        commentArea.setFont(UITheme.BODY_FONT);
        JButton submitBtn = UITheme.successButton("Submit Review");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx = 1; formPanel.add(reviewRoomCombo, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Rating (1-5):"), gbc);
        gbc.gridx = 1; formPanel.add(ratingSlider, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Comment:"), gbc);
        gbc.gridx = 1; formPanel.add(new JScrollPane(commentArea), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(submitBtn, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        reviewTableModel = new DefaultTableModel(
                new Object[]{"Review ID", "Room", "Rating", "Comment", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(reviewTableModel);
        UITheme.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        refreshReviewRooms();
        loadMyReviews();

        submitBtn.addActionListener(e -> {
            String roomNo = (String) reviewRoomCombo.getSelectedItem();
            int rating = ratingSlider.getValue();
            String comment = commentArea.getText().trim();
            if (roomNo == null) {
                JOptionPane.showMessageDialog(this,
                        "No booked rooms found. Please book a room first.",
                        "No Booking", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (comment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a comment.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            AbstractRoom room = roomManager.findRoom(roomNo);
            String reviewId = IdGenerator.generateReviewId(reviewTableModel.getRowCount() + 1);
            RoomReview review = new RoomReview(reviewId, comment, DateUtils.today(), rating, student, room);
            roomManager.addReview(review);
            commentArea.setText("");
            loadMyReviews();
            JOptionPane.showMessageDialog(this, "Review submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private void refreshReviewRooms() {
        if (reviewRoomCombo == null) return;
        reviewRoomCombo.removeAllItems();
        for (Booking b : bookingManager.getBookingsByStudent(student.getId())) {
            if (b.getRoom() != null) {
                String roomNo = b.getRoom().getRoomNumber();
                boolean alreadyAdded = false;
                for (int i = 0; i < reviewRoomCombo.getItemCount(); i++) {
                    if (reviewRoomCombo.getItemAt(i).equals(roomNo)) {
                        alreadyAdded = true;
                        break;
                    }
                }
                if (!alreadyAdded) {
                    reviewRoomCombo.addItem(roomNo);
                }
            }
        }
    }

    private void loadMyReviews() {
        if (reviewTableModel == null) return;
        reviewTableModel.setRowCount(0);
        for (models.AbstractRoom room : roomManager.getAllRooms()) {
            for (RoomReview rv : roomManager.getReviewsForRoom(room.getRoomNumber())) {
                if (rv.getReviewer() != null && rv.getReviewer().getId().equalsIgnoreCase(student.getId())) {
                    reviewTableModel.addRow(new Object[]{
                            rv.getReviewId(), room.getRoomNumber(), rv.getRating(), rv.getComment(), rv.getReviewDate()
                    });
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Tab 5 — Notifications
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

        JLabel unreadCountLabel = new JLabel();
        unreadCountLabel.setFont(UITheme.BODY_FONT);
        unreadCountLabel.setForeground(UITheme.DANGER);

        JButton markReadBtn = UITheme.primaryButton("Mark Selected Read");
        JButton refreshBtn  = UITheme.primaryButton("Refresh");

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(UITheme.LIGHT_BG);
        south.add(unreadCountLabel);
        south.add(refreshBtn);
        south.add(markReadBtn);
        panel.add(south, BorderLayout.SOUTH);

        Runnable load = () -> {
            notifTableModel.setRowCount(0);
            for (Notification n : notificationManager.getNotificationsForUser(student.getId())) {
                notifTableModel.addRow(new Object[]{
                        n.getNotificationId(), n.getMessage(), n.getDateCreated(), n.isRead() ? "Yes" : "No"
                });
            }
            long unread = notificationManager.getUnreadCount(student.getId());
            unreadCountLabel.setText("Unread: " + unread);
        };
        load.run();

        refreshBtn.addActionListener(e -> load.run());
        markReadBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a notification first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String notifId = String.valueOf(notifTableModel.getValueAt(row, 0));
            notificationManager.markNotificationRead(notifId);
            load.run();
            refreshUnreadBadge();
        });

        return panel;
    }

    private void refreshUnreadBadge() {
        if (unreadBadge == null) return;
        long unread = notificationManager.getUnreadCount(student.getId());
        unreadBadge.setText(unread > 0 ? " [" + unread + " new]" : "");
    }
}

package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import models.Booking;
import models.MaintenanceRequest;
import services.BookingManager;
import services.MaintenanceManager;

/**
 * Reports and Analytics panel for the Admin Dashboard.
 * Demonstrates Java2D bar chart drawing, DocumentListener search, CSV export (Lab 12-14).
 */
public class ReportsPanel extends JPanel {

    private final BookingManager bookingManager;
    private final MaintenanceManager maintenanceManager;

    // Revenue sub-tab labels
    private JLabel totalBookingsLabel;
    private JLabel totalRevenueLabel;
    private JLabel avgDurationLabel;
    private JLabel mostBookedTypeLabel;

    // Booking history table
    private DefaultTableModel historyModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Maintenance labels
    private JLabel totalRequestsLabel;
    private JLabel pendingLabel;
    private JLabel inProgressLabel;
    private JLabel resolvedLabel;

    /**
     * Constructs the reports panel.
     *
     * @param bookingManager     booking manager dependency
     * @param maintenanceManager maintenance manager dependency
     */
    public ReportsPanel(BookingManager bookingManager, MaintenanceManager maintenanceManager) {
        this.bookingManager = bookingManager;
        this.maintenanceManager = maintenanceManager;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.LIGHT_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Reports & Analytics", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UITheme.PRIMARY);
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.BODY_FONT);
        tabs.addTab("Revenue Summary", buildRevenueSummaryPanel());
        tabs.addTab("Room Occupancy Chart", buildChartPanel());
        tabs.addTab("Booking History", buildBookingHistoryPanel());
        tabs.addTab("Maintenance Summary", buildMaintenanceSummaryPanel());
        add(tabs, BorderLayout.CENTER);

        refreshAll();
    }

    // ------------------------------------------------------------------
    // Revenue Summary
    // ------------------------------------------------------------------

    private JPanel buildRevenueSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 8));
        grid.setBackground(UITheme.LIGHT_BG);

        totalBookingsLabel  = addStatRow(grid, "Total Active Bookings:");
        totalRevenueLabel   = addStatRow(grid, "Total Revenue (Rs.):");
        avgDurationLabel    = addStatRow(grid, "Average Booking Duration:");
        mostBookedTypeLabel = addStatRow(grid, "Most Booked Room Type:");

        JButton refresh = UITheme.primaryButton("Refresh");
        refresh.addActionListener(e -> refreshRevenue());

        panel.add(grid, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel addStatRow(JPanel grid, String labelText) {
        JLabel key = new JLabel(labelText);
        key.setFont(UITheme.BODY_FONT);
        grid.add(key);
        JLabel val = new JLabel("—");
        val.setFont(UITheme.HEADER_FONT);
        val.setForeground(UITheme.PRIMARY);
        grid.add(val);
        return val;
    }

    private void refreshRevenue() {
        List<Booking> all = bookingManager.getAllBookings();
        int active = 0;
        double revenue = 0;
        long totalDays = 0;
        int singleCount = 0, doubleCount = 0, suiteCount = 0;

        for (Booking b : all) {
            if (b.isActive()) {
                active++;
                if (b.getRoom() != null) {
                    double cost = utils.DateUtils.calculateTotalCost(
                            b.getRoom().getPricePerMonth(),
                            b.getCheckInDate(), b.getCheckOutDate());
                    revenue += cost;
                    totalDays += utils.DateUtils.daysBetween(b.getCheckInDate(), b.getCheckOutDate());
                    String type = b.getRoom().getRoomType();
                    if ("Single".equalsIgnoreCase(type)) singleCount++;
                    else if ("Double".equalsIgnoreCase(type)) doubleCount++;
                    else suiteCount++;
                }
            }
        }

        totalBookingsLabel.setText(String.valueOf(active));
        totalRevenueLabel.setText(String.format("%.2f", revenue));
        avgDurationLabel.setText(active > 0 ? (totalDays / active) + " days" : "—");

        String mostBooked = "None";
        if (singleCount >= doubleCount && singleCount >= suiteCount && singleCount > 0) mostBooked = "Single";
        else if (doubleCount >= singleCount && doubleCount >= suiteCount && doubleCount > 0) mostBooked = "Double";
        else if (suiteCount > 0) mostBooked = "Suite";
        mostBookedTypeLabel.setText(mostBooked);
    }

    // ------------------------------------------------------------------
    // Room Occupancy Bar Chart (Java2D)
    // ------------------------------------------------------------------

    private JPanel buildChartPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UITheme.LIGHT_BG);

        JPanel chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                List<Booking> all = bookingManager.getAllBookings();
                int single = 0, dbl = 0, suite = 0;
                for (Booking b : all) {
                    if (b.isActive() && b.getRoom() != null) {
                        String type = b.getRoom().getRoomType();
                        if ("Single".equalsIgnoreCase(type)) single++;
                        else if ("Double".equalsIgnoreCase(type)) dbl++;
                        else if ("Suite".equalsIgnoreCase(type)) suite++;
                    }
                }

                int maxVal = Math.max(1, Math.max(single, Math.max(dbl, suite)));
                int chartH = getHeight() - 80;
                int barW = 80;
                int gap = 50;
                int startX = 80;
                int baseY = getHeight() - 50;

                String[] labels = {"Single", "Double", "Suite"};
                int[] counts = {single, dbl, suite};
                Color[] colors = {UITheme.PRIMARY, UITheme.SUCCESS, UITheme.WARNING};

                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(UITheme.HEADER_FONT);
                g2d.drawString("Active Bookings by Room Type", 80, 30);

                for (int i = 0; i < 3; i++) {
                    int barH = (int) ((double) counts[i] / maxVal * chartH);
                    int x = startX + i * (barW + gap);
                    int y = baseY - barH;

                    g2d.setColor(colors[i]);
                    g2d.fillRect(x, y, barW, barH);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawRect(x, y, barW, barH);

                    g2d.setFont(UITheme.BODY_FONT);
                    g2d.drawString(labels[i], x + (barW - g2d.getFontMetrics().stringWidth(labels[i])) / 2, baseY + 20);
                    g2d.drawString(String.valueOf(counts[i]), x + (barW - g2d.getFontMetrics().stringWidth(String.valueOf(counts[i]))) / 2, y - 5);
                }
            }
        };
        chart.setBackground(Color.WHITE);

        JButton refresh = UITheme.primaryButton("Refresh Chart");
        refresh.addActionListener(e -> chart.repaint());

        wrapper.add(chart, BorderLayout.CENTER);
        wrapper.add(refresh, BorderLayout.SOUTH);
        return wrapper;
    }

    // ------------------------------------------------------------------
    // Booking History with Search and CSV Export
    // ------------------------------------------------------------------

    private JPanel buildBookingHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        historyModel = new DefaultTableModel(
                new Object[]{"Booking ID", "Student ID", "Student Name", "Room", "Check-In", "Check-Out", "Status", "Total Cost"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        UITheme.styleTable(historyTable);

        rowSorter = new TableRowSorter<>(historyModel);
        historyTable.setRowSorter(rowSorter);

        JTextField searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterHistory(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterHistory(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) { filterHistory(searchField.getText()); }
        });

        JButton exportBtn = UITheme.successButton("Export to CSV");
        exportBtn.addActionListener(e -> exportToCSV(historyTable));

        JPanel topBar = new JPanel();
        topBar.setBackground(UITheme.LIGHT_BG);
        topBar.add(new JLabel("Search:"));
        topBar.add(searchField);
        topBar.add(exportBtn);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    private void filterHistory(String text) {
        if (text == null || text.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void refreshHistory() {
        historyModel.setRowCount(0);
        for (Booking b : bookingManager.getAllBookings()) {
            double cost = 0;
            if (b.getRoom() != null) {
                cost = utils.DateUtils.calculateTotalCost(
                        b.getRoom().getPricePerMonth(),
                        b.getCheckInDate(), b.getCheckOutDate());
            }
            historyModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getStudent() != null ? b.getStudent().getId() : "",
                    b.getStudent() != null ? b.getStudent().getName() : "",
                    b.getRoom() != null ? b.getRoom().getRoomNumber() : "",
                    b.getCheckInDate(),
                    b.getCheckOutDate(),
                    b.getStatus(),
                    String.format("%.2f", cost)
            });
        }
    }

    private void exportToCSV(JTable table) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/report_export.csv"))) {
            // Header
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (i > 0) header.append(",");
                header.append(table.getColumnName(i));
            }
            writer.write(header.toString());
            writer.newLine();
            // Rows (filtered)
            for (int row = 0; row < table.getRowCount(); row++) {
                StringBuilder rowSb = new StringBuilder();
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col > 0) rowSb.append(",");
                    Object val = table.getValueAt(row, col);
                    rowSb.append(val == null ? "" : val.toString().replace(",", ";"));
                }
                writer.write(rowSb.toString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Exported to data/report_export.csv",
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Maintenance Summary
    // ------------------------------------------------------------------

    private JPanel buildMaintenanceSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(UITheme.LIGHT_BG);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel grid = new JPanel(new GridLayout(4, 2, 8, 8));
        grid.setBackground(UITheme.LIGHT_BG);

        totalRequestsLabel = addStatRow(grid, "Total Requests:");
        pendingLabel        = addStatRow(grid, "Pending:");
        inProgressLabel     = addStatRow(grid, "In Progress:");
        resolvedLabel       = addStatRow(grid, "Resolved:");

        JButton refresh = UITheme.primaryButton("Refresh");
        refresh.addActionListener(e -> refreshMaintenance());

        panel.add(grid, BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMaintenance() {
        List<MaintenanceRequest> all = maintenanceManager.getAllRequests();
        int pending = 0, inProgress = 0, resolved = 0;
        for (MaintenanceRequest req : all) {
            if ("Pending".equalsIgnoreCase(req.getStatus())) pending++;
            else if ("In Progress".equalsIgnoreCase(req.getStatus())) inProgress++;
            else if ("Resolved".equalsIgnoreCase(req.getStatus())) resolved++;
        }
        totalRequestsLabel.setText(String.valueOf(all.size()));
        pendingLabel.setText(String.valueOf(pending));
        inProgressLabel.setText(String.valueOf(inProgress));
        resolvedLabel.setText(String.valueOf(resolved));
    }

    /**
     * Refreshes all sub-tabs.
     */
    public void refreshAll() {
        refreshRevenue();
        refreshHistory();
        refreshMaintenance();
    }
}

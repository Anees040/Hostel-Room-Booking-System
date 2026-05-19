package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.AbstractRoom;
import models.DoubleRoom;
import models.RoomReview;
import models.SingleRoom;
import models.SuiteRoom;
import services.RoomManager;

/**
 * Admin room management panel with table and CRUD-like actions.
 * Demonstrates Layout Managers (Lab 13) and Event-Driven Programming (Lab 14).
 */
public class RoomPanel extends JPanel {

    private final RoomManager roomManager;
    private final DefaultTableModel tableModel;
    private final JTable roomTable;
    private JLabel roomCountLabel;

    /**
     * Constructs the room panel.
     *
     * @param roomManager room manager dependency
     */
    public RoomPanel(RoomManager roomManager) {
        this.roomManager = roomManager;

        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.LIGHT_BG);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // North — filter + count
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.setBackground(UITheme.LIGHT_BG);

        JComboBox<String> typeFilter = new JComboBox<>(new String[]{"All", "Single", "Double", "Suite"});
        typeFilter.setFont(UITheme.BODY_FONT);
        JButton refreshBtn = UITheme.primaryButton("Refresh");
        roomCountLabel = new JLabel("Rooms: 0");
        roomCountLabel.setFont(UITheme.BODY_FONT);

        northPanel.add(new JLabel("Filter:"));
        northPanel.add(typeFilter);
        northPanel.add(refreshBtn);
        northPanel.add(roomCountLabel);

        refreshBtn.addActionListener(e -> refreshTable((String) typeFilter.getSelectedItem()));
        typeFilter.addActionListener(e -> refreshTable((String) typeFilter.getSelectedItem()));

        add(northPanel, BorderLayout.NORTH);

        // Center — table
        tableModel = new DefaultTableModel(
                new Object[]{"Type", "Room No", "Floor", "Price/Month", "Available", "Amenities", "Avg Rating", "Max Occ."}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        roomTable = new JTable(tableModel);
        UITheme.styleTable(roomTable);
        roomTable.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        // South — action buttons
        JPanel southPanel = new JPanel(new GridLayout(1, 4, 6, 6));
        southPanel.setBackground(UITheme.LIGHT_BG);
        southPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton addBtn     = UITheme.successButton("Add Room");
        JButton deleteBtn  = UITheme.dangerButton("Delete Room");
        JButton availBtn   = UITheme.primaryButton("Mark Available");
        JButton detailsBtn = UITheme.primaryButton("View Details");

        addBtn.addActionListener(e -> showAddRoomDialog());
        deleteBtn.addActionListener(e -> deleteSelectedRoom());
        availBtn.addActionListener(e -> markSelectedRoomAvailable());
        detailsBtn.addActionListener(e -> viewRoomDetails());

        southPanel.add(addBtn);
        southPanel.add(deleteBtn);
        southPanel.add(availBtn);
        southPanel.add(detailsBtn);
        add(southPanel, BorderLayout.SOUTH);

        refreshTable("All");
    }

    /**
     * Reloads table with type filter.
     *
     * @param typeFilter "All" or specific type name
     */
    public final void refreshTable(String typeFilter) {
        tableModel.setRowCount(0);
        List<AbstractRoom> rooms;
        if (typeFilter == null || "All".equalsIgnoreCase(typeFilter)) {
            rooms = roomManager.getAllRooms();
        } else {
            rooms = roomManager.getRoomsByType(typeFilter);
        }
        for (AbstractRoom room : rooms) {
            double avg = roomManager.getAverageRating(room.getRoomNumber());
            tableModel.addRow(new Object[]{
                    room.getRoomType(),
                    room.getRoomNumber(),
                    room.getFloor(),
                    room.getPricePerMonth(),
                    room.isAvailable() ? "Yes" : "No",
                    room.getAmenities(),
                    avg > 0 ? String.format("%.1f", avg) : "—",
                    room.getMaxOccupancy()
            });
        }
        roomCountLabel.setText("Rooms: " + rooms.size());
    }

    /**
     * Reloads table with no filter (shows all).
     */
    public final void refreshTable() {
        refreshTable("All");
    }

    private void showAddRoomDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add Room", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 340);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY, 1),
                new EmptyBorder(14, 14, 14, 14)));
        panel.setBackground(UITheme.LIGHT_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> typeCombo    = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        JTextField roomNoField         = new JTextField();
        JSpinner floorSpinner          = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JTextField priceField          = new JTextField("8000");
        JTextField amenitiesField      = new JTextField("WiFi");

        addFormRow(panel, gbc, 0, "Type:", typeCombo);
        addFormRow(panel, gbc, 1, "Room No:", roomNoField);
        addFormRow(panel, gbc, 2, "Floor (1-10):", floorSpinner);
        addFormRow(panel, gbc, 3, "Price/Month:", priceField);
        addFormRow(panel, gbc, 4, "Amenities:", amenitiesField);

        typeCombo.addActionListener(e -> {
            String sel = (String) typeCombo.getSelectedItem();
            if ("Single".equalsIgnoreCase(sel)) priceField.setText("8000");
            else if ("Double".equalsIgnoreCase(sel)) priceField.setText("12000");
            else priceField.setText("20000");
        });

        JButton addBtn = UITheme.successButton("Add");
        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            String roomNo    = roomNoField.getText().trim();
            String priceText = priceField.getText().trim();
            String amenities = amenitiesField.getText().trim();
            String type      = (String) typeCombo.getSelectedItem();
            int floor        = (Integer) floorSpinner.getValue();

            if (roomNo.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Room number and price are required.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (roomManager.findRoom(roomNo) != null) {
                JOptionPane.showMessageDialog(dialog, "Room number already exists.",
                        "Duplicate Room", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double price = Double.parseDouble(priceText);
                AbstractRoom room = createRoomByType(type, roomNo, floor, price, true, amenities);
                if (room == null) {
                    JOptionPane.showMessageDialog(dialog, "Invalid room type.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                roomManager.addRoom(room);
                refreshTable("All");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Price must be numeric.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private AbstractRoom createRoomByType(String type, String roomNo, int floor,
                                          double price, boolean available, String amenities) {
        if ("Single".equalsIgnoreCase(type)) return new SingleRoom(roomNo, floor, price, available, amenities);
        if ("Double".equalsIgnoreCase(type)) return new DoubleRoom(roomNo, floor, price, available, amenities);
        if ("Suite".equalsIgnoreCase(type))  return new SuiteRoom(roomNo, floor, price, available, amenities);
        return null;
    }

    private void deleteSelectedRoom() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomNo = String.valueOf(tableModel.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(this, "Delete room " + roomNo + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (roomManager.deleteRoom(roomNo)) {
                refreshTable("All");
                JOptionPane.showMessageDialog(this, "Room deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete room.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void markSelectedRoomAvailable() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomNo = String.valueOf(tableModel.getValueAt(row, 1));
        if (roomManager.markAvailable(roomNo)) {
            refreshTable("All");
            JOptionPane.showMessageDialog(this, "Room marked available.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewRoomDetails() {
        int row = roomTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String roomNo = String.valueOf(tableModel.getValueAt(row, 1));
        AbstractRoom room = roomManager.findRoom(roomNo);
        if (room == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append(room.toString()).append("\n\n");
        List<RoomReview> reviews = roomManager.getReviewsForRoom(roomNo);
        sb.append("Reviews (").append(reviews.size()).append("):\n");
        for (RoomReview rv : reviews) {
            sb.append("  ").append(rv.toString()).append("\n");
        }
        if (reviews.isEmpty()) sb.append("  No reviews yet.\n");

        JTextArea ta = new JTextArea(sb.toString(), 12, 40);
        ta.setEditable(false);
        ta.setFont(UITheme.BODY_FONT);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                "Room Details — " + roomNo, JOptionPane.PLAIN_MESSAGE);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.BODY_FONT);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}

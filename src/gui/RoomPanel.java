package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.AbstractRoom;
import models.DoubleRoom;
import models.SingleRoom;
import models.SuiteRoom;
import services.RoomManager;

/**
 * Admin room management panel with table and CRUD-like actions.
 */
public class RoomPanel extends JPanel {
    private static final Color DARK_BLUE = new Color(25, 78, 140);
    private static final Color GREEN = new Color(39, 174, 96);
    private static final Color RED = new Color(192, 57, 43);
    private static final Color ORANGE = new Color(230, 126, 34);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;

    private final RoomManager roomManager;
    private final DefaultTableModel tableModel;
    private final JTable roomTable;

    /**
     * Constructs the room panel.
     *
     * @param roomManager room manager dependency
     */
    public RoomPanel(RoomManager roomManager) {
        this.roomManager = roomManager;

        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Room Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(DARK_BLUE);

        tableModel = new DefaultTableModel(
                new Object[]{"Room No", "Type", "Capacity", "Price", "Status", "Amenities"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setRowHeight(24);
        roomTable.getTableHeader().setReorderingAllowed(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BG_COLOR);

        JButton addButton = new JButton("Add Room");
        addButton.setBackground(GREEN);
        addButton.setForeground(WHITE);

        JButton deleteButton = new JButton("Delete Room");
        deleteButton.setBackground(RED);
        deleteButton.setForeground(WHITE);

        JButton availableButton = new JButton("Mark Available");
        availableButton.setBackground(ORANGE);
        availableButton.setForeground(WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(availableButton);

        addButton.addActionListener(e -> showAddRoomDialog());
        deleteButton.addActionListener(e -> deleteSelectedRoom());
        availableButton.addActionListener(e -> markSelectedRoomAvailable());

        add(title, BorderLayout.NORTH);
        add(new JScrollPane(roomTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    /**
     * Reloads table contents from manager data.
     */
    public final void refreshTable() {
        tableModel.setRowCount(0);
        List<AbstractRoom> rooms = roomManager.getAllRooms();
        for (AbstractRoom room : rooms) {
            tableModel.addRow(new Object[]{
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getCapacity(),
                    room.getPrice(),
                    room.isAvailable() ? "Available" : "Booked",
                    room.getAmenities()
            });
        }
    }

    private void showAddRoomDialog() {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Add Room", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 330);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        panel.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField roomNoField = new JTextField();
        JTextField floorField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        JTextField priceField = new JTextField("8000");
        JTextField amenitiesField = new JTextField("WiFi");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Booked"});

        addRow(panel, gbc, 0, "Room No:", roomNoField);
        addRow(panel, gbc, 1, "Floor:", floorField);
        addRow(panel, gbc, 2, "Type:", typeCombo);
        addRow(panel, gbc, 3, "Price:", priceField);
        addRow(panel, gbc, 4, "Amenities:", amenitiesField);
        addRow(panel, gbc, 5, "Status:", statusCombo);

        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            if ("Single".equalsIgnoreCase(selected)) {
                priceField.setText("8000");
            } else if ("Double".equalsIgnoreCase(selected)) {
                priceField.setText("12000");
            } else {
                priceField.setText("20000");
            }
        });

        JButton saveButton = new JButton("Save");
        saveButton.setBackground(GREEN);
        saveButton.setForeground(WHITE);

        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            String roomNo = roomNoField.getText().trim();
            String floorText = floorField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String priceText = priceField.getText().trim();
            String amenities = amenitiesField.getText().trim();
            boolean available = "Available".equals(statusCombo.getSelectedItem());

            if (roomNo.isEmpty() || floorText.isEmpty() || priceText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Room number, floor and price are required.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (roomManager.findRoom(roomNo) != null) {
                JOptionPane.showMessageDialog(dialog, "Room number already exists.",
                        "Duplicate Room", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int floor = Integer.parseInt(floorText);
                double price = Double.parseDouble(priceText);
                AbstractRoom room = createRoomByType(type, roomNo, floor, price, available, amenities);
                if (room == null) {
                    JOptionPane.showMessageDialog(dialog, "Invalid room type selected.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                roomManager.addRoom(room);
                roomManager.save();
                refreshTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Floor and price must be numeric.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private AbstractRoom createRoomByType(String type, String roomNo, int floor,
                                          double price, boolean available, String amenities) {
        if ("Single".equalsIgnoreCase(type)) {
            return new SingleRoom(roomNo, floor, price, available, amenities);
        }
        if ("Double".equalsIgnoreCase(type)) {
            return new DoubleRoom(roomNo, floor, price, available, amenities);
        }
        if ("Suite".equalsIgnoreCase(type)) {
            return new SuiteRoom(roomNo, floor, price, available, amenities);
        }
        return null;
    }

    private void deleteSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNo = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        boolean deleted = roomManager.removeRoom(roomNo);
        if (deleted) {
            roomManager.save();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Room deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Could not delete selected room.",
                    "Action Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markSelectedRoomAvailable() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a room first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNo = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        AbstractRoom room = roomManager.findRoom(roomNo);
        if (room != null) {
            room.setAvailable(true);
            roomManager.save();
            refreshTable();
            JOptionPane.showMessageDialog(this, "Room marked available.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}

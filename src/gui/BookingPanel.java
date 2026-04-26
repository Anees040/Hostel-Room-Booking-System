package gui;

import exceptions.RoomNotAvailableException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import models.AbstractRoom;
import models.Booking;
import models.Student;
import services.BookingManager;
import services.RoomManager;

/**
 * Student booking panel for searching and booking available rooms.
 */
public class BookingPanel extends JPanel {
    private static final Color LIGHT_BLUE = new Color(52, 152, 219);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;

    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final Student student;

    private final JComboBox<String> filterCombo;
    private final DefaultTableModel tableModel;
    private final JTable roomTable;

    /**
     * Constructs booking panel.
     *
     * @param roomManager room manager dependency
     * @param bookingManager booking manager dependency
     * @param student logged-in student
     */
    public BookingPanel(RoomManager roomManager, BookingManager bookingManager, Student student) {
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
        this.student = student;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(BG_COLOR);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);

        topPanel.add(new JLabel("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All Types", "Single", "Double", "Suite"});
        topPanel.add(filterCombo);

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

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BG_COLOR);

        JButton bookButton = new JButton("Book Selected Room");
        bookButton.setBackground(LIGHT_BLUE);
        bookButton.setForeground(WHITE);
        bookButton.setFocusPainted(false);
        bottomPanel.add(bookButton);

        filterCombo.addActionListener(e -> refreshRooms());
        bookButton.addActionListener(e -> openBookingDialogForSelectedRoom());

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(roomTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshRooms();
    }

    /**
     * Refreshes visible available rooms according to filter.
     */
    public final void refreshRooms() {
        tableModel.setRowCount(0);

        String filter = (String) filterCombo.getSelectedItem();
        List<AbstractRoom> candidates = new ArrayList<>();

        if ("All Types".equalsIgnoreCase(filter)) {
            candidates = roomManager.getAvailableRooms();
        } else {
            for (AbstractRoom room : roomManager.getRoomsByType(filter)) {
                if (room.isAvailable()) {
                    candidates.add(room);
                }
            }
        }

        for (AbstractRoom room : candidates) {
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

    private void openBookingDialogForSelectedRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a room first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomNo = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        AbstractRoom selectedRoom = roomManager.findRoom(roomNo);

        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Selected room no longer exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            refreshRooms();
            return;
        }

        JDialog dialog = new JDialog();
        dialog.setTitle("Confirm Booking - Room " + roomNo);
        dialog.setModal(true);
        dialog.setSize(340, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
        panel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField checkInField = new JTextField(LocalDate.now().toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusMonths(1).toString());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Check-in Date:"), gbc);

        gbc.gridx = 1;
        panel.add(checkInField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Check-out Date:"), gbc);

        gbc.gridx = 1;
        panel.add(checkOutField, gbc);

        JButton confirm = new JButton("Confirm Booking");
        confirm.setBackground(LIGHT_BLUE);
        confirm.setForeground(WHITE);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(confirm, gbc);

        confirm.addActionListener(e -> {
            try {
                Booking booking = bookingManager.createBooking(
                        student,
                        selectedRoom,
                        checkInField.getText().trim(),
                        checkOutField.getText().trim()
                );

                if (booking != null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Booking successful. Booking ID: " + booking.getBookingId(),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshRooms();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Unable to create booking.",
                            "Booking Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RoomNotAvailableException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Room Not Available", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}

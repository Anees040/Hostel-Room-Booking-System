package gui;

import exceptions.InvalidBookingException;
import exceptions.RoomNotAvailableException;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import models.AbstractRoom;
import models.Booking;
import models.Student;
import services.BookingManager;
import utils.DateUtils;

/**
 * Modal dialog for confirming a room booking.
 * Demonstrates Event-Driven Programming (Lab 14) with DocumentListener.
 */
public class BookingDialog extends JDialog {

    private final Student student;
    private final AbstractRoom room;
    private final BookingManager bookingManager;

    private JTextField checkInField;
    private JTextField checkOutField;
    private JLabel durationLabel;
    private JLabel costLabel;

    /**
     * Constructs the booking dialog.
     *
     * @param parent         parent frame
     * @param student        the student making the booking
     * @param room           the room to be booked
     * @param bookingManager booking manager dependency
     */
    public BookingDialog(JFrame parent, Student student, AbstractRoom room, BookingManager bookingManager) {
        super(parent, "Book Room — " + room.getRoomNumber(), true);
        this.student = student;
        this.room = room;
        this.bookingManager = bookingManager;

        UITheme.applyNimbusLookAndFeel();
        buildUI();
        setSize(480, 380);
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(UITheme.LIGHT_BG);

        // North — room summary
        JPanel northPanel = new JPanel();
        northPanel.setBackground(UITheme.PRIMARY);
        northPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel summary = new JLabel("<html><font color='white'><b>Room: " + room.getRoomNumber()
                + "</b> &nbsp;|&nbsp; Type: " + room.getRoomType()
                + " &nbsp;|&nbsp; Floor: " + room.getFloor()
                + " &nbsp;|&nbsp; Price: Rs." + room.getPricePerMonth() + "/month"
                + "<br>Max Occupancy: " + room.getMaxOccupancy()
                + " &nbsp;|&nbsp; Amenities: " + room.getAmenities()
                + "</font></html>");
        summary.setFont(UITheme.BODY_FONT);
        northPanel.add(summary);
        add(northPanel, BorderLayout.NORTH);

        // Center — date fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UITheme.LIGHT_BG);
        centerPanel.setBorder(new EmptyBorder(16, 20, 8, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        checkInField = new JTextField(DateUtils.today(), 14);
        checkOutField = new JTextField("", 14);
        durationLabel = new JLabel("Duration: — days");
        durationLabel.setFont(UITheme.BODY_FONT);
        costLabel = new JLabel("Estimated Cost: Rs. —");
        costLabel.setFont(UITheme.BODY_FONT);

        addRow(centerPanel, gbc, 0, "Check-in Date:", checkInField);
        addRow(centerPanel, gbc, 1, "Check-out Date:", checkOutField);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        centerPanel.add(durationLabel, gbc);
        gbc.gridy = 3;
        centerPanel.add(costLabel, gbc);

        // DocumentListener to auto-update duration and cost
        DocumentListener dl = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateDuration(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateDuration(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateDuration(); }
        };
        checkInField.getDocument().addDocumentListener(dl);
        checkOutField.getDocument().addDocumentListener(dl);

        add(centerPanel, BorderLayout.CENTER);

        // South — buttons
        JPanel southPanel = new JPanel();
        southPanel.setBackground(UITheme.LIGHT_BG);
        southPanel.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        JButton confirmBtn = UITheme.primaryButton("Confirm Booking");
        JButton cancelBtn = UITheme.dangerButton("Cancel");

        confirmBtn.addActionListener(e -> handleConfirm());
        cancelBtn.addActionListener(e -> dispose());

        southPanel.add(confirmBtn);
        southPanel.add(cancelBtn);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.BODY_FONT);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void updateDuration() {
        String ci = checkInField.getText().trim();
        String co = checkOutField.getText().trim();
        if (DateUtils.isValidDate(ci) && DateUtils.isValidDate(co)
                && DateUtils.isCheckOutAfterCheckIn(ci, co)) {
            long days = DateUtils.daysBetween(ci, co);
            double cost = DateUtils.calculateTotalCost(room.getPricePerMonth(), ci, co);
            durationLabel.setText("Duration: " + days + " days");
            costLabel.setText("Estimated Cost: Rs. " + cost);
        } else {
            durationLabel.setText("Duration: — days");
            costLabel.setText("Estimated Cost: Rs. —");
        }
    }

    private void handleConfirm() {
        String ci = checkInField.getText().trim();
        String co = checkOutField.getText().trim();
        try {
            Booking booking = bookingManager.createBooking(student, room, ci, co);
            double cost = DateUtils.calculateTotalCost(room.getPricePerMonth(), ci, co);
            JOptionPane.showMessageDialog(this,
                    "<html><b>Booking Confirmed!</b><br>"
                    + "Booking ID: " + booking.getBookingId() + "<br>"
                    + "Room: " + room.getRoomNumber() + " (" + room.getRoomType() + ")<br>"
                    + "Check-in: " + ci + "<br>"
                    + "Check-out: " + co + "<br>"
                    + "Total Cost: Rs. " + cost + "</html>",
                    "Booking Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (RoomNotAvailableException | InvalidBookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}

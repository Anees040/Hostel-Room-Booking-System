package gui;

import exceptions.InvalidStudentException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import models.Student;
import services.BookingManager;
import services.RoomManager;
import services.StudentManager;

/**
 * Login screen for admin and student authentication.
 */
public class LoginFrame extends JFrame {
    private static final Color DARK_BLUE = new Color(25, 78, 140);
    private static final Color LIGHT_BLUE = new Color(52, 152, 219);
    private static final Color GREEN = new Color(39, 174, 96);
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private final RoomManager roomManager;
    private final StudentManager studentManager;
    private final BookingManager bookingManager;

    private JRadioButton studentRadio;
    private JRadioButton adminRadio;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton registerButton;

    /**
     * Constructs the login frame and initializes UI.
     *
     * @param roomManager room manager dependency
     * @param studentManager student manager dependency
     * @param bookingManager booking manager dependency
     */
    public LoginFrame(RoomManager roomManager, StudentManager studentManager, BookingManager bookingManager) {
        this.roomManager = roomManager;
        this.studentManager = studentManager;
        this.bookingManager = bookingManager;

        initializeFrame();
        buildUi();
        setVisible(true);
    }

    private void initializeFrame() {
        setTitle("COMSATS Hostel Management System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel heading = new JLabel("COMSATS Hostel Management System", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(DARK_BLUE);
        heading.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 225, 225)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        studentRadio = new JRadioButton("Student Login", true);
        adminRadio = new JRadioButton("Admin Login");

        studentRadio.setBackground(WHITE);
        adminRadio.setBackground(WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(studentRadio);
        group.add(adminRadio);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(studentRadio, gbc);

        gbc.gridx = 1;
        formPanel.add(adminRadio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(label("ID:"), gbc);

        idField = new JTextField(18);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(label("Password:"), gbc);

        passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(LIGHT_BLUE);
        loginButton.setForeground(WHITE);
        loginButton.setFocusPainted(false);

        registerButton = new JButton("Register");
        registerButton.setBackground(GREEN);
        registerButton.setForeground(WHITE);
        registerButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        formPanel.add(registerButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> showRegisterDialog());

        studentRadio.addActionListener(e -> refreshMode());
        adminRadio.addActionListener(e -> refreshMode());
        refreshMode();

        root.add(heading, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_DARK);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return lbl;
    }

    private void refreshMode() {
        registerButton.setVisible(studentRadio.isSelected());
    }

    private void handleLogin() {
        String id = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (adminRadio.isSelected()) {
            if ("admin".equals(id) && "admin123".equals(password)) {
                dispose();
                AdminDashboard adminDashboard = new AdminDashboard(roomManager, studentManager, bookingManager);
                adminDashboard.setVisible(true);
                return;
            }
            JOptionPane.showMessageDialog(this, "Invalid admin credentials.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student student = studentManager.login(id, password);
        if (student != null) {
            dispose();
            StudentDashboard studentDashboard = new StudentDashboard(student, roomManager, studentManager, bookingManager);
            studentDashboard.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid student ID or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Student Registration", true);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField contact = new JTextField();
        JPasswordField password = new JPasswordField();
        JTextField department = new JTextField();
        JTextField program = new JTextField();
        JTextField semester = new JTextField();

        addFormRow(panel, gbc, 0, "Student ID:", id);
        addFormRow(panel, gbc, 1, "Name:", name);
        addFormRow(panel, gbc, 2, "Contact:", contact);
        addFormRow(panel, gbc, 3, "Password:", password);
        addFormRow(panel, gbc, 4, "Department:", department);
        addFormRow(panel, gbc, 5, "Program:", program);
        addFormRow(panel, gbc, 6, "Semester:", semester);

        JButton saveButton = new JButton("Register");
        saveButton.setBackground(GREEN);
        saveButton.setForeground(WHITE);
        saveButton.setFocusPainted(false);

        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                int sem = Integer.parseInt(semester.getText().trim());
                Student s = new Student(
                        name.getText().trim(),
                        id.getText().trim(),
                        contact.getText().trim(),
                        new String(password.getPassword()),
                        department.getText().trim(),
                        program.getText().trim(),
                        sem
                );

                studentManager.registerStudent(s);
                JOptionPane.showMessageDialog(dialog, "Student registered successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Semester must be numeric.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidStudentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String title, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label(title), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }
}

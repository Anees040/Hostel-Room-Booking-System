package gui;

import exceptions.DuplicateStudentException;
import exceptions.InvalidStudentException;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import models.Admin;
import models.Student;
import services.BookingManager;
import services.MaintenanceManager;
import services.NotificationManager;
import services.RoomManager;
import services.StudentManager;

/**
 * Login screen with gradient sidebar and card layout.
 * Demonstrates CardLayout (Lab 13) and Event-Driven Programming (Lab 14).
 */
public class LoginFrame extends JFrame {

    private final StudentManager studentManager;
    private final RoomManager roomManager;
    private final BookingManager bookingManager;
    private final MaintenanceManager maintenanceManager;
    private final NotificationManager notificationManager;

    private JComboBox<String> roleCombo;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton registerLink;
    private final CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel;

    public LoginFrame(StudentManager studentManager, RoomManager roomManager,
                      BookingManager bookingManager, MaintenanceManager maintenanceManager,
                      NotificationManager notificationManager) {
        this.studentManager = studentManager;
        this.roomManager = roomManager;
        this.bookingManager = bookingManager;
        this.maintenanceManager = maintenanceManager;
        this.notificationManager = notificationManager;

        UITheme.applyNimbusLookAndFeel();
        setTitle("Hostel Management System — Login");
        setSize(860, 560);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());

        // LEFT — gradient branding panel
        JPanel left = buildBrandingPanel();
        left.setPreferredSize(new Dimension(320, 0));

        // RIGHT — card panel (login / register)
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(UITheme.LIGHT_BG);
        cardPanel.add(buildLoginCard(),    "login");
        cardPanel.add(buildRegisterCard(), "register");
        cardLayout.show(cardPanel, "login");

        root.add(left,       BorderLayout.WEST);
        root.add(cardPanel,  BorderLayout.CENTER);
        setContentPane(root);
    }

    // -----------------------------------------------------------------------
    // Branding panel (left side)
    // -----------------------------------------------------------------------

    private JPanel buildBrandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.HEADER_BG, 0, getHeight(), UITheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(6, 20, 6, 20);

        // Removed icon placeholder due to font issues

        JLabel title = new JLabel("<html><center>Hostel Management<br>System</center></html>", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        panel.add(title, gbc);

        JLabel sub = new JLabel("<html><center>CSC-241 OOP Project<br>2nd Semester</center></html>", SwingConstants.CENTER);
        sub.setFont(UITheme.LABEL_FONT);
        sub.setForeground(new Color(186, 211, 252));
        panel.add(sub, gbc);

        panel.add(Box.createVerticalStrut(30), gbc);

        // Feature bullets
        String[] features = {"- Browse & Book Rooms", "- Manage Maintenance", "- Leave Room Reviews", "- Real-time Notifications"};
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(UITheme.SMALL_FONT);
            fl.setForeground(new Color(186, 211, 252));
            fl.setBorder(new EmptyBorder(2, 0, 2, 0));
            panel.add(fl, gbc);
        }

        panel.add(Box.createVerticalStrut(20), gbc);
        JLabel version = new JLabel("v1.0  |  COMSATS University", SwingConstants.CENTER);
        version.setFont(UITheme.SMALL_FONT);
        version.setForeground(new Color(147, 197, 253));
        panel.add(version, gbc);

        return panel;
    }

    // -----------------------------------------------------------------------
    // Login card
    // -----------------------------------------------------------------------

    private JPanel buildLoginCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.LIGHT_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(32, 36, 28, 36)));
        card.setPreferredSize(new Dimension(380, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Title
        JLabel loginTitle = new JLabel("Welcome back");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        card.add(loginTitle, gbc);

        JLabel loginSub = new JLabel("Sign in to your account");
        loginSub.setFont(UITheme.LABEL_FONT);
        loginSub.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 22, 0);
        card.add(loginSub, gbc);

        // Role
        gbc.insets = new Insets(6, 0, 3, 0);
        gbc.gridy = 2; card.add(fieldLabel("Login as"), gbc);
        roleCombo = new JComboBox<>(new String[]{"Student", "Admin"});
        styleCombo(roleCombo);
        gbc.gridy = 3; card.add(roleCombo, gbc);

        // ID
        gbc.gridy = 4; card.add(fieldLabel("ID / Username"), gbc);
        idField = new JTextField();
        UITheme.styleTextField(idField);
        idField.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 5; card.add(idField, gbc);

        // Password
        gbc.gridy = 6; card.add(fieldLabel("Password"), gbc);
        passwordField = new JPasswordField();
        UITheme.styleTextField(passwordField);
        passwordField.setPreferredSize(new Dimension(0, 38));
        gbc.gridy = 7; card.add(passwordField, gbc);

        // Show pwd
        JCheckBox showPwd = new JCheckBox("Show password");
        showPwd.setBackground(UITheme.CARD_BG);
        showPwd.setFont(UITheme.SMALL_FONT);
        showPwd.setForeground(UITheme.TEXT_SECONDARY);
        showPwd.addActionListener(e -> passwordField.setEchoChar(showPwd.isSelected() ? (char) 0 : '●'));
        gbc.gridy = 8; gbc.insets = new Insets(2, 0, 14, 0);
        card.add(showPwd, gbc);

        // Login button
        JButton loginBtn = UITheme.primaryButton("  Sign In  ");
        loginBtn.setPreferredSize(new Dimension(0, 42));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 9; gbc.insets = new Insets(4, 0, 12, 0);
        card.add(loginBtn, gbc);

        // Register link
        registerLink = UITheme.outlineButton("Create Student Account");
        registerLink.setFont(UITheme.LABEL_FONT);
        registerLink.setForeground(UITheme.PRIMARY);
        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 0, 0);
        card.add(registerLink, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        registerLink.addActionListener(e -> cardLayout.show(cardPanel, "register"));
        roleCombo.addItemListener(e -> registerLink.setVisible("Student".equals(roleCombo.getSelectedItem())));

        // Enter key submits
        passwordField.addActionListener(e -> handleLogin());
        idField.addActionListener(e -> handleLogin());

        GridBagConstraints wgbc = new GridBagConstraints();
        wrapper.add(card, wgbc);
        return wrapper;
    }

    // -----------------------------------------------------------------------
    // Register card
    // -----------------------------------------------------------------------

    private JPanel buildRegisterCard() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.LIGHT_BG);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(24, 36, 20, 36)));
        card.setPreferredSize(new Dimension(400, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 2, 0);

        JLabel regTitle = new JLabel("Create Account");
        regTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        regTitle.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        card.add(regTitle, gbc);

        JLabel regSub = new JLabel("Register as a new student");
        regSub.setFont(UITheme.LABEL_FONT);
        regSub.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 16, 0);
        card.add(regSub, gbc);

        gbc.insets = new Insets(4, 0, 2, 0);

        JTextField regId       = styledField(); JTextField regName   = styledField();
        JTextField regContact  = styledField(); JPasswordField regPwd  = new JPasswordField();
        JPasswordField regPwd2 = new JPasswordField();
        JTextField regDept     = styledField(); JTextField regProg   = styledField();
        JSpinner regSemester   = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        regSemester.setFont(UITheme.BODY_FONT);
        UITheme.styleTextField(regPwd); UITheme.styleTextField(regPwd2);

        int r = 2;
        for (Object[] row : new Object[][]{
                {"Student ID (e.g. SP23-BSE-001)", regId},
                {"Full Name", regName},
                {"Contact (11 digits)", regContact},
                {"Password (min 8 chars)", regPwd},
                {"Confirm Password", regPwd2},
                {"Department (e.g. CS)", regDept},
                {"Program (e.g. BSE)", regProg},
                {"Semester (1–8)", regSemester}
        }) {
            gbc.gridy = r++; card.add(fieldLabel((String) row[0]), gbc);
            gbc.gridy = r++; card.add((java.awt.Component) row[1], gbc);
        }

        JPanel btnRow = new JPanel(new BorderLayout(10, 0));
        btnRow.setBackground(UITheme.CARD_BG);
        JButton backBtn = UITheme.outlineButton("← Back");
        JButton regBtn  = UITheme.successButton("Register");
        regBtn.setPreferredSize(new Dimension(140, 38));
        backBtn.setPreferredSize(new Dimension(100, 38));
        btnRow.add(backBtn, BorderLayout.WEST);
        btnRow.add(regBtn,  BorderLayout.EAST);

        gbc.gridy = r; gbc.insets = new Insets(14, 0, 0, 0);
        card.add(btnRow, gbc);

        backBtn.addActionListener(e -> cardLayout.show(cardPanel, "login"));
        regBtn.addActionListener(e -> {
            String pwd  = new String(regPwd.getPassword());
            String pwd2 = new String(regPwd2.getPassword());
            if (!pwd.equals(pwd2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int sem = (Integer) regSemester.getValue();
                studentManager.registerStudent(new Student(
                        regId.getText().trim(), regName.getText().trim(),
                        regContact.getText().trim(), pwd,
                        regDept.getText().trim(), regProg.getText().trim(), sem));
                JOptionPane.showMessageDialog(this, "✔ Registration successful! Please sign in.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "login");
            } catch (InvalidStudentException | DuplicateStudentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        GridBagConstraints wgbc = new GridBagConstraints();
        wrapper.add(card, wgbc);
        return wrapper;
    }

    // -----------------------------------------------------------------------
    // Login handler
    // -----------------------------------------------------------------------

    private void handleLogin() {
        String id       = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your ID and password.", "Required Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Admin".equals(roleCombo.getSelectedItem())) {
            if ("admin".equals(id) && "admin123".equals(password)) {
                Admin admin = new Admin("admin", "Administrator", "03000000000", "admin123");
                dispose();
                new AdminDashboard(admin, roomManager, studentManager, bookingManager,
                        maintenanceManager, notificationManager).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials. Try: admin / admin123",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        Student student = studentManager.login(id, password);
        if (student != null) {
            dispose();
            new StudentDashboard(student, roomManager, bookingManager,
                    maintenanceManager, notificationManager).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "<html>Invalid credentials.<br>Sample: <b>SP23-BSE-030 / ali12345</b></html>",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.LABEL_FONT);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        return lbl;
    }

    private static JTextField styledField() {
        JTextField f = new JTextField();
        UITheme.styleTextField(f);
        f.setPreferredSize(new Dimension(0, 36));
        return f;
    }

    private static void styleCombo(JComboBox<String> combo) {
        combo.setFont(UITheme.BODY_FONT);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(0, 36));
    }
}

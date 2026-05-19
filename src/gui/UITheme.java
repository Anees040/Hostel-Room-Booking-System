package gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Central theme constants and factory methods for consistent UI styling.
 * Demonstrates GUI theming and reusable component patterns (Lab 13, 14).
 */
public class UITheme {

    private UITheme() { }

    // ── Palette ─────────────────────────────────────────────────────────────
    public static final Color PRIMARY         = new Color(37, 99, 235);   // modern blue
    public static final Color PRIMARY_DARK    = new Color(29, 78, 216);
    public static final Color PRIMARY_LIGHT   = new Color(219, 234, 254);
    public static final Color SUCCESS         = new Color(22, 163, 74);
    public static final Color SUCCESS_DARK    = new Color(15, 118, 55);
    public static final Color DANGER          = new Color(220, 38, 38);
    public static final Color DANGER_DARK     = new Color(185, 28, 28);
    public static final Color WARNING         = new Color(217, 119, 6);
    public static final Color LIGHT_BG        = new Color(248, 250, 252);
    public static final Color CARD_BG         = new Color(255, 255, 255);
    public static final Color BORDER_COLOR    = new Color(226, 232, 240);
    public static final Color TEXT_PRIMARY    = new Color(15,  23,  42);
    public static final Color TEXT_SECONDARY  = new Color(100, 116, 139);
    public static final Color ROW_EVEN        = new Color(255, 255, 255);
    public static final Color ROW_ODD         = new Color(248, 250, 252);
    public static final Color ROW_UNAVAILABLE = new Color(254, 242, 242);
    public static final Color HEADER_BG       = new Color(30,  64, 175);

    // ── Fonts ────────────────────────────────────────────────────────────────
    public static final Font TITLE_FONT   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font HEADER_FONT  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font BODY_FONT    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL_FONT   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font LABEL_FONT   = new Font("Segoe UI", Font.PLAIN, 12);

    // ── Rounded Button ───────────────────────────────────────────────────────

    /**
     * Creates a styled rounded button with hover effect.
     */
    public static JButton primaryButton(String text) {
        return roundedButton(text, PRIMARY, PRIMARY_DARK, Color.WHITE);
    }

    public static JButton dangerButton(String text) {
        return roundedButton(text, DANGER, DANGER_DARK, Color.WHITE);
    }

    public static JButton successButton(String text) {
        return roundedButton(text, SUCCESS, SUCCESS_DARK, Color.WHITE);
    }

    public static JButton outlineButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(PRIMARY);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setBackground(CARD_BG);
        b.setForeground(PRIMARY);
        b.setFont(HEADER_FONT);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        return b;
    }

    private static JButton roundedButton(String text, Color normal, Color hover, Color fg) {
        JButton b = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hover : normal);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(fg);
        b.setFont(HEADER_FONT);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        return b;
    }

    // ── Table Styling ────────────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setRowHeight(32);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 36));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
    }

    // ── Input Field ──────────────────────────────────────────────────────────

    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
    }

    // ── Card Panel ───────────────────────────────────────────────────────────

    /**
     * Returns a white card-style JPanel with rounded border and shadow-like appearance.
     */
    public static JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 24, 20, 24)));
        return panel;
    }

    /**
     * Creates a section header label.
     */
    public static JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(HEADING_FONT);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lbl;
    }

    /**
     * Creates a gradient header panel with title and subtitle.
     */
    public static JPanel gradientHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new java.awt.BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, HEADER_BG, getWidth(), 0, PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setBorder(new EmptyBorder(14, 20, 14, 20));

        JPanel textPanel = new JPanel(new java.awt.GridLayout(subtitle.isEmpty() ? 1 : 2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(TITLE_FONT);
        titleLbl.setForeground(Color.WHITE);
        textPanel.add(titleLbl);

        if (!subtitle.isEmpty()) {
            JLabel subLbl = new JLabel(subtitle);
            subLbl.setFont(LABEL_FONT);
            subLbl.setForeground(new Color(186, 211, 252));
            textPanel.add(subLbl);
        }
        panel.add(textPanel, java.awt.BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a status badge label (colored pill).
     */
    public static JLabel statusBadge(String status) {
        JLabel lbl = new JLabel("  " + status + "  ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(SMALL_FONT);
        lbl.setOpaque(false);
        switch (status.toLowerCase()) {
            case "active":    lbl.setBackground(new Color(220, 252, 231)); lbl.setForeground(new Color(22, 101, 52)); break;
            case "cancelled": lbl.setBackground(new Color(254, 226, 226)); lbl.setForeground(new Color(153, 27, 27)); break;
            case "pending":   lbl.setBackground(new Color(254, 243, 199)); lbl.setForeground(new Color(146, 64, 14)); break;
            case "resolved":  lbl.setBackground(new Color(220, 252, 231)); lbl.setForeground(new Color(22, 101, 52)); break;
            case "in progress": lbl.setBackground(new Color(219, 234, 254)); lbl.setForeground(new Color(30, 64, 175)); break;
            default:          lbl.setBackground(new Color(241, 245, 249)); lbl.setForeground(TEXT_SECONDARY);
        }
        return lbl;
    }

    // ── Look and Feel ────────────────────────────────────────────────────────

    public static void applyNimbusLookAndFeel() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    // Override Nimbus defaults for cleaner look
                    javax.swing.UIManager.put("control",          new Color(248, 250, 252));
                    javax.swing.UIManager.put("info",             new Color(248, 250, 252));
                    javax.swing.UIManager.put("nimbusBase",       PRIMARY);
                    javax.swing.UIManager.put("nimbusBlueGrey",   new Color(148, 163, 184));
                    javax.swing.UIManager.put("nimbusFocus",      new Color(147, 197, 253));
                    javax.swing.UIManager.put("nimbusLightBackground", Color.WHITE);
                    break;
                }
            }
        } catch (Exception e) {
            // fallback to default
        }
    }
}

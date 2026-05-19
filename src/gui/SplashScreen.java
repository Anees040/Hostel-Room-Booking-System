package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Animated splash screen displayed before LoginFrame.
 * Demonstrates Java2D drawing (Lab 13/14).
 */
public class SplashScreen extends JWindow {

    private int progress = 0;

    private final JPanel canvas = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2d.setColor(UITheme.PRIMARY);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Title
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 28));
            drawCenteredString(g2d, "HOSTEL MANAGEMENT SYSTEM", getWidth(), 110);

            // Subtitle
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            drawCenteredString(g2d, "CSC-241 OOP Project", getWidth(), 150);

            // Loading text
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            drawCenteredString(g2d, "Loading...", getWidth(), 230);

            // Progress bar background
            int barX = 60;
            int barY = 250;
            int barW = getWidth() - 120;
            int barH = 10;
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.fillRoundRect(barX, barY, barW, barH, 8, 8);

            // Progress bar fill
            g2d.setColor(Color.WHITE);
            int filled = (int) (barW * (progress / 100.0));
            g2d.fillRoundRect(barX, barY, filled, barH, 8, 8);
        }

        private void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
            int strWidth = g2d.getFontMetrics().stringWidth(text);
            g2d.drawString(text, (width - strWidth) / 2, y);
        }
    };

    /**
     * Creates and shows the splash screen.
     */
    public SplashScreen() {
        setContentPane(canvas);
        canvas.setBackground(UITheme.PRIMARY);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Starts the splash animation and calls onComplete when finished.
     *
     * @param onComplete runnable called on the EDT after animation completes
     */
    public void show(Runnable onComplete) {
        Timer timer = new Timer(30, null);
        timer.addActionListener(e -> {
            progress += 2;
            canvas.repaint();
            if (progress >= 100) {
                timer.stop();
                dispose();
                SwingUtilities.invokeLater(onComplete);
            }
        });
        timer.start();
    }
}

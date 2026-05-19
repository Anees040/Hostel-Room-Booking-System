package gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom table cell renderer providing alternating row colors.
 * Also applies status-based colors for booking and availability states.
 * Demonstrates Event-Driven GUI customization (Lab 14).
 */
public class AlternatingRowRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setForeground(table.getForeground());

            // Check for special column values
            String cellValue = value == null ? "" : value.toString();

            // Check "Available" column (column 4 in room tables)
            Color rowBg = (row % 2 == 0) ? UITheme.ROW_EVEN : UITheme.ROW_ODD;

            // Status-based coloring for booking tables
            int statusCol = findColumnIndex(table, "Status");
            if (statusCol >= 0) {
                Object statusVal = table.getModel().getValueAt(row, statusCol);
                String status = statusVal == null ? "" : statusVal.toString();
                if ("Cancelled".equalsIgnoreCase(status)) {
                    rowBg = new Color(255, 240, 240);
                } else if ("Active".equalsIgnoreCase(status)) {
                    rowBg = new Color(240, 255, 240);
                }
            }

            // Availability-based coloring for room tables
            int availCol = findColumnIndex(table, "Available");
            if (availCol >= 0) {
                Object availVal = table.getModel().getValueAt(row, availCol);
                String avail = availVal == null ? "" : availVal.toString();
                if ("No".equalsIgnoreCase(avail) || "Booked".equalsIgnoreCase(avail)) {
                    rowBg = UITheme.ROW_UNAVAILABLE;
                }
            }

            // Status column in maintenance/room panel
            int statusColAlt = findColumnIndex(table, "Status");
            if (statusColAlt < 0) {
                // Also check for "Booked"/"Available" cell value directly
                if ("No".equalsIgnoreCase(cellValue) || "Booked".equalsIgnoreCase(cellValue)) {
                    rowBg = UITheme.ROW_UNAVAILABLE;
                }
            }

            setBackground(rowBg);
        }

        return this;
    }

    private int findColumnIndex(JTable table, String columnName) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(table.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }
}

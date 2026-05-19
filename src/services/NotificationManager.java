package services;

import interfaces.Notifiable;
import interfaces.Saveable;
import java.util.ArrayList;
import java.util.List;
import models.Notification;
import utils.IdGenerator;
import utils.DateUtils;

/**
 * Manages system notifications and persistence.
 * Implements Saveable (Lab 12) and Notifiable (Lab 10) interfaces.
 */
public class NotificationManager implements Saveable, Notifiable {

    private final ArrayList<Notification> notifications = new ArrayList<>();
    private int notifCounter = 1;
    private static final String NOTIF_FILE = "data/notifications.txt";

    /**
     * Creates the notification manager.
     */
    public NotificationManager() {
        // Dependencies: none
    }

    @Override
    public void sendNotification(String recipientId, String message) {
        String id = IdGenerator.generateNotificationId(notifCounter++);
        Notification notification = new Notification(id, message, DateUtils.today(), recipientId, false);
        notifications.add(notification);
        save();
    }

    @Override
    public List<Notification> getNotificationsForUser(String userId) {
        List<Notification> result = new ArrayList<>();
        for (Notification n : notifications) {
            if (n.getRecipientId() != null && n.getRecipientId().equalsIgnoreCase(userId)) {
                result.add(n);
            }
        }
        return result;
    }

    @Override
    public void markNotificationRead(String notificationId) {
        for (Notification n : notifications) {
            if (n.getNotificationId().equalsIgnoreCase(notificationId)) {
                n.setRead(true);
                save();
                return;
            }
        }
    }

    @Override
    public long getUnreadCount(String userId) {
        long count = 0;
        for (Notification n : notifications) {
            if (n.getRecipientId() != null && n.getRecipientId().equalsIgnoreCase(userId) && !n.isRead()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns all notifications.
     *
     * @return copy of notifications list
     */
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notifications);
    }

    @Override
    public void save() {
        List<String> lines = new ArrayList<>();
        for (Notification n : notifications) {
            String line = sanitize(n.getNotificationId()) + "|"
                    + sanitize(n.getRecipientId()) + "|"
                    + sanitize(n.getMessage()) + "|"
                    + sanitize(n.getDateCreated()) + "|"
                    + n.isRead();
            lines.add(line);
        }
        FileManager.writeToFile(NOTIF_FILE, lines);
    }

    @Override
    public void load() {
        notifications.clear();
        List<String> lines = FileManager.readFromFile(NOTIF_FILE);
        int maxCounter = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) {
                continue;
            }
            try {
                String id = parts[0].trim();
                String recipientId = parts[1].trim();
                String message = parts[2].trim();
                String dateCreated = parts[3].trim();
                boolean isRead = Boolean.parseBoolean(parts[4].trim());

                notifications.add(new Notification(id, message, dateCreated, recipientId, isRead));

                int num = parseCounter(id);
                if (num > maxCounter) {
                    maxCounter = num;
                }
            } catch (Exception e) {
                System.err.println("Skipping malformed notification line: " + line);
            }
        }
        notifCounter = maxCounter + 1;
    }

    private int parseCounter(String id) {
        try {
            String num = id.replaceAll("[^0-9]", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/").trim();
    }
}

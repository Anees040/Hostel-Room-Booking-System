package interfaces;

import java.util.List;
import models.Notification;

/**
 * Defines notification operations for sending and reading notifications.
 */
public interface Notifiable {

    /**
     * Sends a notification to a user.
     *
     * @param recipientId the recipient user ID
     * @param message     the notification message
     */
    void sendNotification(String recipientId, String message);

    /**
     * Returns all notifications for a specific user.
     *
     * @param userId the user ID
     * @return list of notifications for the user
     */
    List<Notification> getNotificationsForUser(String userId);

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId the notification ID
     */
    void markNotificationRead(String notificationId);

    /**
     * Returns the unread notification count for a user.
     *
     * @param userId the user ID
     * @return count of unread notifications
     */
    long getUnreadCount(String userId);
}

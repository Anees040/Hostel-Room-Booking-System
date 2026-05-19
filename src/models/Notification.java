package models;

/**
 * Represents a system notification sent to a user.
 */
public class Notification {
    private String notificationId;
    private String message;
    private String dateCreated;
    private String recipientId;
    private boolean isRead;

    /**
     * Constructs a notification.
     *
     * @param notificationId unique notification identifier
     * @param message        notification message text
     * @param dateCreated    creation date
     * @param recipientId    recipient user ID
     * @param isRead         whether notification has been read
     */
    public Notification(String notificationId, String message,
                         String dateCreated, String recipientId, boolean isRead) {
        this.notificationId = notificationId;
        this.message = message;
        this.dateCreated = dateCreated;
        this.recipientId = recipientId;
        this.isRead = isRead;
    }

    public String getNotificationId() { return notificationId; }
    public String getMessage() { return message; }
    public String getDateCreated() { return dateCreated; }
    public String getRecipientId() { return recipientId; }
    public boolean isRead() { return isRead; }

    public void setRead(boolean read) { this.isRead = read; }

    @Override
    public String toString() {
        return notificationId + " | To: " + recipientId + " | " + message
                + " | Date: " + dateCreated + " | Read: " + (isRead ? "Yes" : "No");
    }
}

package models;

/**
 * Represents a maintenance request submitted by a student for a room.
 */
public class MaintenanceRequest {
    private String requestId;
    private String description;
    private String status;
    private String requestedDate;
    private String resolvedDate;
    private Student requestedBy;
    private AbstractRoom room;

    /**
     * Constructs a maintenance request.
     *
     * @param requestId     unique request identifier
     * @param description   description of the issue
     * @param status        current status (Pending/In Progress/Resolved)
     * @param requestedDate date the request was submitted
     * @param resolvedDate  date the request was resolved (may be empty)
     * @param requestedBy   student who submitted the request
     * @param room          room the request is for
     */
    public MaintenanceRequest(String requestId, String description, String status,
                               String requestedDate, String resolvedDate,
                               Student requestedBy, AbstractRoom room) {
        this.requestId = requestId;
        this.description = description;
        this.status = status;
        this.requestedDate = requestedDate;
        this.resolvedDate = resolvedDate;
        this.requestedBy = requestedBy;
        this.room = room;
    }

    public String getRequestId() { return requestId; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getRequestedDate() { return requestedDate; }
    public String getResolvedDate() { return resolvedDate; }
    public Student getRequestedBy() { return requestedBy; }
    public AbstractRoom getRoom() { return room; }

    public void setStatus(String status) { this.status = status; }
    public void setResolvedDate(String resolvedDate) { this.resolvedDate = resolvedDate; }

    @Override
    public String toString() {
        return requestId + " | Room: " + (room != null ? room.getRoomNumber() : "N/A")
                + " | " + description + " | Status: " + status
                + " | Requested: " + requestedDate
                + " | Resolved: " + (resolvedDate == null || resolvedDate.isEmpty() ? "N/A" : resolvedDate);
    }
}

package services;

import exceptions.MaintenanceException;
import interfaces.Maintainable;
import interfaces.Saveable;
import java.util.ArrayList;
import java.util.List;
import models.AbstractRoom;
import models.MaintenanceRequest;
import models.Student;
import utils.DateUtils;
import utils.IdGenerator;

/**
 * Manages maintenance requests and persistence.
 * Implements Saveable (Lab 12) and Maintainable (Lab 10) interfaces.
 */
public class MaintenanceManager implements Saveable, Maintainable {

    private final ArrayList<MaintenanceRequest> requests = new ArrayList<>();
    private final RoomManager roomManager;
    private final NotificationManager notificationManager;
    private int requestCounter = 1;
    private static final String MAINTENANCE_FILE = "data/maintenance.txt";

    /**
     * Constructs MaintenanceManager with dependencies.
     *
     * @param roomManager          room manager dependency
     * @param notificationManager  notification manager dependency
     */
    public MaintenanceManager(RoomManager roomManager, NotificationManager notificationManager) {
        this.roomManager = roomManager;
        this.notificationManager = notificationManager;
    }

    @Override
    public void submitMaintenanceRequest(MaintenanceRequest request) {
        if (request == null || request.getDescription() == null || request.getDescription().trim().isEmpty()) {
            return;
        }
        requests.add(request);
        save();
        notificationManager.sendNotification("ADMIN",
                "New maintenance request " + request.getRequestId()
                        + " submitted for room "
                        + (request.getRoom() != null ? request.getRoom().getRoomNumber() : "N/A") + ".");
    }

    @Override
    public List<MaintenanceRequest> getPendingRequests() {
        List<MaintenanceRequest> result = new ArrayList<>();
        for (MaintenanceRequest req : requests) {
            if ("Pending".equalsIgnoreCase(req.getStatus())) {
                result.add(req);
            }
        }
        return result;
    }

    @Override
    public void updateRequestStatus(String requestId, String newStatus) throws MaintenanceException {
        if (newStatus == null || (!newStatus.equals("Pending")
                && !newStatus.equals("In Progress") && !newStatus.equals("Resolved"))) {
            throw new MaintenanceException("Invalid status: " + newStatus
                    + ". Must be Pending, In Progress, or Resolved.");
        }

        for (MaintenanceRequest req : requests) {
            if (req.getRequestId().equalsIgnoreCase(requestId)) {
                req.setStatus(newStatus);
                if ("Resolved".equals(newStatus)) {
                    req.setResolvedDate(DateUtils.today());
                    if (req.getRequestedBy() != null) {
                        notificationManager.sendNotification(req.getRequestedBy().getId(),
                                "Your maintenance request " + req.getRequestId()
                                        + " status updated to: Resolved.");
                    }
                }
                save();
                return;
            }
        }
        throw new MaintenanceException("Maintenance request not found: " + requestId);
    }

    @Override
    public List<MaintenanceRequest> getRequestsForRoom(String roomNumber) {
        List<MaintenanceRequest> result = new ArrayList<>();
        for (MaintenanceRequest req : requests) {
            if (req.getRoom() != null && req.getRoom().getRoomNumber().equalsIgnoreCase(roomNumber)) {
                result.add(req);
            }
        }
        return result;
    }

    /**
     * Returns all maintenance requests.
     *
     * @return copy of all requests
     */
    public List<MaintenanceRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    /**
     * Returns the current request counter value.
     *
     * @return request counter
     */
    public int getRequestCounter() {
        return requestCounter;
    }

    /**
     * Returns the next request ID using the counter.
     *
     * @return new request ID
     */
    public String generateNextRequestId() {
        return IdGenerator.generateRequestId(requestCounter++);
    }

    @Override
    public void save() {
        List<String> lines = new ArrayList<>();
        for (MaintenanceRequest req : requests) {
            String studentId = req.getRequestedBy() != null ? req.getRequestedBy().getId() : "";
            String roomNumber = req.getRoom() != null ? req.getRoom().getRoomNumber() : "";
            String line = sanitize(req.getRequestId()) + "|"
                    + sanitize(studentId) + "|"
                    + sanitize(roomNumber) + "|"
                    + sanitize(req.getDescription()) + "|"
                    + sanitize(req.getStatus()) + "|"
                    + sanitize(req.getRequestedDate()) + "|"
                    + sanitize(req.getResolvedDate() == null ? "" : req.getResolvedDate());
            lines.add(line);
        }
        FileManager.writeToFile(MAINTENANCE_FILE, lines);
    }

    @Override
    public void load() {
        requests.clear();
        List<String> lines = FileManager.readFromFile(MAINTENANCE_FILE);
        int maxCounter = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                System.err.println("Skipping malformed maintenance line: " + line);
                continue;
            }
            try {
                String requestId = parts[0].trim();
                String studentId = parts[1].trim();
                String roomNumber = parts[2].trim();
                String description = parts[3].trim();
                String status = parts[4].trim();
                String requestedDate = parts[5].trim();
                String resolvedDate = parts[6].trim();

                // Try to find the student from RoomManager's context — we need StudentManager
                // Student resolution is done via find-by-room workaround; store student ID only
                // Since MaintenanceManager doesn't have StudentManager, we resolve at load via null-safe approach
                AbstractRoom room = roomManager.findRoom(roomNumber);

                // Create a minimal student for ID reference (workaround for lack of StudentManager)
                Student student = new Student("Unknown", studentId, "", "", "", "", 1);
                MaintenanceRequest reqWithStudent = new MaintenanceRequest(
                        requestId, description, status, requestedDate, resolvedDate, student, room);

                requests.add(reqWithStudent);

                int num = parseCounter(requestId);
                if (num > maxCounter) {
                    maxCounter = num;
                }
            } catch (Exception e) {
                System.err.println("Skipping invalid maintenance line: " + line);
            }
        }
        requestCounter = maxCounter + 1;
    }

    /**
     * Reloads maintenance data with full student resolution.
     * Call this after StudentManager has been initialized.
     *
     * @param studentManager the student manager for resolving student references
     */
    public void reloadWithStudentManager(services.StudentManager studentManager) {
        requests.clear();
        List<String> lines = FileManager.readFromFile(MAINTENANCE_FILE);
        int maxCounter = 0;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                continue;
            }
            try {
                String requestId = parts[0].trim();
                String studentId = parts[1].trim();
                String roomNumber = parts[2].trim();
                String description = parts[3].trim();
                String status = parts[4].trim();
                String requestedDate = parts[5].trim();
                String resolvedDate = parts[6].trim();

                AbstractRoom room = roomManager.findRoom(roomNumber);
                Student student = studentManager.findStudent(studentId);

                requests.add(new MaintenanceRequest(requestId, description, status, requestedDate, resolvedDate, student, room));

                int num = parseCounter(requestId);
                if (num > maxCounter) {
                    maxCounter = num;
                }
            } catch (Exception e) {
                System.err.println("Skipping invalid maintenance line: " + line);
            }
        }
        requestCounter = maxCounter + 1;
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

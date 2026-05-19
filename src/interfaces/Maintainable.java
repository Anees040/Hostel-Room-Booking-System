package interfaces;

import exceptions.MaintenanceException;
import java.util.List;
import models.MaintenanceRequest;

/**
 * Defines maintenance request operations.
 */
public interface Maintainable {

    /**
     * Submits a new maintenance request.
     *
     * @param request the request to submit
     */
    void submitMaintenanceRequest(MaintenanceRequest request);

    /**
     * Returns all pending maintenance requests.
     *
     * @return list of pending requests
     */
    List<MaintenanceRequest> getPendingRequests();

    /**
     * Updates the status of an existing maintenance request.
     *
     * @param requestId  request identifier
     * @param newStatus  new status value
     * @throws MaintenanceException if the request is not found or status is invalid
     */
    void updateRequestStatus(String requestId, String newStatus) throws MaintenanceException;

    /**
     * Returns all maintenance requests for a specific room.
     *
     * @param roomNumber the room number
     * @return list of requests for the room
     */
    List<MaintenanceRequest> getRequestsForRoom(String roomNumber);
}

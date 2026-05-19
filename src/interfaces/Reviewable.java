package interfaces;

import java.util.List;
import models.RoomReview;

/**
 * Defines review operations for rooms.
 */
public interface Reviewable {

    /**
     * Adds a review to the system.
     *
     * @param review the review to add
     */
    void addReview(RoomReview review);

    /**
     * Returns all reviews for a specific room.
     *
     * @param roomNumber the room number
     * @return list of reviews for the room
     */
    List<RoomReview> getReviewsForRoom(String roomNumber);

    /**
     * Calculates and returns the average rating for a room.
     *
     * @param roomNumber the room number
     * @return average rating, or 0.0 if no reviews
     */
    double getAverageRating(String roomNumber);
}

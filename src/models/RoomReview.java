package models;

/**
 * Represents a student review for a room.
 */
public class RoomReview {
    private String reviewId;
    private String comment;
    private String reviewDate;
    private int rating;
    private Student reviewer;
    private AbstractRoom room;

    /**
     * Constructs a room review.
     *
     * @param reviewId   unique review identifier
     * @param comment    review text
     * @param reviewDate date the review was submitted
     * @param rating     rating from 1 to 5
     * @param reviewer   student who wrote the review
     * @param room       room being reviewed
     */
    public RoomReview(String reviewId, String comment, String reviewDate,
                       int rating, Student reviewer, AbstractRoom room) {
        this.reviewId = reviewId;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.reviewer = reviewer;
        this.room = room;
    }

    public String getReviewId() { return reviewId; }
    public String getComment() { return comment; }
    public String getReviewDate() { return reviewDate; }
    public int getRating() { return rating; }
    public Student getReviewer() { return reviewer; }
    public AbstractRoom getRoom() { return room; }

    /**
     * Validates the rating is between 1 and 5 inclusive.
     *
     * @return true if rating is valid
     */
    public boolean isValidRating() {
        return rating >= 1 && rating <= 5;
    }

    @Override
    public String toString() {
        return reviewId + " | Room: " + (room != null ? room.getRoomNumber() : "N/A")
                + " | Rating: " + rating + "/5 | " + comment
                + " | By: " + (reviewer != null ? reviewer.getName() : "N/A")
                + " | Date: " + reviewDate;
    }
}

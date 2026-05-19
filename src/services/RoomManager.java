package services;

import interfaces.Reviewable;
import interfaces.Saveable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import models.AbstractRoom;
import models.DoubleRoom;
import models.RoomReview;
import models.SingleRoom;
import models.SuiteRoom;
import utils.DataStore;

/**
 * Manages room inventory and persistence.
 * Implements Saveable (Lab 12) and Reviewable (Lab 10) interfaces.
 * Also uses DataStore<AbstractRoom> to demonstrate Generics (Lab 11).
 */
public class RoomManager implements Saveable, Reviewable {
    private final ArrayList<AbstractRoom> rooms = new ArrayList<>();
    private final ArrayList<RoomReview> reviews = new ArrayList<>();
    private final DataStore<AbstractRoom> roomStore = new DataStore<>();

    private static final String ROOMS_FILE = "data/rooms.txt";
    private static final String REVIEWS_FILE = "data/reviews.txt";

    /**
     * Creates the room manager. Data must be loaded externally via load().
     */
    public RoomManager() {
        // Data is loaded externally
    }

    // -----------------------------------------------------------------------
    // Saveable implementation
    // -----------------------------------------------------------------------

    @Override
    public void save() {
        List<String> lines = new ArrayList<>();
        for (AbstractRoom room : rooms) {
            String amenities = room.getAmenities() == null ? "" : room.getAmenities().replace("|", "/");
            lines.add(room.getRoomType() + "|"
                    + room.getRoomNumber() + "|"
                    + room.getFloor() + "|"
                    + room.getPricePerMonth() + "|"
                    + room.isAvailable() + "|"
                    + amenities);
        }
        FileManager.writeToFile(ROOMS_FILE, lines);
        saveReviews();
    }

    @Override
    public void load() {
        rooms.clear();
        roomStore.clear();
        List<String> lines = FileManager.readFromFile(ROOMS_FILE);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 6) {
                System.err.println("Skipping malformed room line: " + line);
                continue;
            }
            try {
                String type = parts[0].trim();
                String roomNumber = parts[1].trim();
                int floor = Integer.parseInt(parts[2].trim());
                double price = Double.parseDouble(parts[3].trim());
                boolean available = Boolean.parseBoolean(parts[4].trim());
                String amenities = parts[5].trim();

                AbstractRoom room = createRoomByType(type, roomNumber, floor, price, available, amenities);
                if (room != null) {
                    rooms.add(room);
                    roomStore.add(room);
                }
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid room line: " + line);
            }
        }
        loadReviews();
    }

    private void saveReviews() {
        List<String> lines = new ArrayList<>();
        for (RoomReview rv : reviews) {
            String studentId = rv.getReviewer() != null ? rv.getReviewer().getId() : "";
            String roomNo = rv.getRoom() != null ? rv.getRoom().getRoomNumber() : "";
            lines.add(rv.getReviewId() + "|"
                    + studentId + "|"
                    + roomNo + "|"
                    + rv.getRating() + "|"
                    + rv.getComment().replace("|", "/") + "|"
                    + rv.getReviewDate());
        }
        FileManager.writeToFile(REVIEWS_FILE, lines);
    }

    private void loadReviews() {
        reviews.clear();
        List<String> lines = FileManager.readFromFile(REVIEWS_FILE);
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 6) {
                continue;
            }
            try {
                String reviewId = parts[0].trim();
                // parts[1] = studentId (can't resolve without StudentManager, stored for display)
                String roomNo = parts[2].trim();
                int rating = Integer.parseInt(parts[3].trim());
                String comment = parts[4].trim();
                String reviewDate = parts[5].trim();
                AbstractRoom room = findRoom(roomNo);
                // Create review with null student (student name resolved from ID at display time)
                reviews.add(new models.RoomReview(reviewId, comment, reviewDate, rating, null, room));
            } catch (Exception e) {
                System.err.println("Skipping invalid review line: " + line);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Reviewable implementation
    // -----------------------------------------------------------------------

    @Override
    public void addReview(RoomReview review) {
        if (review == null || !review.isValidRating()) {
            return;
        }
        reviews.add(review);
        save();
    }

    @Override
    public List<RoomReview> getReviewsForRoom(String roomNumber) {
        List<RoomReview> result = new ArrayList<>();
        for (RoomReview rv : reviews) {
            if (rv.getRoom() != null && rv.getRoom().getRoomNumber().equalsIgnoreCase(roomNumber)) {
                result.add(rv);
            }
        }
        return result;
    }

    @Override
    public double getAverageRating(String roomNumber) {
        List<RoomReview> roomReviews = getReviewsForRoom(roomNumber);
        if (roomReviews.isEmpty()) {
            return 0.0;
        }
        double total = 0;
        for (RoomReview rv : roomReviews) {
            total += rv.getRating();
        }
        return total / roomReviews.size();
    }

    // -----------------------------------------------------------------------
    // Room management methods
    // -----------------------------------------------------------------------

    /**
     * Adds a room if no duplicate roomNumber exists.
     *
     * @param room the room to add
     * @return true if added
     */
    public boolean addRoom(AbstractRoom room) {
        if (room == null || findRoom(room.getRoomNumber()) != null) {
            return false;
        }
        rooms.add(room);
        roomStore.add(room);
        save();
        return true;
    }

    /**
     * Deletes a room by room number.
     *
     * @param roomNumber room number
     * @return true if deleted
     */
    public boolean deleteRoom(String roomNumber) {
        AbstractRoom room = findRoom(roomNumber);
        if (room == null) {
            return false;
        }
        rooms.remove(room);
        // Rebuild roomStore
        roomStore.clear();
        for (AbstractRoom r : rooms) {
            roomStore.add(r);
        }
        save();
        return true;
    }

    /**
     * Alias for deleteRoom() for backwards compatibility.
     *
     * @param roomNumber room number
     * @return true if removed
     */
    public boolean removeRoom(String roomNumber) {
        return deleteRoom(roomNumber);
    }

    /**
     * Marks a room as available.
     *
     * @param roomNumber room number
     * @return true if found and updated
     */
    public boolean markAvailable(String roomNumber) {
        AbstractRoom room = findRoom(roomNumber);
        if (room == null) {
            return false;
        }
        room.setAvailable(true);
        save();
        return true;
    }

    /**
     * Finds a room by number.
     *
     * @param roomNumber room number
     * @return room instance or null
     */
    public AbstractRoom findRoom(String roomNumber) {
        if (roomNumber == null) {
            return null;
        }
        for (AbstractRoom room : rooms) {
            if (roomNumber.equalsIgnoreCase(room.getRoomNumber())) {
                return room;
            }
        }
        return null;
    }

    /**
     * Finds a room by number, returning Optional.
     *
     * @param roomNumber room number
     * @return Optional containing the room
     */
    public Optional<AbstractRoom> findRoomOptional(String roomNumber) {
        return Optional.ofNullable(findRoom(roomNumber));
    }

    /**
     * Returns available rooms only.
     *
     * @return list of available rooms
     */
    public List<AbstractRoom> getAvailableRooms() {
        List<AbstractRoom> available = new ArrayList<>();
        for (AbstractRoom room : rooms) {
            if (room.isAvailable()) {
                available.add(room);
            }
        }
        return available;
    }

    /**
     * Returns available rooms filtered by type.
     * "All" returns all available rooms.
     *
     * @param type room type or "All"
     * @return filtered available rooms
     */
    public List<AbstractRoom> getAvailableRoomsByType(String type) {
        if (type == null || "All".equalsIgnoreCase(type)) {
            return getAvailableRooms();
        }
        List<AbstractRoom> result = new ArrayList<>();
        for (AbstractRoom room : rooms) {
            if (room.isAvailable() && room.getRoomType().equalsIgnoreCase(type)) {
                result.add(room);
            }
        }
        return result;
    }

    /**
     * Returns rooms filtered by type (all availabilities).
     *
     * @param type room type
     * @return filtered list
     */
    public List<AbstractRoom> getRoomsByType(String type) {
        List<AbstractRoom> filtered = new ArrayList<>();
        if (type == null) {
            return filtered;
        }
        for (AbstractRoom room : rooms) {
            if (type.equalsIgnoreCase(room.getRoomType())) {
                filtered.add(room);
            }
        }
        return filtered;
    }

    /**
     * Returns all rooms.
     *
     * @return copy of room list
     */
    public List<AbstractRoom> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    /**
     * Returns room count.
     *
     * @return number of rooms
     */
    public int getRoomCount() {
        return rooms.size();
    }

    /**
     * Returns rooms sorted by price, using DataStore generic sort.
     * Demonstrates Generics (Lab 11) usage.
     *
     * @return sorted room list
     */
    public ArrayList<AbstractRoom> getRoomsSortedByPrice() {
        roomStore.sort(Comparator.comparingDouble(AbstractRoom::getPricePerMonth));
        return roomStore.getAll();
    }

    /**
     * Preloads 10 sample rooms if the rooms list is empty.
     */
    public void preloadSampleData() {
        if (!rooms.isEmpty()) {
            return;
        }
        rooms.add(new SingleRoom("A101", 1, 8000, true, "WiFi, Fan"));
        rooms.add(new SingleRoom("A102", 1, 8000, true, "WiFi, Fan"));
        rooms.add(new SingleRoom("A103", 1, 9000, true, "WiFi, AC"));
        rooms.add(new SingleRoom("A104", 1, 9000, true, "WiFi, AC"));
        rooms.add(new DoubleRoom("B101", 2, 12000, true, "WiFi, AC, TV"));
        rooms.add(new DoubleRoom("B102", 2, 12000, true, "WiFi, AC, TV"));
        rooms.add(new DoubleRoom("B103", 2, 13000, true, "WiFi, AC, TV, Fridge"));
        rooms.add(new DoubleRoom("B104", 2, 13000, false, "WiFi, AC, TV, Fridge"));
        rooms.add(new SuiteRoom("C101", 3, 20000, true, "WiFi, AC, TV, Kitchen, Fridge"));
        rooms.add(new SuiteRoom("C102", 3, 22000, true, "WiFi, AC, TV, Kitchen, Fridge, Study Desk"));
        // Sync roomStore
        roomStore.clear();
        for (AbstractRoom r : rooms) {
            roomStore.add(r);
        }
        save();
    }

    private AbstractRoom createRoomByType(String type, String roomNumber, int floor,
                                          double price, boolean available, String amenities) {
        if ("Single".equalsIgnoreCase(type)) {
            return new SingleRoom(roomNumber, floor, price, available, amenities);
        }
        if ("Double".equalsIgnoreCase(type)) {
            return new DoubleRoom(roomNumber, floor, price, available, amenities);
        }
        if ("Suite".equalsIgnoreCase(type)) {
            return new SuiteRoom(roomNumber, floor, price, available, amenities);
        }
        return null;
    }
}

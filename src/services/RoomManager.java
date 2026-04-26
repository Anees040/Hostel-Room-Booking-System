package services;

import interfaces.Saveable;
import java.util.ArrayList;
import java.util.List;
import models.AbstractRoom;
import models.DoubleRoom;
import models.SingleRoom;
import models.SuiteRoom;

/**
 * Manages room inventory and persistence.
 */
public class RoomManager implements Saveable {
    private final List<AbstractRoom> rooms = new ArrayList<>();
    private static final String FILE = "data/rooms.txt";

    /**
     * Creates the room manager and loads data.
     */
    public RoomManager() {
        load();
        if (rooms.isEmpty()) {
            preloadSampleRooms();
            save();
        }
    }

    /**
     * Adds a room if not already present.
     *
     * @param room room object
     */
    public void addRoom(AbstractRoom room) {
        if (room == null || findRoom(room.getRoomNumber()) != null) {
            return;
        }
        rooms.add(room);
    }

    /**
     * Removes a room by room number.
     *
     * @param roomNumber room number
     * @return true if removed
     */
    public boolean removeRoom(String roomNumber) {
        AbstractRoom room = findRoom(roomNumber);
        if (room != null) {
            rooms.remove(room);
            return true;
        }
        return false;
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
     * Returns all rooms.
     *
     * @return copy of room list
     */
    public List<AbstractRoom> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    /**
     * Filters rooms by type.
     *
     * @param type room type
     * @return filtered room list
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

    @Override
    public final void save() {
        List<String> lines = new ArrayList<>();
        for (AbstractRoom room : rooms) {
            String amenities = room.getAmenities() == null ? "" : room.getAmenities().replace("|", "/");
            String line = room.getRoomType() + "|"
                    + room.getRoomNumber() + "|"
                    + room.getFloor() + "|"
                    + room.getPricePerMonth() + "|"
                    + room.isAvailable() + "|"
                    + amenities;
            lines.add(line);
        }
        FileManager.writeToFile(FILE, lines);
    }

    @Override
    public final void load() {
        rooms.clear();
        List<String> lines = FileManager.readFromFile(FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 6) {
                continue;
            }

            try {
                String type = parts[0];
                String roomNumber = parts[1];
                int floor = Integer.parseInt(parts[2]);
                double price = Double.parseDouble(parts[3]);
                boolean available = Boolean.parseBoolean(parts[4]);
                String amenities = parts[5];

                AbstractRoom room = createRoomByType(type, roomNumber, floor, price, available, amenities);
                if (room != null) {
                    rooms.add(room);
                }
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid room line: " + line);
            }
        }
    }

    private void preloadSampleRooms() {
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

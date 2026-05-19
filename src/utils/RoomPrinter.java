package utils;

import java.util.ArrayList;
import models.AbstractRoom;
import models.DoubleRoom;
import models.SingleRoom;
import models.SuiteRoom;

/**
 * Demonstrates polymorphism (Lab 08) on AbstractRoom.
 * Shows runtime dispatch, instanceof, and upcasting.
 */
public class RoomPrinter {

    private RoomPrinter() {
        // Utility class — no instances needed
    }

    /**
     * Polymorphic method — accepts AbstractRoom and prints type-specific info.
     * Demonstrates runtime dispatch of getRoomType() and getMaxOccupancy().
     *
     * @param room any AbstractRoom subtype
     */
    public static void printRoomDetails(AbstractRoom room) {
        System.out.println("=== Room Details ===");
        System.out.println("Type: " + room.getRoomType());           // runtime dispatch
        System.out.println("Max Occupancy: " + room.getCapacity());  // runtime dispatch
        System.out.println(room.toString());                          // overridden toString
    }

    /**
     * Processes a heterogeneous list — demonstrates runtime polymorphism.
     *
     * @param rooms list of AbstractRoom objects
     */
    public static void printAllRooms(ArrayList<AbstractRoom> rooms) {
        System.out.println("Total rooms: " + rooms.size());
        for (AbstractRoom room : rooms) {
            printRoomDetails(room);
        }
    }

    /**
     * Demonstrates upcasting and instanceof for type-specific categorization.
     *
     * @param rooms list of AbstractRoom objects
     */
    public static void categorizeRooms(ArrayList<AbstractRoom> rooms) {
        for (AbstractRoom room : rooms) {
            if (room instanceof SuiteRoom) {
                System.out.println("SUITE: " + room.getRoomNumber() + " — premium room");
            } else if (room instanceof DoubleRoom) {
                System.out.println("DOUBLE: " + room.getRoomNumber() + " — shared room");
            } else if (room instanceof SingleRoom) {
                System.out.println("SINGLE: " + room.getRoomNumber() + " — private room");
            }
        }
    }
}

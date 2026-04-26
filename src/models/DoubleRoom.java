package models;

/**
 * Concrete room model for double occupancy.
 */
public class DoubleRoom extends AbstractRoom {

    /**
     * Constructs a double room.
     *
     * @param roomNumber room number
     * @param floor floor number
     * @param pricePerMonth monthly price
     * @param isAvailable availability
     * @param amenities amenities list
     */
    public DoubleRoom(String roomNumber, int floor, double pricePerMonth, boolean isAvailable, String amenities) {
        super(roomNumber, floor, pricePerMonth, isAvailable, amenities);
    }

    @Override
    public String getRoomType() {
        return "Double";
    }

    @Override
    public double getPrice() {
        return getPricePerMonth();
    }

    @Override
    public int getCapacity() {
        return 2;
    }
}

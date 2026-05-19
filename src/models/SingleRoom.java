package models;

/**
 * Concrete room model for single occupancy.
 */
public class SingleRoom extends AbstractRoom {

    /**
     * Constructs a single room.
     *
     * @param roomNumber    room number
     * @param floor         floor number
     * @param pricePerMonth monthly price
     * @param isAvailable   availability
     * @param amenities     amenities list
     */
    public SingleRoom(String roomNumber, int floor, double pricePerMonth, boolean isAvailable, String amenities) {
        super(roomNumber, floor, pricePerMonth, isAvailable, amenities);
    }

    @Override
    public String getRoomType() {
        return "Single";
    }

    @Override
    public double getPrice() {
        return getPricePerMonth();
    }

    @Override
    public int getMaxOccupancy() {
        return 1;
    }
}

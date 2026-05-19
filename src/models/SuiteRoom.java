package models;

/**
 * Concrete room model for suite occupancy.
 */
public class SuiteRoom extends AbstractRoom {

    /**
     * Constructs a suite room.
     *
     * @param roomNumber    room number
     * @param floor         floor number
     * @param pricePerMonth monthly price
     * @param isAvailable   availability
     * @param amenities     amenities list
     */
    public SuiteRoom(String roomNumber, int floor, double pricePerMonth, boolean isAvailable, String amenities) {
        super(roomNumber, floor, pricePerMonth, isAvailable, amenities);
    }

    @Override
    public String getRoomType() {
        return "Suite";
    }

    @Override
    public double getPrice() {
        return getPricePerMonth();
    }

    @Override
    public int getMaxOccupancy() {
        return 4;
    }
}

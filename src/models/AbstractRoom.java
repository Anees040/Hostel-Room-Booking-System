package models;

/**
 * Base abstraction for all room types in the hostel.
 */
public abstract class AbstractRoom {
    private String roomNumber;
    private int floor;
    private double pricePerMonth;
    private boolean isAvailable;
    private String amenities;

    /**
     * Constructs a room with common attributes.
     *
     * @param roomNumber room identifier
     * @param floor floor number
     * @param pricePerMonth monthly room price
     * @param isAvailable room availability flag
     * @param amenities comma separated amenities
     */
    public AbstractRoom(String roomNumber, int floor, double pricePerMonth, boolean isAvailable, String amenities) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.pricePerMonth = pricePerMonth;
        this.isAvailable = isAvailable;
        this.amenities = amenities;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    /**
     * Returns the room category name.
     *
     * @return type name
     */
    public abstract String getRoomType();

    /**
     * Returns effective room price.
     *
     * @return room price
     */
    public abstract double getPrice();

    /**
     * Returns maximum occupancy.
     *
     * @return room capacity
     */
    public abstract int getCapacity();

    @Override
    public String toString() {
        return roomNumber + " | " + getRoomType() + " | Rs." + getPrice()
                + " | " + (isAvailable ? "Available" : "Booked");
    }
}

package interfaces;

/**
 * Defines persistence operations for manager classes.
 */
public interface Saveable {

    /**
     * Saves in-memory data to storage.
     */
    void save();

    /**
     * Loads data from storage into memory.
     */
    void load();
}

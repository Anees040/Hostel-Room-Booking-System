package interfaces;

/**
 * Defines persistence operations for manager classes (Lab 12).
 * Provides default getDataDirectory() method.
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

    /**
     * Convenience alias for save(). Calls save() by default.
     */
    default void saveData() {
        save();
    }

    /**
     * Convenience alias for load(). Calls load() by default.
     */
    default void loadData() {
        load();
    }

    /**
     * Returns the default data directory path.
     *
     * @return data directory path
     */
    default String getDataDirectory() {
        return "data/";
    }
}

package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic data store demonstrating Generics (Lab 11).
 * Provides a type-safe container with common collection operations.
 *
 * @param <T> the type of items stored
 */
public class DataStore<T> {

    private final ArrayList<T> items = new ArrayList<>();

    /**
     * Adds an item to the store.
     *
     * @param item item to add
     */
    public void add(T item) {
        items.add(item);
    }

    /**
     * Removes an item by index.
     *
     * @param index zero-based index
     * @return true if removed, false if index out of bounds
     */
    public boolean remove(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        items.remove(index);
        return true;
    }

    /**
     * Gets an item by index.
     *
     * @param index zero-based index
     * @return the item at that index
     */
    public T get(int index) {
        return items.get(index);
    }

    /**
     * Finds the first item matching the predicate.
     *
     * @param predicate filter condition
     * @return Optional containing the match, or empty
     */
    public Optional<T> findFirst(Predicate<T> predicate) {
        for (T item : items) {
            if (predicate.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all items matching the predicate.
     *
     * @param predicate filter condition
     * @return filtered list
     */
    public ArrayList<T> filter(Predicate<T> predicate) {
        ArrayList<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Returns the number of items in the store.
     *
     * @return item count
     */
    public int size() {
        return items.size();
    }

    /**
     * Returns a copy of all items.
     *
     * @return new list with all items
     */
    public ArrayList<T> getAll() {
        return new ArrayList<>(items);
    }

    /**
     * Checks whether any item matches the predicate.
     *
     * @param predicate filter condition
     * @return true if any item matches
     */
    public boolean contains(Predicate<T> predicate) {
        return findFirst(predicate).isPresent();
    }

    /**
     * Clears all items from the store.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Sorts items using the provided comparator.
     *
     * @param comparator the sort order comparator
     */
    public void sort(Comparator<T> comparator) {
        Collections.sort(items, comparator);
    }

    /**
     * Prints all items using their toString() method.
     * Demonstrates polymorphism — each item's own toString is called.
     */
    public void printAll() {
        items.forEach(item -> System.out.println(item.toString()));
    }
}

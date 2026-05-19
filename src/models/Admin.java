package models;

/**
 * Admin entity for management operations.
 * Extends AbstractPerson (Lab 06 — Inheritance).
 */
public class Admin extends AbstractPerson {

    /**
     * Constructs an admin profile.
     *
     * @param personId      admin ID
     * @param name          admin name
     * @param contactNumber contact number
     * @param password      password
     */
    public Admin(String personId, String name, String contactNumber, String password) {
        super(personId, name, contactNumber, password);
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}

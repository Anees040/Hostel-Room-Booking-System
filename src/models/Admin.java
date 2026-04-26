package models;

/**
 * Admin entity for management operations.
 */
public class Admin extends AbstractPerson {
    private String designation;

    /**
     * Constructs an admin profile.
     *
     * @param name admin name
     * @param id admin id
     * @param contact contact details
     * @param password password
     * @param designation admin designation
     */
    public Admin(String name, String id, String contact, String password, String designation) {
        super(name, id, contact, password);
        this.designation = designation;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @Override
    public String getRole() {
        return "Admin";
    }
}

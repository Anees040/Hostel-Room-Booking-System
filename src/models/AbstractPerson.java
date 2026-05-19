package models;

/**
 * Base abstraction for all people using the hostel system.
 * Demonstrates abstract class (Lab 07) and encapsulation (Lab 03).
 */
public abstract class AbstractPerson {
    private String personId;
    private String name;
    private String contactNumber;
    private String password;

    /**
     * Constructs a person with common profile details.
     *
     * @param personId      unique identifier
     * @param name          full name
     * @param contactNumber contact number
     * @param password      account password
     */
    public AbstractPerson(String personId, String name, String contactNumber, String password) {
        this.personId = personId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.password = password;
    }

    public String getPersonId() { return personId; }

    /** Alias for getPersonId() for backwards compatibility with existing code. */
    public String getId() { return personId; }

    public void setPersonId(String personId) { this.personId = personId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }

    /** Alias for getContactNumber() for backwards compatibility. */
    public String getContact() { return contactNumber; }

    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /**
     * Returns the role of this person.
     *
     * @return role name string
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return "ID: " + personId + " | Name: " + name + " | Contact: " + contactNumber;
    }
}

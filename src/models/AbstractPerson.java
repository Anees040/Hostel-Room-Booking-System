package models;

/**
 * Base abstraction for all people using the hostel system.
 */
public abstract class AbstractPerson {
    private String name;
    private String id;
    private String contact;
    private String password;

    /**
     * Constructs a person with common profile details.
     *
     * @param name full name
     * @param id unique identifier
     * @param contact contact info
     * @param password account password
     */
    public AbstractPerson(String name, String id, String contact, String password) {
        this.name = name;
        this.id = id;
        this.contact = contact;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the role name.
     *
     * @return role name
     */
    public abstract String getRole();
}

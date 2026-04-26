package models;

/**
 * Student entity with academic profile details.
 */
public class Student extends AbstractPerson {
    private String department;
    private String program;
    private int semester;

    /**
     * Constructs a student profile.
     *
     * @param name student name
     * @param id student id
     * @param contact contact number or email
     * @param password password
     * @param department department name
     * @param program degree program
     * @param semester semester number
     */
    public Student(String name, String id, String contact, String password,
                   String department, String program, int semester) {
        super(name, id, contact, password);
        this.department = department;
        this.program = program;
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    @Override
    public String getRole() {
        return "Student";
    }
}

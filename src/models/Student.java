package models;

/**
 * Student entity with academic profile details.
 * Extends AbstractPerson (Lab 06 — Inheritance).
 */
public class Student extends AbstractPerson {
    private String department;
    private String program;
    private int semester;

    /**
     * Constructs a student profile.
     *
     * @param personId   student ID
     * @param name       full name
     * @param contactNumber contact number
     * @param password   password
     * @param department department name
     * @param program    degree program
     * @param semester   semester number
     */
    public Student(String personId, String name, String contactNumber, String password,
                   String department, String program, int semester) {
        super(personId, name, contactNumber, password);
        this.department = department;
        this.program = program;
        this.semester = semester;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Dept: " + department
                + " | Program: " + program
                + " | Semester: " + semester;
    }
}

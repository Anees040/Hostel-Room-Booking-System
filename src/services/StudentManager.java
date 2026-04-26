package services;

import exceptions.InvalidStudentException;
import interfaces.Saveable;
import java.util.ArrayList;
import java.util.List;
import models.Student;

/**
 * Manages student registration, lookup, and persistence.
 */
public class StudentManager implements Saveable {
    private final List<Student> students = new ArrayList<>();
    private static final String FILE = "data/students.txt";

    /**
     * Creates the student manager and loads data.
     */
    public StudentManager() {
        load();
        if (students.isEmpty()) {
            preloadSampleStudents();
            save();
        }
    }

    /**
     * Registers a student after validation.
     *
     * @param s student to register
     * @throws InvalidStudentException on invalid data
     */
    public void registerStudent(Student s) throws InvalidStudentException {
        if (s == null) {
            throw new InvalidStudentException("Student object cannot be null.");
        }
        if (isBlank(s.getId()) || isBlank(s.getName()) || isBlank(s.getPassword())) {
            throw new InvalidStudentException("Student ID, name, and password are required.");
        }
        if (studentExists(s.getId())) {
            throw new InvalidStudentException("Student with ID " + s.getId() + " already exists.");
        }

        students.add(s);
        save();
    }

    /**
     * Attempts student login.
     *
     * @param id student id
     * @param password student password
     * @return matching student or null
     */
    public Student login(String id, String password) {
        for (Student student : students) {
            if (student.getId().equalsIgnoreCase(id) && student.getPassword().equals(password)) {
                return student;
            }
        }
        return null;
    }

    /**
     * Finds student by id.
     *
     * @param id student id
     * @return student or null
     */
    public Student findStudent(String id) {
        if (isBlank(id)) {
            return null;
        }
        for (Student student : students) {
            if (student.getId().equalsIgnoreCase(id)) {
                return student;
            }
        }
        return null;
    }

    /**
     * Returns all students.
     *
     * @return copy of student list
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Checks if student id already exists.
     *
     * @param id student id
     * @return true if exists
     */
    public boolean studentExists(String id) {
        return findStudent(id) != null;
    }

    @Override
    public final void save() {
        List<String> lines = new ArrayList<>();
        for (Student student : students) {
            String line = student.getId().replace("|", "/") + "|"
                    + student.getName().replace("|", "/") + "|"
                    + nullSafe(student.getContact()) + "|"
                    + student.getPassword().replace("|", "/") + "|"
                    + nullSafe(student.getDepartment()) + "|"
                    + nullSafe(student.getProgram()) + "|"
                    + student.getSemester();
            lines.add(line);
        }
        FileManager.writeToFile(FILE, lines);
    }

    @Override
    public final void load() {
        students.clear();
        List<String> lines = FileManager.readFromFile(FILE);

        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                continue;
            }

            try {
                String id = parts[0];
                String name = parts[1];
                String contact = parts[2];
                String password = parts[3];
                String department = parts[4];
                String program = parts[5];
                int semester = Integer.parseInt(parts[6]);

                students.add(new Student(name, id, contact, password, department, program, semester));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid student line: " + line);
            }
        }
    }

    private void preloadSampleStudents() {
        students.add(new Student("Ali Raza", "FA24-001", "0300-1111111", "ali123", "AI", "BS AI", 3));
        students.add(new Student("Sara Khan", "FA24-002", "0300-2222222", "sara123", "CS", "BS CS", 5));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value.replace("|", "/");
    }
}

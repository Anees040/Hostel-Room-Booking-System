package services;

import exceptions.DuplicateStudentException;
import exceptions.InvalidStudentException;
import interfaces.Saveable;
import java.util.ArrayList;
import java.util.List;
import models.Student;
import utils.ValidationUtils;

/**
 * Manages student registration, lookup, and persistence.
 * Implements Saveable (Lab 12) interface.
 */
public class StudentManager implements Saveable {
    private final List<Student> students = new ArrayList<>();
    private static final String FILE = "data/students.txt";

    /**
     * Creates the student manager (data loaded separately via load()).
     */
    public StudentManager() {
        // Data is loaded externally via load()
    }

    /**
     * Registers a student after validation.
     *
     * @param s student to register
     * @throws InvalidStudentException   on invalid data
     * @throws DuplicateStudentException if student ID already exists
     */
    public void registerStudent(Student s) throws InvalidStudentException, DuplicateStudentException {
        if (s == null) {
            throw new InvalidStudentException("Student object cannot be null.");
        }
        if (!ValidationUtils.isNonEmpty(s.getId())) {
            throw new InvalidStudentException("Student ID is required.");
        }
        if (!ValidationUtils.isNonEmpty(s.getName())) {
            throw new InvalidStudentException("Student name is required.");
        }
        if (!ValidationUtils.isValidPassword(s.getPassword())) {
            throw new InvalidStudentException("Password must be at least 8 characters.");
        }
        if (!ValidationUtils.isValidSemester(s.getSemester())) {
            throw new InvalidStudentException("Semester must be between 1 and 8.");
        }
        if (studentExists(s.getId())) {
            throw new DuplicateStudentException("Student with ID " + s.getId() + " already exists.");
        }

        students.add(s);
        save();
    }

    /**
     * Attempts student login.
     *
     * @param id       student id
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
        if (id == null || id.trim().isEmpty()) {
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
     * Returns student count.
     *
     * @return number of students
     */
    public int getStudentCount() {
        return students.size();
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

    /**
     * Updates a student's password.
     *
     * @param studentId   student ID
     * @param newPassword new password
     * @throws InvalidStudentException if password is invalid or student not found
     */
    public void updateStudentPassword(String studentId, String newPassword) throws InvalidStudentException {
        if (!ValidationUtils.isValidPassword(newPassword)) {
            throw new InvalidStudentException("Password must be at least 8 characters.");
        }
        Student s = findStudent(studentId);
        if (s == null) {
            throw new InvalidStudentException("Student not found: " + studentId);
        }
        s.setPassword(newPassword);
        save();
    }

    /**
     * Preloads sample student data if list is empty.
     */
    public void preloadSampleData() {
        if (!students.isEmpty()) {
            return;
        }
        students.add(new Student("SP23-BSE-030", "Ali Raza", "03001111111", "ali12345",
                "CS", "BSE", 3));
        students.add(new Student("SP23-BSE-029", "Sara Khan", "03002222222", "sara1234",
                "CS", "BSE", 5));
        students.add(new Student("SP23-BSE-051", "Ahmed Hassan", "03003333333", "ahm12345",
                "CS", "BSE", 4));
        save();
    }

    @Override
    public void save() {
        List<String> lines = new ArrayList<>();
        for (Student student : students) {
            String line = sanitize(student.getId()) + "|"
                    + sanitize(student.getName()) + "|"
                    + sanitize(student.getContact()) + "|"
                    + sanitize(student.getPassword()) + "|"
                    + sanitize(student.getDepartment()) + "|"
                    + sanitize(student.getProgram()) + "|"
                    + student.getSemester();
            lines.add(line);
        }
        FileManager.writeToFile(FILE, lines);
    }

    @Override
    public void load() {
        students.clear();
        List<String> lines = FileManager.readFromFile(FILE);

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) {
                continue;
            }
            try {
                String id = parts[0].trim();
                String name = parts[1].trim();
                String contact = parts[2].trim();
                String password = parts[3].trim();
                String department = parts[4].trim();
                String program = parts[5].trim();
                int semester = Integer.parseInt(parts[6].trim());

                students.add(new Student(id, name, contact, password, department, program, semester));
            } catch (NumberFormatException e) {
                System.err.println("Skipping invalid student line: " + line);
            }
        }
    }

    private String sanitize(String value) {
        return value == null ? "" : value.replace("|", "/").trim();
    }
}

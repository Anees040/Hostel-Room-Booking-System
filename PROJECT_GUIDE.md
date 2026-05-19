# Hostel Room Booking System — Complete Project Guide
### CSC-241 Object-Oriented Programming | 3rd Semester Project

---

## 1. PROJECT OVERVIEW

This is a **Java Swing desktop application** implementing a fully functional Hostel Room Booking System. It covers all 13 OOP lab topics from CSC-241 and is designed as a comprehensive 3rd-semester OOP project.

**Team:** 3 Students | **Subject:** CSC-241 OOP | **Semester:** 3rd

**Tech Stack:** Java 17+, Java Swing (GUI), File I/O (persistence), No external libraries

---

## 2. HOW TO COMPILE AND RUN

```powershell
# Step 1 — Compile (from project root folder)
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Step 2 — Run
java -cp out Main
```

**Default Credentials:**
| Role    | ID              | Password  |
|---------|-----------------|-----------|
| Admin   | `admin`         | `admin123`|
| Student | `SP23-BSE-030`  | `ali12345` |
| Student | `SP23-BSE-029`  | `sara1234` |
| Student | `SP23-BSE-051`  | `ahm12345` |

---

## 3. COMPLETE FEATURE LIST & HOW TO TEST

### 3.1 Splash Screen
- **What:** Animated loading bar appears for 1.5 seconds before login
- **Test:** Run the app — a blue splash window with progress bar appears

### 3.2 Login System
- **What:** Role-based login (Admin / Student) with CardLayout
- **Test Admin:** Select "Admin" from dropdown → ID: `admin` → Password: `admin123` → Click Login
- **Test Student:** Select "Student" → ID: `SP23-BSE-030` → Password: `ali12345` → Click Login
- **Test Invalid:** Enter wrong password → Error dialog should appear
- **Show/Hide Password:** Check the "Show Password" checkbox

### 3.3 Student Registration
- **What:** New students can self-register from the login screen
- **Test:** Click "New Student? Register Here" → Fill all fields → Click Register
- **Validation Tests:**
  - Leave ID blank → error
  - Password < 8 chars → error
  - Passwords don't match → error
  - Same ID twice → "Duplicate student" error

### 3.4 Admin Dashboard — Room Management (Tab: Rooms)
- **What:** Full CRUD for rooms with type filter and average rating display
- **Test Add Room:** Click "Add Room" → Select type (Single/Double/Suite) → Fill Room No, Floor, Price → Click Add
- **Test Filter:** Select "Single" from dropdown → only single rooms shown
- **Test Delete:** Select a row → Click "Delete Room" → Confirm
- **Test Mark Available:** Select a booked room → Click "Mark Available"
- **Test View Details:** Select room → Click "View Details" → see reviews

### 3.5 Admin Dashboard — All Bookings (Tab: All Bookings)
- **What:** View and filter all bookings by status
- **Test:** Filter dropdown: All / Active / Cancelled
- **Test Details:** Double-click a booking row to see full details

### 3.6 Admin Dashboard — Student Management (Tab: Students)
- **What:** View all registered students with search
- **Test Search:** Type a name or ID in the search box → Click Search

### 3.7 Admin Dashboard — Maintenance Requests (Tab: Maintenance)
- **What:** View and update status of maintenance requests
- **Test:** Select a request → Click "Update Status" → Choose "In Progress" or "Resolved"
- **Filter:** Use dropdown to filter by Pending/In Progress/Resolved

### 3.8 Admin Dashboard — Room Reviews (Tab: Reviews)
- **What:** View student reviews and average rating per room
- **Test:** Select room number from dropdown → Click "Load Reviews"

### 3.9 Admin Dashboard — Notifications (Tab: Notifications)
- **What:** Admin receives notifications for all new bookings and maintenance requests
- **Test:** After a student books a room, check this tab → notification appears
- **Mark Read:** Click "Mark All Read"

### 3.10 Admin Dashboard — Reports (Tab: Reports)
- **Sub-tabs:**
  1. **Revenue Summary** — Shows active bookings count, total revenue, avg duration, most booked type
  2. **Room Occupancy Chart** — Java2D bar chart of bookings by type
  3. **Booking History** — Searchable table with CSV export
  4. **Maintenance Summary** — Pending/In Progress/Resolved counts

- **Test CSV Export:** Go to Booking History → Click "Export to CSV" → file saved to `data/report_export.csv`
- **Test Search:** Type in the search box → table filters live

### 3.11 Student Dashboard — Browse Rooms (Tab 1)
- **What:** Browse all available rooms with filter by type and max price
- **Test Filter:** Set Max Price to 12000 → Click Search → only affordable rooms shown
- **Test Booking:** Select room → Click "Book Selected Room" → BookingDialog opens

### 3.12 Booking Dialog
- **What:** Modal dialog for confirming a booking with live cost calculation
- **Test:** Enter Check-in: `2026-06-01`, Check-out: `2026-07-01` → Duration and cost auto-update
- **Validation Tests:**
  - Invalid date format → error
  - Check-out before check-in → error
  - Overlapping dates → error
- **Confirm:** Click "Confirm Booking" → success dialog with booking ID

### 3.13 Student Dashboard — My Bookings (Tab 2)
- **What:** View all personal bookings with status and total cost
- **Test Cancel:** Select active booking → Click "Cancel Selected Booking" → Confirm

### 3.14 Student Dashboard — Submit Maintenance (Tab 3)
- **What:** Submit a maintenance request for an actively booked room
- **Test:** Select room (only rooms with active bookings shown) → Enter description → Click Submit
- **Note:** If no active bookings, form is disabled with a message

### 3.15 Student Dashboard — Reviews (Tab 4)
- **What:** Leave a star rating (1–5 slider) and comment for previously booked rooms
- **Test:** Select room → Drag rating slider → Enter comment → Click "Submit Review"

### 3.16 Student Dashboard — Notifications (Tab 5)
- **What:** View personal notifications (booking confirmations, maintenance updates)
- **Test:** After booking a room, unread count badge appears in header
- **Mark Read:** Select notification → Click "Mark Selected Read"

### 3.17 Data Persistence
- **What:** All data is saved to `data/` folder as pipe-delimited `.txt` files
- **Files Created:**
  - `data/rooms.txt` — room inventory
  - `data/students.txt` — registered students
  - `data/bookings.txt` — booking records
  - `data/maintenance.txt` — maintenance requests
  - `data/reviews.txt` — room reviews
  - `data/notifications.txt` — notifications
- **Test:** Add a room → Close app → Reopen → Room is still there

---

## 4. OOP CONCEPTS — WHERE & HOW APPLIED

### Lab 01 — Introduction to Java
- **File:** `Main.java`
- All basic Java constructs: variables, data types, `System.out.println`, method calls
- Entry point `public static void main(String[] args)`

### Lab 02 & 03 — Classes, Objects, Encapsulation
- **Files:** `models/Student.java`, `models/AbstractRoom.java`, `models/Booking.java`
- **What:** All model fields are `private`. Access only through `get/set` methods.
- **Example (Student.java):**
```java
private String personId;       // private field
public String getId() { return personId; }   // getter
public void setName(String name) { this.name = name; }  // setter
```
- **Viva Answer:** "Encapsulation hides internal state and exposes only what is needed through public methods, preventing direct field access."

### Lab 04 — Constructors & Method Overloading
- **Files:** `BookingManager.java` — two constructors (with and without NotificationManager)
- **File:** `models/AbstractPerson.java` — parameterized constructor
```java
// Overloaded constructors in BookingManager
public BookingManager(RoomManager r, StudentManager s, NotificationManager n) { ... }
public BookingManager(RoomManager r, StudentManager s) { this(r, s, new NotificationManager()); }
```
- **Viva Answer:** "Constructor overloading allows creating objects in multiple ways. `this()` delegates to another constructor."

### Lab 05 — Composition (Has-A Relationship)
- **File:** `models/Booking.java`
- **What:** Booking has-a Student and has-a AbstractRoom (not inherits)
```java
public class Booking {
    private Student student;       // Composition
    private AbstractRoom room;     // Composition
}
```
- **File:** `models/MaintenanceRequest.java` — has-a Student, has-a AbstractRoom
- **Viva Answer:** "Composition is a strong has-a relationship. Booking cannot exist without a Student and Room."

### Lab 06 — Inheritance (Is-A Relationship)
- **Files:** `models/SingleRoom.java`, `DoubleRoom.java`, `SuiteRoom.java` → extend `AbstractRoom`
- **Files:** `models/Student.java`, `models/Admin.java` → extend `AbstractPerson`
```java
public class Student extends AbstractPerson { ... }
public class SingleRoom extends AbstractRoom { ... }
```
- **Viva Answer:** "Inheritance promotes code reuse. SingleRoom inherits floor, price, availability from AbstractRoom and only overrides type-specific methods."

### Lab 07 — Abstract Classes
- **Files:** `models/AbstractRoom.java`, `models/AbstractPerson.java`
- **What:** Cannot be instantiated directly. Force subclasses to implement specific methods.
```java
public abstract class AbstractRoom {
    public abstract String getRoomType();    // must be overridden
    public abstract double getPrice();       // must be overridden
    public abstract int getMaxOccupancy();   // must be overridden
}
public abstract class AbstractPerson {
    public abstract String getRole();        // must be overridden
}
```
- **Viva Answer:** "Abstract classes define a template. You can't do `new AbstractRoom()` — you must use SingleRoom, DoubleRoom, or SuiteRoom."

### Lab 08 — Polymorphism
- **Files:** `utils/RoomPrinter.java`, `utils/PersonPrinter.java`
- **What:** One method works on any room/person subtype. Runtime decides which version runs.
```java
// RoomPrinter — takes AbstractRoom but actual object could be Single/Double/Suite
public static void printRoomDetails(AbstractRoom room) {
    System.out.println(room.getRoomType());    // dispatched at runtime
    System.out.println(room.getMaxOccupancy()); // dispatched at runtime
}
```
- **Console Output:** Run the app — first output is the polymorphism demo
- **instanceof demo in RoomPrinter.categorizeRooms():**
```java
if (room instanceof SuiteRoom) { ... }
else if (room instanceof DoubleRoom) { ... }
```
- **Viva Answer:** "Polymorphism means one interface, many implementations. The JVM looks up the actual type at runtime and calls the correct method."

### Lab 09 — Interfaces (Contractual Behavior)
- **Files:** `interfaces/Saveable.java`, `Bookable.java`, `Reviewable.java`, `Maintainable.java`, `Notifiable.java`
- **What:** Define what a class must do without saying how
```java
public interface Saveable {
    void save();
    void load();
    default String getDataDirectory() { return "data/"; }  // default method (Java 8+)
}
```
- **Implemented by:**
  - `RoomManager` → implements `Saveable`, `Reviewable`
  - `StudentManager` → implements `Saveable`
  - `BookingManager` → implements `Saveable`
  - `NotificationManager` → implements `Saveable`, `Notifiable`
  - `MaintenanceManager` → implements `Saveable`, `Maintainable`
- **Viva Answer:** "An interface is a contract. Any class implementing Saveable MUST provide save() and load(). This enforces consistency."

### Lab 10 — Multiple Interfaces & Default Methods
- **File:** `interfaces/Saveable.java` — has `default` methods `saveData()` and `getDataDirectory()`
- **File:** `RoomManager.java` — implements both `Saveable` AND `Reviewable` (multiple interfaces)
```java
public class RoomManager implements Saveable, Reviewable { ... }
public class NotificationManager implements Saveable, Notifiable { ... }
```
- **Viva Answer:** "Java doesn't allow multiple inheritance of classes, but a class can implement multiple interfaces. Default methods in interfaces provide a fallback implementation."

### Lab 11 — Generics
- **File:** `utils/DataStore.java`
- **What:** Type-safe container that works with ANY type
```java
public class DataStore<T> {
    private final ArrayList<T> items = new ArrayList<>();
    public void add(T item) { items.add(item); }
    public Optional<T> findFirst(Predicate<T> predicate) { ... }
    public ArrayList<T> filter(Predicate<T> predicate) { ... }
}
```
- **Used in:** `RoomManager.java`
```java
private final DataStore<AbstractRoom> roomStore = new DataStore<>();
roomStore.sort(Comparator.comparingDouble(AbstractRoom::getPricePerMonth));
```
- **Viva Answer:** "Generics provide compile-time type safety. DataStore<AbstractRoom> can only hold AbstractRoom objects. Without generics, you'd use Object and need casting everywhere."

### Lab 12 — File I/O (Data Persistence)
- **File:** `services/FileManager.java`
- **What:** Uses `BufferedReader`/`BufferedWriter` to read/write pipe-delimited text files
- **All managers implement save() and load():**
```java
// RoomManager.save() — writes to data/rooms.txt
lines.add(room.getRoomType() + "|" + room.getRoomNumber() + "|" + room.getFloor() + ...);
FileManager.writeToFile(ROOMS_FILE, lines);

// RoomManager.load() — reads from data/rooms.txt
String[] parts = line.split("\\|", -1);
```
- **Error handling:** Malformed lines are skipped with `System.err.println()`
- **CSV Export:** `ReportsPanel.exportToCSV()` uses `BufferedWriter` to export booking history
- **Viva Answer:** "We use BufferedReader/BufferedWriter for efficient I/O. Pipe (|) is used as delimiter because it rarely appears in user data. We handle `NumberFormatException` for corrupted lines."

### Lab 13 — GUI Layout Managers
- **Layouts used:**
  | Layout | Where Used |
  |--------|-----------|
  | `BorderLayout` | All panels (North/Center/South/East/West) |
  | `CardLayout` | LoginFrame (switches between Login and Register) |
  | `GridBagLayout` | Forms (Add Room dialog, Register form, Booking dialog) |
  | `GridLayout` | Button rows, stats rows in ReportsPanel |
  | `FlowLayout` | Filter bars, button groups |
  | `BoxLayout` | (StudentDashboard inner panels) |
- **Viva Answer:** "BorderLayout is for overall frame structure. GridBagLayout is for forms with labels+fields. CardLayout swaps between panels like pages."

### Lab 14 — Event-Driven Programming
- **ActionListener:** Every button uses `addActionListener(e -> { ... })` lambda
- **DocumentListener (live feedback):**
```java
// BookingDialog.java — updates duration and cost as user types dates
checkInField.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { updateDuration(); }
    public void removeUpdate(DocumentEvent e) { updateDuration(); }
    public void changedUpdate(DocumentEvent e) { updateDuration(); }
});
```
- **ItemListener:** LoginFrame roleCombo hides register link for Admin
- **MouseListener:** AdminDashboard bookings table — double-click shows details
- **WindowListener:** Both dashboards confirm before closing
- **Swing Timer:** SplashScreen uses `Timer` to animate progress bar
- **Viva Answer:** "Event-driven means code runs in response to events (button clicks, key presses, window events). Swing uses the Event Dispatch Thread (EDT) — we use `SwingUtilities.invokeLater()` to ensure thread safety."

---

## 5. EXCEPTION HANDLING — COMPLETE LIST

| Exception Class | Type | Where Thrown | Where Caught |
|---|---|---|---|
| `InvalidBookingException` | Checked | `BookingManager.createBooking()` | `BookingDialog`, `BookingPanel` |
| `RoomNotAvailableException` | Checked | `BookingManager.createBooking()` | `BookingDialog`, `BookingPanel` |
| `DuplicateStudentException` | Checked | `StudentManager.registerStudent()` | `LoginFrame` |
| `InvalidStudentException` | Checked | `StudentManager.registerStudent()` | `LoginFrame` |
| `MaintenanceException` | Checked | `MaintenanceManager.updateRequestStatus()` | `AdminDashboard` |
| `UnauthorizedAccessException` | Unchecked | Can be thrown for security violations | Anywhere |
| `NumberFormatException` | Unchecked | File loading (malformed data) | All `load()` methods |

**Viva Answer:** "Checked exceptions must be handled at compile time (try-catch or throws). Unchecked extend RuntimeException. We chose checked exceptions for business logic errors so callers are forced to handle them."

---

## 6. PROJECT STRUCTURE

```
OOP_ Project/
├── src/
│   ├── Main.java                          ← Entry point
│   ├── models/
│   │   ├── AbstractPerson.java            ← Abstract class (Lab 07)
│   │   ├── AbstractRoom.java              ← Abstract class (Lab 07)
│   │   ├── Student.java                   ← Inheritance (Lab 06)
│   │   ├── Admin.java                     ← Inheritance (Lab 06)
│   │   ├── SingleRoom.java                ← Inheritance + Polymorphism
│   │   ├── DoubleRoom.java                ← Inheritance + Polymorphism
│   │   ├── SuiteRoom.java                 ← Inheritance + Polymorphism
│   │   ├── Booking.java                   ← Composition (Lab 05)
│   │   ├── MaintenanceRequest.java        ← Composition (Lab 05)
│   │   ├── RoomReview.java                ← Composition (Lab 05)
│   │   └── Notification.java             ← Entity
│   ├── interfaces/
│   │   ├── Saveable.java                  ← Interface + default method (Lab 09/10)
│   │   ├── Bookable.java                  ← Interface (Lab 09)
│   │   ├── Reviewable.java                ← Interface (Lab 09)
│   │   ├── Maintainable.java              ← Interface (Lab 09)
│   │   └── Notifiable.java               ← Interface (Lab 09)
│   ├── exceptions/
│   │   ├── InvalidBookingException.java   ← Checked
│   │   ├── RoomNotAvailableException.java ← Checked
│   │   ├── DuplicateStudentException.java ← Checked
│   │   ├── InvalidStudentException.java   ← Checked
│   │   ├── MaintenanceException.java      ← Checked
│   │   └── UnauthorizedAccessException.java ← Unchecked
│   ├── services/
│   │   ├── FileManager.java               ← File I/O utility (Lab 12)
│   │   ├── RoomManager.java               ← Implements Saveable, Reviewable
│   │   ├── StudentManager.java            ← Implements Saveable
│   │   ├── BookingManager.java            ← Implements Saveable
│   │   ├── MaintenanceManager.java        ← Implements Saveable, Maintainable
│   │   └── NotificationManager.java      ← Implements Saveable, Notifiable
│   ├── utils/
│   │   ├── DataStore.java                 ← Generics (Lab 11)
│   │   ├── DateUtils.java                 ← Date utility
│   │   ├── IdGenerator.java               ← ID formatting utility
│   │   ├── ValidationUtils.java           ← Input validation
│   │   ├── RoomPrinter.java               ← Polymorphism demo (Lab 08)
│   │   └── PersonPrinter.java            ← Polymorphism demo (Lab 08)
│   └── gui/
│       ├── UITheme.java                   ← Theme constants (Lab 13)
│       ├── AlternatingRowRenderer.java    ← Custom renderer (Lab 14)
│       ├── SplashScreen.java              ← Java2D animation (Lab 13/14)
│       ├── LoginFrame.java                ← CardLayout login (Lab 13/14)
│       ├── AdminDashboard.java            ← 7-tab admin UI (Lab 13/14)
│       ├── StudentDashboard.java          ← 5-tab student UI (Lab 13/14)
│       ├── RoomPanel.java                 ← Room management (Lab 13/14)
│       ├── BookingPanel.java              ← Legacy booking panel
│       ├── BookingDialog.java             ← Modal booking dialog (Lab 14)
│       └── ReportsPanel.java             ← Charts + CSV export (Lab 12/13/14)
├── data/                                  ← Auto-created at runtime
│   ├── rooms.txt
│   ├── students.txt
│   ├── bookings.txt
│   ├── maintenance.txt
│   ├── reviews.txt
│   └── notifications.txt
└── out/                                   ← Compiled .class files
```

---

## 7. EXPECTED VIVA QUESTIONS & ANSWERS

**Q: What is the difference between abstract class and interface?**
> Abstract class can have constructors, instance fields, and concrete methods. Interface (before Java 8) could only have abstract methods. A class can extend only ONE abstract class but implement MULTIPLE interfaces. We use AbstractRoom as abstract class because rooms share common fields (roomNumber, floor, price). We use Saveable as interface because any unrelated class (RoomManager, StudentManager) can implement it.

**Q: Why did you use Composition in Booking?**
> Booking has-a Student and has-a Room. We used composition (not inheritance) because a Booking IS NOT a Student — it just references one. If we deleted a Booking, the Student still exists. Composition models real-world "uses/has" relationships.

**Q: What is the benefit of DataStore<T>?**
> Without generics, we'd store objects as `Object` type and need casting everywhere, risking `ClassCastException` at runtime. With `DataStore<AbstractRoom>`, the compiler enforces type safety at compile time. We also get reusability — the same DataStore works for any type.

**Q: What is the Event Dispatch Thread (EDT)?**
> Swing is single-threaded. All GUI updates must happen on the EDT. We use `SwingUtilities.invokeLater()` in Main.java to ensure the LoginFrame is created on the EDT, preventing race conditions and rendering glitches.

**Q: How does polymorphism work in your project?**
> In RoomPrinter.printRoomDetails(AbstractRoom room), the parameter type is AbstractRoom, but we pass SingleRoom, DoubleRoom, or SuiteRoom objects. Java's JVM uses dynamic dispatch — it looks at the actual object type at runtime and calls the correct `getRoomType()` and `getMaxOccupancy()` method. This is demonstrated in the console output when the app starts.

**Q: What is the purpose of the Saveable interface's default method?**
> `getDataDirectory()` returns `"data/"` as a default. Any class implementing Saveable gets this for free without overriding. This is Java 8's default interface method feature, avoiding code duplication.

**Q: How do you prevent data corruption in file loading?**
> Each `load()` method wraps parsing in try-catch blocks. If a line has wrong field count or non-numeric values (NumberFormatException), we print an error and skip that line. The rest of the data still loads correctly.

**Q: What design pattern does your Manager architecture follow?**
> It follows the **Service Layer pattern** — each manager encapsulates all business logic for one entity (rooms, students, bookings). The GUI never directly modifies data; it always goes through the manager. This is also similar to the **Repository pattern** for data access.

**Q: Explain the CardLayout in LoginFrame.**
> CardLayout lets multiple panels occupy the same space, like a deck of cards — only one visible at a time. We have "login" card and "register" card. Clicking "Register Here" calls `cardLayout.show(cardPanel, "register")` to flip to the registration form.

**Q: What is the difference between checked and unchecked exceptions?**
> Checked exceptions extend `Exception` and MUST be handled (try-catch or `throws`). Examples: `InvalidBookingException`, `MaintenanceException`. Unchecked exceptions extend `RuntimeException` and don't require explicit handling. Example: `UnauthorizedAccessException`. We use checked for expected business errors and unchecked for programming errors.

---

## 8. DATA FLOW DIAGRAM

```
User Action (GUI)
      │
      ▼
Manager Layer (business logic + validation)
      │
      ├─→ throws Exception → caught by GUI → shown as JOptionPane
      │
      ├─→ calls NotificationManager.sendNotification()
      │
      └─→ calls FileManager.writeToFile() → data/*.txt
              │
              └─→ FileManager.readFromFile() on next load()
```

---

## 9. JUSTIFICATION AS A 3RD SEMESTER OOP PROJECT

This project fulfills ALL requirements of a 3rd semester OOP project:

| Criterion | Evidence |
|---|---|
| **13+ Lab Topics Covered** | Labs 01–14 all implemented (see Section 4) |
| **Abstract Classes** | `AbstractRoom`, `AbstractPerson` |
| **Interfaces** | 5 interfaces: Saveable, Bookable, Reviewable, Maintainable, Notifiable |
| **Inheritance** | Student/Admin extend AbstractPerson; 3 room types extend AbstractRoom |
| **Polymorphism** | RoomPrinter, PersonPrinter; runtime dispatch demonstrated |
| **Encapsulation** | All fields private in model classes |
| **Composition** | Booking has-a Student has-a Room; MaintenanceRequest same |
| **Exception Handling** | 6 custom exceptions; try-catch throughout |
| **Generics** | `DataStore<T>` generic container |
| **File I/O** | All data persisted in `data/` folder with BufferedReader/Writer |
| **GUI (Swing)** | 10+ GUI classes; Login, Admin (7 tabs), Student (5 tabs) |
| **Layout Managers** | BorderLayout, CardLayout, GridBagLayout, GridLayout, FlowLayout |
| **Event-Driven** | ActionListener, DocumentListener, ItemListener, MouseListener, WindowListener |
| **Multi-user** | Admin and Student roles with different dashboards |
| **Real-world Problem** | Hostel booking — relatable, practical domain |

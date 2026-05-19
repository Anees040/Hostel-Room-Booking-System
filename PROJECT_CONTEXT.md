# Project Context - Hostel Room Booking System

## Purpose and Scope
A Java Swing desktop app for managing hostel rooms, student records, and room bookings with file-based persistence. The system provides admin and student workflows and stores data in plain text files under the data/ directory.

## Current Progress (Implemented)
- Core architecture in place: GUI layer, services layer, models layer, interfaces, and custom exceptions.
- Role-based authentication (admin and student).
- Student registration and login.
- Room inventory management (add/delete/mark available).
- Booking lifecycle (create, view, cancel) with status tracking.
- File-based persistence for rooms, students, and bookings.
- Sample data auto-preload for rooms and students when files are empty.

## Functionalities
### Admin
- Login with hardcoded credentials (admin/admin123).
- Manage rooms:
  - View all rooms
  - Add room (Single/Double/Suite)
  - Delete room
  - Mark room available
- View all bookings (id, student, room, dates, status).
- View all registered students.

### Student
- Register new student account.
- Login as student.
- Browse available rooms with type filter (All/Single/Double/Suite).
- Book a selected room with check-in/check-out dates.
- View personal bookings.
- Cancel active bookings.

### Booking Rules and Behavior
- Booking status values: Active, Cancelled.
- Booking creation sets the room to unavailable.
- Cancellation sets the room to available again.
- Booking ID format: BK### (counter derived from saved data).

## Data Persistence
All data is stored in text files with pipe-delimited fields. Pipes in values are replaced with '/' during save.

### rooms.txt format
Type|RoomNumber|Floor|PricePerMonth|IsAvailable|Amenities

### students.txt format
StudentId|Name|Contact|Password|Department|Program|Semester

### bookings.txt format
BookingId|StudentId|RoomNumber|BookingDate|CheckIn|CheckOut|Status

## Current Data Snapshot
### data/rooms.txt
- Single|A101|1|8000.0|false|WiFi, Fan
- Single|A102|1|8000.0|true|WiFi, Fan
- Single|A103|1|9000.0|true|WiFi, AC
- Single|A104|1|9000.0|false|WiFi, AC
- Double|B101|2|12000.0|true|WiFi, AC, TV
- Double|B102|2|12000.0|true|WiFi, AC, TV
- Double|B103|2|13000.0|true|WiFi, AC, TV, Fridge
- Double|B104|2|13000.0|false|WiFi, AC, TV, Fridge
- Suite|C101|3|20000.0|true|WiFi, AC, TV, Kitchen, Fridge
- Suite|C102|3|22000.0|true|WiFi, AC, TV, Kitchen, Fridge, Study Desk

### data/students.txt
- SP23-BSE-030|Muhammad Anees|03105632437|Anees123|CS|BS SE|7
- SP23-BSE-029|Ahsan Shaid|03104978294|Ahsan123|CS|BS SE|7
- SP23-BSE-051|Umais Ahmar|03001234567|Umain|CS|BS SE|7

### data/bookings.txt
- BK001|SP23-BSE-030|A104|2026-04-26|2026-04-26|2026-05-26|Active

## Architecture and Responsibilities
### Entry Point
- Main initializes data directory, creates managers, prints counts, and opens LoginFrame.

### GUI Layer
- LoginFrame: login and registration dialog for students; admin login.
- AdminDashboard: room management, bookings view, students view.
- StudentDashboard: booking panel, my bookings, cancel bookings.
- BookingPanel: room search/filter and booking dialog.
- RoomPanel: admin CRUD-like room actions.

### Services Layer
- RoomManager: room inventory, filtering, persistence, sample data preload.
- StudentManager: student registration, login, persistence, sample data preload.
- BookingManager: booking creation/cancellation, status, persistence, ID generation.
- FileManager: read/write text files and ensure directories exist.

### Models Layer
- AbstractPerson: base class for people (name/id/contact/password).
- Student, Admin: concrete roles inheriting AbstractPerson.
- AbstractRoom: base class for rooms (number/floor/price/availability/amenities).
- SingleRoom, DoubleRoom, SuiteRoom: concrete room types.
- Booking: composite of Student and AbstractRoom plus dates and status.

### Interfaces
- Saveable: load/save contract implemented by RoomManager, StudentManager, BookingManager.
- Bookable: booking operations implemented by BookingManager.

### Exceptions
- InvalidStudentException: invalid registration data.
- RoomNotAvailableException: booking attempts for unavailable rooms.

## OOP Techniques Used
- Abstraction: AbstractPerson and AbstractRoom define shared contracts.
- Inheritance: Student/Admin extend AbstractPerson; room types extend AbstractRoom.
- Polymorphism: collections and methods operate on AbstractRoom and AbstractPerson references.
- Encapsulation: private fields with getters/setters in models.
- Interface-based design: Saveable and Bookable for behavior contracts.
- Composition: Booking contains Student and AbstractRoom; managers hold collections of models.
- Custom exceptions: domain-specific validation and availability errors.

## Application Flow
1. Main ensures data directory and initializes RoomManager, StudentManager, BookingManager.
2. Managers load data from text files; preload sample rooms/students if empty.
3. LoginFrame handles admin or student login.
4. Admin workflow: manage rooms, view bookings, view students.
5. Student workflow: browse rooms, create booking, view and cancel bookings.
6. Managers save changes to data files after each mutation.

## Known Limitations / Gaps
- Admin credentials are hardcoded; student passwords stored in plain text.
- No date validation or overlap checks beyond room availability.
- No search or filtering for bookings/students beyond basic views.
- No unit tests; no database backend.

## Build and Run (PowerShell)
- javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
- java -cp out Main

## Suggested Next Steps
- Add date validation and conflict checks for bookings.
- Add stronger authentication (hashing, roles stored in data).
- Add search/filtering in admin and student tables.
- Add automated tests for service classes.
- Consider migration to a database backend.

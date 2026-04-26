# Hostel Room Booking System

A Java Swing desktop application for managing hostel rooms, student registrations, and room bookings. The system supports both admin and student workflows with local file-based persistence.

## Highlights

- Clean object-oriented design using abstraction, inheritance, interfaces, and custom exceptions.
- Role-based login for admin and students.
- Student self-registration and authentication.
- Room inventory management with multiple room types:
  - Single
  - Double
  - Suite
- Booking lifecycle management:
  - Create bookings
  - Cancel bookings
  - Track booking status
- Persistent storage using plain text files in the `data/` directory.
- Auto-preload of sample rooms and students on first run.

## Project Structure

```text
Hostel-Room-Booking-System/
|-- data/
|   |-- bookings.txt
|   |-- rooms.txt
|   |-- students.txt
|-- src/
|   |-- Main.java
|   |-- exceptions/
|   |-- gui/
|   |-- interfaces/
|   |-- models/
|   |-- services/
|-- .gitignore
|-- README.md
```

## Tech Stack

- Language: Java
- UI Framework: Java Swing
- Data Storage: File-based text storage
- Architecture Style: Layered OOP (GUI, models, services, interfaces, exceptions)

## Core Modules

- `gui`: UI screens and panels for login, dashboards, room operations, and bookings.
- `models`: Domain models such as `Student`, `Booking`, and room abstractions.
- `services`: Business logic and persistence coordination (`RoomManager`, `StudentManager`, `BookingManager`, `FileManager`).
- `interfaces`: Contracts for reusable behaviors (`Bookable`, `Saveable`).
- `exceptions`: Domain-specific validation and availability exceptions.

## Default Access

- Admin login:
  - ID: `admin`
  - Password: `admin123`

Sample student records are auto-created on first launch when no student data exists.

## Getting Started

### Prerequisites

- Java JDK 8 or newer
- Any Java IDE (recommended: IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

### Run from an IDE

1. Open the project folder.
2. Ensure the `src/` folder is marked as the source root.
3. Run `Main.java`.

### Compile and Run from Terminal (PowerShell)

```powershell
# From project root
New-Item -ItemType Directory -Force out | Out-Null
$javaFiles = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $javaFiles
java -cp out Main
```

## Data Persistence

All runtime data is stored in:

- `data/rooms.txt`
- `data/students.txt`
- `data/bookings.txt`

The application automatically creates the `data/` directory if it does not exist.

## OOP Concepts Demonstrated

- Abstraction with `AbstractPerson` and `AbstractRoom`.
- Inheritance for room specializations and user roles.
- Polymorphism via room/model hierarchies.
- Interface-driven design through `Saveable` and `Bookable`.
- Encapsulation and validation in service layer methods.
- Custom exceptions for domain errors.

## Future Improvements

- Password hashing and stronger authentication rules.
- Search/filter UI improvements for rooms and bookings.
- Export reports (CSV/PDF).
- Unit tests for services and models.
- Migration from text files to a relational database.

## Author

Anees

## License

This project is for educational and academic use.

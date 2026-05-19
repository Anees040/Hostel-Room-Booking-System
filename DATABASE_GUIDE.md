# Database Integration Guide — Hostel Management System

## Option A: PostgreSQL (Local — Recommended for OOP Semester Project)
## Option B: Firebase Realtime Database (Cloud)

---

## PART 1 — POSTGRESQL INTEGRATION

### Step 1: Download the JDBC Driver

1. Go to: https://jdbc.postgresql.org/download/
2. Download `postgresql-42.7.x.jar`
3. Place the `.jar` file in your project: `OOP_ Project/lib/postgresql-42.7.x.jar`

### Step 2: Create the Database

Open **pgAdmin** or **psql** and run:

```sql
CREATE DATABASE hostel_db;

\c hostel_db

CREATE TABLE rooms (
    room_number   VARCHAR(20)  PRIMARY KEY,
    room_type     VARCHAR(20)  NOT NULL,
    floor         INTEGER      NOT NULL,
    price         DECIMAL      NOT NULL,
    is_available  BOOLEAN      DEFAULT TRUE,
    amenities     TEXT
);

CREATE TABLE students (
    student_id  VARCHAR(30) PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    contact     VARCHAR(15),
    password    VARCHAR(255) NOT NULL,
    department  VARCHAR(50),
    program     VARCHAR(30),
    semester    INTEGER
);

CREATE TABLE bookings (
    booking_id   VARCHAR(30) PRIMARY KEY,
    student_id   VARCHAR(30) REFERENCES students(student_id),
    room_number  VARCHAR(20) REFERENCES rooms(room_number),
    check_in     DATE        NOT NULL,
    check_out    DATE        NOT NULL,
    status       VARCHAR(20) DEFAULT 'Active'
);

CREATE TABLE maintenance_requests (
    request_id     VARCHAR(30) PRIMARY KEY,
    description    TEXT        NOT NULL,
    status         VARCHAR(30) DEFAULT 'Pending',
    requested_date DATE,
    resolved_date  DATE,
    student_id     VARCHAR(30) REFERENCES students(student_id),
    room_number    VARCHAR(20) REFERENCES rooms(room_number)
);

CREATE TABLE room_reviews (
    review_id   VARCHAR(30) PRIMARY KEY,
    room_number VARCHAR(20) REFERENCES rooms(room_number),
    student_id  VARCHAR(30) REFERENCES students(student_id),
    rating      INTEGER CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    review_date DATE
);

CREATE TABLE notifications (
    notification_id VARCHAR(30) PRIMARY KEY,
    user_id         VARCHAR(30),
    message         TEXT,
    date_created    DATE,
    is_read         BOOLEAN DEFAULT FALSE
);
```

### Step 3: Create DBConnection.java

Create file: `src/utils/DBConnection.java`

```java
package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager.
 * Replace credentials with your PostgreSQL setup.
 */
public class DBConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/hostel_db";
    private static final String USER     = "postgres";      // your postgres username
    private static final String PASSWORD = "your_password"; // your postgres password

    private static Connection instance;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("org.postgresql.Driver");
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✔ Database connected successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found. Add JAR to classpath.", e);
            }
        }
        return instance;
    }

    public static void close() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
```

### Step 4: Update RoomManager to use Database

Replace the current file-based `save()` and `load()` with SQL versions.
Create file: `src/services/RoomManagerDB.java`

```java
package services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.*;
import utils.DBConnection;

/**
 * RoomManager backed by PostgreSQL instead of text files.
 * Drop-in replacement for file-based RoomManager.
 */
public class RoomManagerDB {

    // ── Save (INSERT OR UPDATE) ──────────────────────────────────────────────

    public void saveRoom(AbstractRoom room) {
        String sql = """
            INSERT INTO rooms (room_number, room_type, floor, price, is_available, amenities)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (room_number) DO UPDATE
            SET price = EXCLUDED.price,
                is_available = EXCLUDED.is_available,
                amenities    = EXCLUDED.amenities
            """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setInt(3,    room.getFloor());
            ps.setDouble(4, room.getPricePerMonth());
            ps.setBoolean(5, room.isAvailable());
            ps.setString(6, room.getAmenities());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving room: " + e.getMessage());
        }
    }

    // ── Load All ─────────────────────────────────────────────────────────────

    public List<AbstractRoom> loadAllRooms() {
        List<AbstractRoom> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AbstractRoom room = createRoom(
                    rs.getString("room_type"),
                    rs.getString("room_number"),
                    rs.getInt("floor"),
                    rs.getDouble("price"),
                    rs.getBoolean("is_available"),
                    rs.getString("amenities")
                );
                if (room != null) rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        }
        return rooms;
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    public boolean deleteRoom(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
            return false;
        }
    }

    // ── Find by type ─────────────────────────────────────────────────────────

    public List<AbstractRoom> getAvailableByType(String type) {
        List<AbstractRoom> rooms = new ArrayList<>();
        String sql = "All".equalsIgnoreCase(type)
                ? "SELECT * FROM rooms WHERE is_available = TRUE"
                : "SELECT * FROM rooms WHERE is_available = TRUE AND room_type = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            if (!"All".equalsIgnoreCase(type)) ps.setString(1, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AbstractRoom r = createRoom(
                    rs.getString("room_type"), rs.getString("room_number"),
                    rs.getInt("floor"), rs.getDouble("price"),
                    rs.getBoolean("is_available"), rs.getString("amenities"));
                if (r != null) rooms.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    private AbstractRoom createRoom(String type, String no, int floor,
                                    double price, boolean avail, String amenities) {
        return switch (type.toLowerCase()) {
            case "single" -> new SingleRoom(no, floor, price, avail, amenities);
            case "double" -> new DoubleRoom(no, floor, price, avail, amenities);
            case "suite"  -> new SuiteRoom(no, floor, price, avail, amenities);
            default -> null;
        };
    }
}
```

### Step 5: Similar pattern for StudentManager

```java
// Save student
String sql = """
    INSERT INTO students (student_id, name, contact, password, department, program, semester)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    ON CONFLICT (student_id) DO UPDATE SET name = EXCLUDED.name
    """;
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, student.getId());
ps.setString(2, student.getName());
// ... etc
ps.executeUpdate();
```

### Step 6: Compile with JDBC JAR

```powershell
# Compile with PostgreSQL driver on classpath
javac -cp "lib\postgresql-42.7.x.jar" -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })

# Run with driver
java -cp "out;lib\postgresql-42.7.x.jar" Main
```

---

## PART 2 — FIREBASE REALTIME DATABASE

> If you prefer Firebase, provide your credentials (projectId, databaseURL, serviceAccount JSON).
> Firebase REST API can be called from Java without any Android SDK.

### Step 1: Dependencies needed

Add to project (download JARs):
- `okhttp-4.x.x.jar` — for HTTP requests
- `gson-2.x.jar` — for JSON parsing

Download from Maven Central or use build tool.

### Step 2: FirebaseClient.java

```java
package utils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Simple Firebase Realtime Database REST client.
 * No Firebase SDK needed — pure HTTP.
 */
public class FirebaseClient {

    // Replace with YOUR Firebase project URL
    private static final String BASE_URL = "https://YOUR_PROJECT_ID-default-rtdb.firebaseio.com";

    // Firebase auth token (optional — set rules to allow read/write for dev)
    private static final String AUTH_TOKEN = ""; // leave empty if rules are open

    /**
     * PUT data to a Firebase path (creates or overwrites).
     * @param path  e.g. "/rooms/A101"
     * @param json  JSON string of the object
     */
    public static boolean put(String path, String json) {
        try {
            URL url = new URL(BASE_URL + path + ".json"
                    + (AUTH_TOKEN.isEmpty() ? "" : "?auth=" + AUTH_TOKEN));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            System.err.println("Firebase PUT error: " + e.getMessage());
            return false;
        }
    }

    /**
     * GET data from Firebase path.
     * @param path  e.g. "/rooms.json"
     * @return raw JSON string
     */
    public static String get(String path) {
        try {
            URL url = new URL(BASE_URL + path + ".json"
                    + (AUTH_TOKEN.isEmpty() ? "" : "?auth=" + AUTH_TOKEN));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } catch (Exception e) {
            System.err.println("Firebase GET error: " + e.getMessage());
            return "null";
        }
    }

    /**
     * DELETE a node from Firebase.
     */
    public static boolean delete(String path) {
        try {
            URL url = new URL(BASE_URL + path + ".json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Step 3: Usage example for saving a room

```java
// Convert room to JSON manually (or use Gson library)
String json = String.format(
    "{\"roomType\":\"%s\",\"floor\":%d,\"price\":%.2f,\"available\":%b,\"amenities\":\"%s\"}",
    room.getRoomType(), room.getFloor(), room.getPricePerMonth(),
    room.isAvailable(), room.getAmenities()
);

// PUT to Firebase path /rooms/{roomNumber}
FirebaseClient.put("/rooms/" + room.getRoomNumber(), json);
```

### Step 4: Firebase Database Rules (for development)

In Firebase Console → Realtime Database → Rules:

```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

> ⚠️ Use open rules ONLY for testing. For production, add authentication rules.

---

## PART 3 — COMPARISON TABLE

| Feature | File I/O (Current) | PostgreSQL | Firebase |
|---|---|---|---|
| Setup complexity | ✅ None | 🟡 Medium | 🟡 Medium |
| Suitable for OOP lab | ✅ Best | ✅ Good | 🟡 OK |
| Multi-user support | ❌ No | ✅ Yes | ✅ Yes |
| Transactions | ❌ No | ✅ Full ACID | ❌ Partial |
| Cloud access | ❌ No | ❌ (local) | ✅ Yes |
| No external JAR needed | ✅ Yes | ❌ Need JDBC JAR | ❌ Need HTTP libs |
| SQL/Query power | ❌ No | ✅ Full SQL | ❌ Limited |
| Best for | This project | Production Java app | Mobile/web apps |

---

## PART 4 — RECOMMENDED APPROACH FOR YOUR VIVA

Since this is a **semester project**, the examiner only needs to see the system working.

### Option 1 (Easiest — Keep File I/O)
> Your current implementation already works. File-based persistence is perfectly valid for a 3rd semester OOP project. Just mention: *"We implemented file-based persistence using Java BufferedReader/Writer with pipe-delimited text files. In a production system, this would be replaced with a relational database like PostgreSQL."*

### Option 2 (Impressive — Add PostgreSQL)
1. Install PostgreSQL
2. Create `hostel_db` database with the SQL above
3. Download `postgresql-42.7.x.jar`
4. Create `DBConnection.java`
5. Create `RoomManagerDB.java` alongside existing `RoomManager.java`
6. In `Main.java`, swap: `RoomManager roomManager = new RoomManager()` with `RoomManagerDB`
7. Mention during viva: *"We implemented the Repository pattern — swapping file I/O for JDBC SQL with zero changes to GUI layer"*

### Option 3 (Cloud — Firebase)
Only choose this if you're comfortable with REST APIs and have Firebase credentials ready.

---

## PART 5 — QUICK POSTGRES TEST

```sql
-- Test data to insert
INSERT INTO rooms VALUES ('A101', 'Single', 1, 8000.00, true, 'WiFi, Fan');
INSERT INTO rooms VALUES ('B201', 'Double', 2, 12000.00, true, 'WiFi, AC, TV');
INSERT INTO rooms VALUES ('C301', 'Suite',  3, 20000.00, true, 'WiFi, AC, Kitchen');

INSERT INTO students VALUES ('SP23-BSE-030', 'Ali Hassan', '03001234567', 'ali12345', 'CS', 'BSE', 5);

-- Verify
SELECT * FROM rooms;
SELECT * FROM students;
```

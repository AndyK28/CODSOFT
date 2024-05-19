package registration.system.database_management;

import registration.system.course_management.Course;
import registration.system.course_management.schedule.Schedule;
import registration.system.student_management.Student;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {
    public static String URL = "jdbc:sqlite:registration.db";
    public static String SCHEMA_PATH = "src/main/resources/database.sql";
    public static BufferedReader reader;

    /**
     * Initializes the database schema reader with the path specified in SCHEMA_PATH.
     * This is done once when the class is loaded.
     */
    static {
        try {
            reader = new BufferedReader(new FileReader(SCHEMA_PATH));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Setter for the database URL, primarily for test purposes.
     * @param databaseUrl the new URL to set
     */
    public static void setURL(String databaseUrl) {
        URL = databaseUrl;
    }

    /**
     * Setter for the database schema path, primarily for test purposes.
     * @param schemaPath the new schema path to set
     */
    public static void setSchemaPath(String schemaPath) {
        SCHEMA_PATH = schemaPath;
    }

    /**
     * Establishes a connection to the database using the URL.
     * @return a Connection object representing the database connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Creates database tables using the provided connection and schema reader.
     * @param conn the connection to the database
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void createTables(Connection conn) throws SQLException, IOException {
        processSqlStatements(conn, reader);
    }

    /**
     * Processes SQL statements read from the provided reader and executes them using the connection.
     * @param conn the connection to the database
     * @param reader the reader for SQL statements
     * @throws IOException if an I/O error occurs
     * @throws SQLException if a database access error occurs
     */
    public static void processSqlStatements(Connection conn, BufferedReader reader) throws IOException, SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sqlBuilder.append(line);
            if (line.endsWith(";")) {
                try (PreparedStatement statement = conn.prepareStatement(sqlBuilder.toString())) {
                    statement.executeUpdate();
                }
                sqlBuilder.setLength(0);
            }
        }
    }

    /**
     * Creates a trigger in the database to insert course codes into another table after course insertion.
     * @throws SQLException if a database access error occurs
     */
    public static void createTrigger() throws SQLException {
        try(Connection conn = connect()) {
            String trigger = "CREATE TRIGGER IF NOT EXISTS insert_course_codes " +
                    "AFTER INSERT ON courses " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "INSERT INTO course_code (courseCode, title) " +
                    "VALUES (NEW.courseCode, NEW.title); " +
                    "END;";
            try(PreparedStatement stmt = conn.prepareStatement(trigger)) {
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Inserts a course into the database.
     * @param course the course to insert
     * @throws SQLException if a database access error occurs
     */
    public static void insertCourse(Course course) throws SQLException {
        try(Connection conn = connect()) {
            String sql = "INSERT INTO courses (courseCode, title, description, spacesLeft) VALUES (?, ?, ?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, course.getCourseCode());
                stmt.setString(2, course.getTitle());
                stmt.setString(3, course.getDescription());
                stmt.setInt(4, course.getSpacesLeft());
                stmt.executeUpdate();
            }
        }
        createTrigger();
    }

    /**
     * Inserts a schedule for a course into the database.
     * @param courseCode the code of the course
     * @param schedule the schedule to insert
     * @throws SQLException if a database access error occurs
     */
    public static void insertSchedule(String courseCode, Schedule schedule) throws SQLException {
        try (Connection conn = connect()) {
            String sql = "INSERT INTO schedule (courseCode, days, startTime, endTime) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseCode);
                stmt.setString(2, String.join(",", schedule.days()));
                stmt.setString(3, schedule.startTime());
                stmt.setString(4, schedule.endTime());
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Inserts sample data into the database for testing purposes.
     * @param conn the connection to the database
     * @throws SQLException if a database access error occurs
     */
    public static void insertSampleData(Connection conn) throws SQLException {
        String insertCourse = "INSERT INTO courses (courseCode, title, description, spacesLeft) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertCourse)) {
            stmt.setString(1, "CS101");
            stmt.setString(2, "Introduction to Computer Science");
            stmt.setString(3, "Basics of CS");
            stmt.setInt(4, 100);
            stmt.executeUpdate();
        }

        String insertSchedule = "INSERT INTO schedule (courseCode, days, startTime, endTime) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertSchedule)) {
            stmt.setString(1, "CS101");
            stmt.setString(2, "Monday");
            stmt.setString(3, "10:00");
            stmt.setString(4, "12:00");
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the capacity of a course in the database.
     * @param courseCode the code of the course
     * @param spacesLeft the updated number of spaces left
     * @throws SQLException if a database access error occurs
     */
    public static void updateCapacity(String courseCode, int spacesLeft) throws SQLException {
        try(Connection conn = connect()) {
            String sql = "UPDATE courses SET spacesLeft = ? WHERE courseCode = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, spacesLeft);
                stmt.setString(2, courseCode);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Retrieves a list of courses from a ResultSet obtained from the database.
     * @param conn the connection to the database
     * @param sql the SQL query to execute
     * @return a list of courses retrieved from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    public static List<Course> getCoursesFromResultSet(Connection conn, String sql) throws SQLException {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String courseCode = rs.getString("courseCode");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int spacesLeft = rs.getInt("spacesLeft");

                String days = rs.getString("days");
                String startTime = rs.getString("startTime");
                String endTime = rs.getString("endTime");

                Schedule schedule = new Schedule(Collections.singletonList(days), startTime, endTime);
                Course course = new Course(courseCode, title, description, spacesLeft, schedule);
                courses.add(course);
            }
        }
        return courses;
    }

    /**
     * Retrieves all courses from the database.
     * @return a list of all courses in the database
     * @throws SQLException if a database access error occurs
     */
    public static List<Course> getAllCourses() throws SQLException {
        try(Connection conn = connect()) {
            String sql = "SELECT c.courseCode, c.title, c.description, c.spacesLeft, s.days, s.startTime, s.endTime " +
                    "FROM courses c " +
                    "LEFT JOIN schedule s ON c.courseCode = s.courseCode";
            return getCoursesFromResultSet(conn, sql);
        }
    }

    /**
     * Retrieves available courses (courses with spaces left) from the database.
     * @return a list of available courses
     * @throws SQLException if a database access error occurs
     */
    public static List<Course> getAvailableCourses() throws SQLException {
        try(Connection conn = connect()) {
            String sql = "SELECT c.courseCode, c.title, c.description, c.spacesLeft, s.days, s.startTime, s.endTime " +
                    "FROM courses c " +
                    "JOIN schedule s ON c.courseCode = s.courseCode " +
                    "WHERE c.spacesLeft > 0";
            return getCoursesFromResultSet(conn, sql);
        }
    }

    /**
     * Saves a student to the database.
     * @param student the student to save
     * @throws SQLException if a database access error occurs
     */
    public static void saveStudent(Student student) throws SQLException {
        try(Connection conn = connect()) {
            String sql = "INSERT INTO students (studentID, name, surname) VALUES (?, ?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.studentId());
                stmt.setString(2, student.name());
                stmt.setString(3, student.surname());
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Retrieves a course from the database by its course code.
     * @param courseCode the course code
     * @return the course with the specified course code, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public static Course getCourseByCourseCode(String courseCode) throws SQLException {
        try (Connection conn = connect()) {
            String sql = "SELECT * FROM courses WHERE courseCode = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String title = rs.getString("title");
                        String description = rs.getString("description");
                        int spacesLeft = rs.getInt("spacesLeft");
                        return new Course(courseCode, title, description, spacesLeft);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves the schedule of a course from the database by its course code.
     * @param courseCode the course code
     * @return the schedule of the course, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public static Schedule getScheduleByCourseCode(String courseCode) throws SQLException {
        try (Connection conn = connect()) {
            String sql = "SELECT * FROM schedule WHERE courseCode = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, courseCode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String days = rs.getString("days");
                        String startTime = rs.getString("startTime");
                        String endTime = rs.getString("endTime");
                        return new Schedule(Collections.singletonList(days), startTime, endTime);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a student from the database by their ID.
     * @param studentId the ID of the student
     * @return the student with the specified ID, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public static Student getStudentById(String studentId) throws SQLException {
        try(Connection conn = connect()) {
            String sql = "SELECT * FROM students WHERE studentID = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Student(
                            rs.getString("studentID"),
                            rs.getString("name"),
                            rs.getString("surname")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Checks if a student ID is valid (exists in the database).
     * @param studentId the student ID to check
     * @return true if the student ID is valid, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public static boolean isValidStudentId(String studentId) throws  SQLException {
        try(Connection conn = connect()) {
            String sql  = "SELECT COUNT(*) AS count FROM students WHERE studentID = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count  = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    /**
     * Registers a student for a course in the database.
     * @param studentId the ID of the student
     * @param courseCode the course code
     * @throws SQLException if a database access error occurs
     */
    public static void registerStudentForCourse(String studentId, String courseCode) throws SQLException {
        try(Connection conn = connect()) {
            String sql = "INSERT INTO registrations (studentID, courseCode) VALUES (?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseCode);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Retrieves a list of courses registered by a student from the database.
     * @param student the student
     * @return a list of courses registered by the student
     * @throws SQLException if a database access error occurs
     */
    public static List<Course> getRegisteredCourses(Student student) throws SQLException {
        List<Course> registeredCourses = new ArrayList<>();
        String sql = "SELECT r.courseCode, c.title, s.days, s.startTime, s.endTime " +
                "FROM registrations r " +
                "INNER JOIN courses c ON r.courseCode = c.courseCode " +
                "INNER JOIN schedule s ON r.courseCode = s.courseCode " +
                "WHERE r.studentID = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.studentId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("courseCode");
                    String title = rs.getString("title");
                    String days = rs.getString("days");
                    String startTime = rs.getString("startTime");
                    String endTime = rs.getString("endTime");

                    Schedule schedule = new Schedule(Collections.singletonList(days), startTime, endTime);
                    Course course = new Course(courseCode, title, schedule);
                    registeredCourses.add(course);
                }
                return registeredCourses;
            }
        }
    }

    /**
     * Checks if a student is registered for a specific course.
     * @param student the student
     * @param courseCode the course code
     * @return true if the student is registered for the course, otherwise false
     * @throws SQLException if a database access error occurs
     */
    public static boolean isStudentRegisteredForCourse(Student student, String courseCode) throws SQLException {
        try (Connection conn = connect()) {
            String sql = "SELECT COUNT(*) AS count FROM registrations WHERE studentID = ? AND courseCode = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.studentId());
                stmt.setString(2, courseCode);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    /**
     * Deregisters a student from a course in the database.
     * @param studentId the ID of the student
     * @param courseCode the course code
     * @throws SQLException if a database access error occurs
     */
    public static void deregisterStudentFromCourse(String studentId, String courseCode) throws SQLException {
        try(Connection conn = connect()) {
            String sql = "DELETE FROM registrations WHERE studentID = ? AND courseCode = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseCode);
                stmt.executeUpdate();
            }
        }
    }
}

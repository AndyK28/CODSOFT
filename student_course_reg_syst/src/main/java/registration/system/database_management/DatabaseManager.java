package registration.system.database_management;

import registration.system.course_management.Course;
import registration.system.course_management.schedule.Schedule;
import registration.system.student_management.Student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {
    private static final String url = "jdbc:sqlite:student_registration.db";

    public static void connect() throws SQLException {
        Connection conn = DriverManager.getConnection(url);
        conn.close();
    }

    public static void createTables() throws SQLException, IOException {
        try(Connection conn = DriverManager.getConnection(url)) {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/database.sql"));
            processSqlStatements(conn, reader);
        }
    }

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
        createTrigger();
    }

    public static void createTrigger() throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
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

    public static void insertCourse(Course course) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO courses (courseCode, title, description, spacesLeft) VALUES (?, ?, ?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, course.getCourseCode());
                stmt.setString(2, course.getTitle());
                stmt.setString(3, course.getDescription());
                stmt.setInt(4, course.getSpacesLeft());
                stmt.executeUpdate();
            }
        }
    }

    public static void insertSchedule(String courseCode, Schedule schedule) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {
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

    public static void updateCapacity(String courseCode, int spacesLeft) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "UPDATE courses SET spacesLeft = ? WHERE courseCode = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, spacesLeft);
                stmt.setString(2, courseCode);
                stmt.executeUpdate();
            }
        }
    }

    public static List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT c.courseCode, c.title, c.description, c.spacesLeft, s.days, s.startTime, s.endTime " +
                    "FROM courses c " +
                    "LEFT JOIN schedule s ON c.courseCode = s.courseCode";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
        return courses;
    }

    public static List<Course> getAvailableCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT c.courseCode, c.title, c.description, c.spacesLeft, s.days, s.startTime, s.endTime " +
                    "FROM courses c " +
                    "JOIN schedule s ON c.courseCode = s.courseCode " +
                    "WHERE c.spacesLeft > 0";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        }
        return courses;
    }

    public static void saveStudent(Student student) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO students (studentID, name, surname) VALUES (?, ?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.studentId());
                stmt.setString(2, student.name());
                stmt.setString(3, student.surname());
                stmt.executeUpdate();
            }
        }
    }

    public static Course getCourseByCourseCode(String courseCode) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {
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

    public static Student getStudentById(String studentId) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
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

    public static boolean isValidStudentId(String studentId) throws  SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
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


    public static void registerStudentForCourse(String studentId, String courseCode) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "INSERT INTO registrations (studentID, courseCode) VALUES (?, ?)";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseCode);
                stmt.executeUpdate();
            }
        }
    }

    public static List<Course> getRegisteredCourses(Student student) throws SQLException {
        List<Course> registeredCourses = new ArrayList<>();
        String sql = "SELECT r.courseCode, c.title, s.days, s.startTime, s.endTime " +
                "FROM registrations r " +
                "INNER JOIN courses c ON r.courseCode = c.courseCode " +
                "INNER JOIN schedule s ON r.courseCode = s.courseCode " +
                "WHERE r.studentID = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, student.studentId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseCode = rs.getString("courseCode");
                    String title = rs.getString("title");

                    String days = rs.getString("days");
                    String startTime = rs.getString("startTime");
                    String endTIme = rs.getString("endTIme");

                    Schedule schedule = new Schedule(Collections.singletonList(days), startTime, endTIme);
                    Course course = new Course(courseCode, title, schedule);
                    registeredCourses.add(course);
                }
                return registeredCourses;
            }
        }
    }

    public static boolean isStudentRegisteredForCourse(Student student, String courseCode) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {
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

    public static void deregisterStudentFromCourse(String studentId, String courseCode) throws SQLException {
        try(Connection conn = DriverManager.getConnection(url)) {
            String sql = "DELETE FROM registrations WHERE studentID = ? AND courseCode = ?";
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, studentId);
                stmt.setString(2, courseCode);
                stmt.executeUpdate();
            }
        }
    }
}

package course_management_tests;

import org.junit.jupiter.api.*;
import registration.system.course_management.Course;
import registration.system.course_management.CourseManager;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.Student;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseManagerTest {
    @BeforeEach
    void setUp() throws SQLException, IOException {
        DatabaseManager.setURL("jdbc:sqlite:test.db");
        DatabaseManager.setSchemaPath("src/test/resources/database.sql");

        try (Connection conn = DatabaseManager.connect()) {
            DatabaseManager.createTables(conn);
            DatabaseManager.insertSampleData(conn);
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection conn = DatabaseManager.connect()) {
            conn.createStatement().execute("DELETE FROM courses");
            conn.createStatement().execute("DELETE FROM course_code");
            conn.createStatement().execute("DELETE FROM students");
            conn.createStatement().execute("DELETE FROM registrations");
            conn.createStatement().execute("DELETE FROM schedule");
        }
    }

    @Test
    void testCheckAvailableCourses() throws SQLException {
        CourseManager courseManager = new CourseManager();

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        courseManager.checkAvailableCourses();

        String expectedOutput = """
                Available courses:
                1. CS101 - Introduction to Computer Science
                """;
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testCheckRegisteredCourses() throws SQLException {
        Course course = DatabaseManager.getCourseByCourseCode("CS101");
        Student student = new Student("S001", "John", "Doe");
        DatabaseManager.saveStudent(student);

        assert course != null;
        CourseManager courseManager = new CourseManager();
        courseManager.registerForCourse(student, course);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        courseManager.checkRegisteredCourses(student);

        String expectedOutput = """
                Registered course(s):
                1. CS101 - Introduction to Computer Science
                """;
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testCheckSchedule() throws SQLException {
        Course course = DatabaseManager.getCourseByCourseCode("CS101");
        Student student = new Student("S001", "John", "Doe");
        DatabaseManager.saveStudent(student);

        assert course != null;
        CourseManager courseManager = new CourseManager();
        courseManager.registerForCourse(student, course);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        courseManager.checkSchedule(student);

        String expectedOutput = """
                Schedule for CS101:
                Days: [Monday]
                Start Time - 10:00
                End Time - 12:00
                """;
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    void testRegisterForCourse() throws SQLException {
        Course course = DatabaseManager.getCourseByCourseCode("CS101");
        Student student = new Student("S001", "John", "Doe");
        DatabaseManager.saveStudent(student);


        assert course != null;
        CourseManager courseManager = new CourseManager();

        courseManager.registerForCourse(student, course);
        assertTrue(DatabaseManager.isStudentRegisteredForCourse(student, course.getCourseCode()));

        List<Course> registeredCourses = DatabaseManager.getRegisteredCourses(student);
        assertTrue(registeredCourses.stream().anyMatch(c -> c.getCourseCode().equals("CS101")));
    }

    @Test
    void testDeregisterFromCourse() throws SQLException {
        Course course = DatabaseManager.getCourseByCourseCode("CS101");
        Student student = new Student("S001", "John", "Doe");
        DatabaseManager.saveStudent(student);

        assert course != null;
        CourseManager courseManager = new CourseManager();

        courseManager.registerForCourse(student, course);
        assertTrue(DatabaseManager.isStudentRegisteredForCourse(student, course.getCourseCode()));

        courseManager.deregisterFromCourse(student, course);
        List<Course> registeredCourses = DatabaseManager.getRegisteredCourses(student);
        assertTrue(registeredCourses.isEmpty());
    }
}

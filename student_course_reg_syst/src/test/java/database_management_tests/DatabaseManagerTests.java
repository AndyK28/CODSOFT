package database_management_tests;

import org.junit.jupiter.api.*;
import registration.system.course_management.Course;
import registration.system.course_management.schedule.Schedule;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.Student;

import java.io.*;
import java.sql.*;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTests {
    @BeforeAll
    static void setup() throws SQLException, IOException {
        DatabaseManager.setURL("jdbc:sqlite:test.db");
        DatabaseManager.setSchemaPath("src/test/resources/database.sql");
    }

    @BeforeEach
    void initializeDatabase() throws SQLException, IOException {
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
    void testInsertAndGetCourse() throws SQLException {
        Course retrievedCourse = DatabaseManager.getCourseByCourseCode("CS101");
        assertNotNull(retrievedCourse);
        assertEquals("CS101", retrievedCourse.getCourseCode());
        assertEquals("Introduction to Computer Science", retrievedCourse.getTitle());
        assertEquals("Basics of CS", retrievedCourse.getDescription());
        assertEquals(100, retrievedCourse.getSpacesLeft());
    }

    @Test
    void testInsertAndGetStudent() throws SQLException {
        Student student = new Student("S001", "John", "Doe");
        DatabaseManager.saveStudent(student);

        Student retrievedStudent = DatabaseManager.getStudentById("S001");
        assertNotNull(retrievedStudent);
        assertEquals("S001", retrievedStudent.studentId());
        assertEquals("John", retrievedStudent.name());
        assertEquals("Doe", retrievedStudent.surname());
    }

    @Test
    void testRegisterAndRetrieveCourses() throws SQLException {
        Student student = new Student("S002", "Jane", "Smith");
        DatabaseManager.saveStudent(student);

        DatabaseManager.registerStudentForCourse("S002", "CS101");

        List<Course> registeredCourses = DatabaseManager.getRegisteredCourses(student);
        assertNotNull(registeredCourses);
        assertEquals(1, registeredCourses.size());
        assertEquals("CS101", registeredCourses.get(0).getCourseCode());
        assertEquals("Introduction to Computer Science", registeredCourses.get(0).getTitle());
    }

    @Test
    void testUpdateCapacity() throws SQLException {
        DatabaseManager.updateCapacity("CS101", 15);
        Course updatedCourse = DatabaseManager.getCourseByCourseCode("CS101");
        assertNotNull(updatedCourse);
        assertEquals(15, updatedCourse.getSpacesLeft());
    }

    @Test
    void testDeregisterStudentFromCourse() throws SQLException {
        Student student = new Student("S003", "Alice", "Johnson");
        DatabaseManager.saveStudent(student);

        DatabaseManager.registerStudentForCourse("S003", "CS101");
        DatabaseManager.deregisterStudentFromCourse("S003", "CS101");

        List<Course> registeredCourses = DatabaseManager.getRegisteredCourses(student);
        assertTrue(registeredCourses.isEmpty());
    }

    @Test
    public void testGetCourseByCourseCode() throws SQLException {
        Course course = DatabaseManager.getCourseByCourseCode("CS101");
        assertNotNull(course);
        assertEquals("Introduction to Computer Science", course.getTitle());
    }

    @Test
    void testIsValidStudentId() throws SQLException {
        Student student = new Student("S004", "Robert", "Brown");
        DatabaseManager.saveStudent(student);

        boolean isValid = DatabaseManager.isValidStudentId("S004");
        assertTrue(isValid);

        boolean isInvalid = DatabaseManager.isValidStudentId("S999");
        assertFalse(isInvalid);
    }

    @Test
    void testInsertCourse() throws SQLException {
        Course newCourse = new Course("CS102", "Advanced Computer Science", "Advanced topics in CS", 50, null);
        DatabaseManager.insertCourse(newCourse);

        Course retrievedCourse = DatabaseManager.getCourseByCourseCode("CS102");
        assertNotNull(retrievedCourse);
        assertEquals("CS102", retrievedCourse.getCourseCode());
        assertEquals("Advanced Computer Science", retrievedCourse.getTitle());
        assertEquals("Advanced topics in CS", retrievedCourse.getDescription());
        assertEquals(50, retrievedCourse.getSpacesLeft());
    }

    @Test
    void testGetAllCourses() throws SQLException {
        List<Course> courses = DatabaseManager.getAllCourses();
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertEquals("CS101", courses.get(0).getCourseCode());
        assertEquals("Introduction to Computer Science", courses.get(0).getTitle());
    }

    @Test
    void testGetAvailableCourses() throws SQLException {
        List<Course> courses = DatabaseManager.getAvailableCourses();
        assertNotNull(courses);
        assertFalse(courses.isEmpty());
        assertEquals("CS101", courses.get(0).getCourseCode());
        assertEquals("Introduction to Computer Science", courses.get(0).getTitle());
    }

    @Test
    void testInsertSchedule() throws SQLException {
        Schedule schedule = DatabaseManager.getScheduleByCourseCode("CS101");

        assertNotNull(schedule);
        assertEquals("[Monday]", schedule.days().toString());
        assertEquals("10:00", schedule.startTime());
        assertEquals("12:00", schedule.endTime());
    }
}

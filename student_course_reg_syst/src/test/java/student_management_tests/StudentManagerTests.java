package student_management_tests;

import org.junit.jupiter.api.*;
import registration.system.course_management.Course;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.Student;
import registration.system.student_management.StudentManager;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentManagerTests {
    private InputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream errOutput;
    private String input;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        DatabaseManager.setURL("jdbc:sqlite:test.db");
        DatabaseManager.setSchemaPath("src/test/resources/database.sql");

        try (Connection conn = DatabaseManager.connect()) {
            DatabaseManager.createTables(conn);
            DatabaseManager.insertSampleData(conn);
        }

        outputStream = new ByteArrayOutputStream();
        errOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errOutput));
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

        System.setOut(System.out);
        System.setIn(System.in);
    }

    @Test
    void testProcessLogin_RegisteredStudent() throws SQLException {
        input = "y\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = spy(new StudentManager());
        doNothing().when(studentManager).processReturningUserLogin();

        studentManager.processLogin();

        verify(studentManager, times(1)).processReturningUserLogin();
    }

    @Test
    void testProcessLogin_NewStudent() throws SQLException {
        input = "n\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = spy(new StudentManager());
        doNothing().when(studentManager).processNewUserLogin();

        studentManager.processLogin();

        verify(studentManager, times(1)).processNewUserLogin();
    }

    @Test
    void testProcessLogin_InvalidResponse() throws SQLException {
        input = "invalid\nn\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = spy(new StudentManager());
        studentManager.setScanner(new Scanner(System.in));
        doNothing().when(studentManager).processNewUserLogin();

        studentManager.processLogin();

        verify(studentManager, times(1)).processNewUserLogin();
        assertTrue(errOutput.toString().contains("Invalid response, Please try again."));
    }

    @Test
    void testProcessReturningUserLogin() throws SQLException {
        Student student = new Student("SE00123", "John", "Doe");
        DatabaseManager.saveStudent(student);

        input = "SE00123\n6";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));
        studentManager.processReturningUserLogin();

        assertTrue(outputStream.toString().contains("Welcome back " + student.name()));
    }

    @Test
    void testProcessReturningInvalidUserLogin() throws SQLException {
        Student student = new Student("SE00123", "John", "Doe");
        DatabaseManager.saveStudent(student);

        input = "SE12345\nY\nSE00123\n6";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));
        studentManager.processReturningUserLogin();

        assertTrue(errOutput.toString().contains("Student not found! Please enter valid studentId i.e SE12345"));
    }

    @Test
    void testProcessNewUserLogin() throws SQLException {
        input = "John\nDoe\n6";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.processNewUserLogin();

        assertTrue(outputStream.toString().contains("Profile created successfully"));
    }

    @Test
    void testGenerateStudentId() throws SQLException {
        StudentManager studentManager = new StudentManager();
        String studentId = studentManager.generateStudentId();

        assertTrue(studentId.startsWith("SE"));
    }

    @Test
    void testPromptForCourse() throws SQLException {
        List<Course> courses = DatabaseManager.getAllCourses();

        input = "CS101";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        String message = "Enter course code to register: ";
        studentManager.setScanner(new Scanner(System.in));
        Course selectedCourse = studentManager.promptForCourse(courses, message);

        assertEquals(courses.get(0).getCourseCode(), selectedCourse.getCourseCode());
    }

    @Test
    void testUserChoice() throws SQLException {
        Student student = new Student("S001", "John", "Doe");
        DatabaseManager.saveStudent(student);

        input = "1\nn";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));
        studentManager.userChoice(student, "1");

        assertTrue(outputStream.toString().contains("Available courses:"));
    }

    @Test
    void testEnterName_ValidInput() {
        input = "John\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String name = studentManager.enterName();

        assertEquals("John", name);
        assertTrue(outputStream.toString().contains("Enter your name"));
    }

    @Test
    void testEnterName_InvalidInputThenValidInput() {
        input = "123\nJohn\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String name = studentManager.enterName();

        assertEquals("John", name);
        assertTrue(outputStream.toString().contains("Enter your name"));
        assertTrue(errOutput.toString().contains("Invalid input. Name should contain only letters. Please try again."));
    }

    @Test
    void testEnterSurname_ValidInput() {
        input = "Doe\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String surname = studentManager.enterSurname();

        assertEquals("Doe", surname);
        assertTrue(outputStream.toString().contains("Enter your surname"));
    }

    @Test
    void testEnterSurname_InvalidInputThenValidInput() {
        input = "123\nDoe\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String surname = studentManager.enterSurname();

        assertEquals("Doe", surname);
        assertTrue(outputStream.toString().contains("Enter your surname"));
        assertTrue(errOutput.toString().contains("Invalid input. Surname should contain only letters. Please try again."));
    }

    @Test
    void testGetYesOrNoInput_Yes() {
        input = "y\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String result = studentManager.getYesOrNoInput();

        assertEquals("y", result);
    }

    @Test
    void testGetYesOrNoInput_No() {
        input = "n\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String result = studentManager.getYesOrNoInput();

        assertEquals("n", result);
    }

    @Test
    void testGetYesOrNoInput_InvalidThenYes() {
        input = "invalid\ny\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String result = studentManager.getYesOrNoInput();

        assertEquals("y", result);
        assertTrue(errOutput.toString().contains("Invalid input. Please enter 'y' or 'n'."));
    }

    @Test
    void testGetYesOrNoInput_InvalidThenNo() {
        input = "invalid\nn\n";
        inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        StudentManager studentManager = new StudentManager();
        studentManager.setScanner(new Scanner(System.in));

        String result = studentManager.getYesOrNoInput();

        assertEquals("n", result);
        assertTrue(errOutput.toString().contains("Invalid input. Please enter 'y' or 'n'."));
    }
}

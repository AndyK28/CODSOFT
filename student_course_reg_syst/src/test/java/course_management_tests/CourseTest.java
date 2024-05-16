package course_management_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import registration.system.course_management.Course;
import registration.system.course_management.schedule.Schedule;
import registration.system.database_management.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseTest {
    private Course course;
    private Schedule schedule;

    @BeforeEach
    void setUp() {
        schedule = new Schedule(List.of("Thursday"), "09:00", "11:00");
        course = new Course("CS101", "Introduction to Computer Science", "Basic concepts of computer science", 30, schedule);
    }

    @Test
    void testCourseCapacity() throws SQLException {
        // Mock the DatabaseManager.updateCapacity method
        try (MockedStatic<DatabaseManager> mockedDatabaseManager = mockStatic(DatabaseManager.class)) {
            course.courseCapacity();
            mockedDatabaseManager.verify(() -> DatabaseManager.updateCapacity("CS101", 30), times(1));
        }
    }

    @Test
    void testSaveToDatabase() throws SQLException {
        // Mock the DatabaseManager methods
        try (MockedStatic<DatabaseManager> mockedDatabaseManager = mockStatic(DatabaseManager.class)) {
            // Simulate that the course does not exist in the database
            mockedDatabaseManager.when(() -> DatabaseManager.getCourseByCourseCode("CS101")).thenReturn(null);

            course.saveToDatabase();

            mockedDatabaseManager.verify(() -> DatabaseManager.insertCourse(course), times(1));
            mockedDatabaseManager.verify(() -> DatabaseManager.insertSchedule("CS101", schedule), times(1));
        }
    }

    @Test
    void testLoadCourses() throws IOException {
        // Mock the ObjectMapper and its behavior
        Course[] mockCourses = { course };
        try (MockedConstruction<ObjectMapper> ignored = mockConstruction(ObjectMapper.class,
                (mock, context) -> when(mock.readValue(any(File.class), eq(Course[].class))).thenReturn(mockCourses)))
        {
            List<Course> courses = Course.loadCourses();

            assertEquals(1, courses.size());
            assertEquals("CS101", courses.get(0).getCourseCode());
        }
    }
}

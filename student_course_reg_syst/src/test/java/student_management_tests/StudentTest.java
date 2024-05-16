package student_management_tests;

import org.junit.jupiter.api.*;
import registration.system.student_management.Student;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {
    @Test
    void testConstructor() {
        Student student = new Student("S001", "John", "Doe");

        assertEquals("S001", student.studentId());
        assertEquals("John", student.name());
        assertEquals("Doe", student.surname());
    }

    @Test
    void testEquality() {
        Student student1 = new Student("S001", "John", "Doe");
        Student student2 = new Student("S002", "Jane", "Smith");

        assertNotEquals(student1, student2);
    }

    @Test
    void testToStringMethod() {
        Student student = new Student("S001", "John", "Doe");

        assertEquals("Student[studentId=S001, name=John, surname=Doe]", student.toString());
    }
}

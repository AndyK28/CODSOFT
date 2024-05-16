package registration.system.course_management;

import registration.system.student_management.Student;

import java.sql.SQLException;
import java.util.List;

public interface ICourseManagement {
    void checkAvailableCourses() throws SQLException;
    void checkRegisteredCourses(Student student) throws SQLException;
    void registerForCourse(Student student, Course course) throws SQLException;
    void deregisterFromCourse(Student student, Course course) throws SQLException;
}

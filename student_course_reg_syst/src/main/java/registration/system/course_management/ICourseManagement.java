package registration.system.course_management;

import registration.system.student_management.Student;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface for managing course-related operations such as checking available courses,
 * registered courses for a student, course schedule, registering for a course, and deregistering from a course.
 */
public interface ICourseManagement {

    /**
     * Checks and displays the available courses.
     *
     * @throws SQLException if a database access error occurs
     */
    void checkAvailableCourses() throws SQLException;

    /**
     * Checks and displays the courses registered by the specified student.
     *
     * @param student the student whose registered courses are to be checked
     * @throws SQLException if a database access error occurs
     */
    void checkRegisteredCourses(Student student) throws SQLException;

    /**
     * Checks and displays the schedule of courses registered by the specified student.
     *
     * @param student the student whose course schedule is to be checked
     * @throws SQLException if a database access error occurs
     */
    void checkSchedule(Student student) throws SQLException;

    /**
     * Registers the specified student for the specified course.
     *
     * @param student the student to register
     * @param course the course to register for
     * @throws SQLException if a database access error occurs
     */
    void registerForCourse(Student student, Course course) throws SQLException;

    /**
     * Deregisters the specified student from the specified course.
     *
     * @param student the student to deregister
     * @param course the course to deregister from
     * @throws SQLException if a database access error occurs
     */
    void deregisterFromCourse(Student student, Course course) throws SQLException;
}

package registration.system.course_management;

import registration.system.course_management.schedule.Schedule;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.Student;

import java.sql.SQLException;
import java.util.List;

public class CourseManager implements ICourseManagement {
    public void courseList(String message, List<Course> courses) {
        int count = 1;

        System.out.println(message);
        for (Course course : courses) {
            Schedule schedule = course.getSchedule();
            System.out.println(count + ". " + course.getCourseCode() + " - " + course.getTitle() + "\n" +
                    "Schedule:\n" +
                    "Days: " + schedule.days() + "\n" +
                    "Start Time - " + schedule.startTime() + "\n" +
                    "End Time - " + schedule.endTime()
            );
            count++;
        }
    }
    @Override
    public void checkAvailableCourses() throws SQLException {
        List<Course> availableCourses = DatabaseManager.getAvailableCourses();
        if (!availableCourses.isEmpty()) {
            String message = "Available courses:";
            courseList(message, availableCourses);
        } else System.out.println("Sorry, there are no available courses.");
    }

    @Override
    public void checkRegisteredCourses(Student student) throws SQLException {
        List<Course> registeredCourses = DatabaseManager.getRegisteredCourses(student);
        if (!registeredCourses.isEmpty()) {
            String message = "Registered course(s):";
            courseList(message, registeredCourses);
        } else System.out.println("You are not registered for a course.");
    }

    @Override
    public void registerForCourse(Student student, Course course) throws SQLException {
        if (course.getCapacity() > 0) {
            DatabaseManager.registerStudentForCourse(student.studentId(), course.getCourseCode());
            if (DatabaseManager.isStudentRegisteredForCourse(student, course.getCourseCode())) {
                course.setCapacity(course.getCapacity() - 1);
                course.courseCapacity();
                System.out.println("You have successfully registered for course: " + course.getTitle());
            }
        } else System.out.println("Course " + course.getTitle() + " is full.");
    }

    @Override
    public void deregisterFromCourse(Student student, Course course) throws SQLException {
        DatabaseManager.deregisterStudentFromCourse(student.studentId(), course.getCourseCode());
        if (!DatabaseManager.isStudentRegisteredForCourse(student, course.getCourseCode())) {
            course.setCapacity(course.getCapacity() + 1);
            course.courseCapacity();
            System.out.println("Successfully deregistered from course: " + course.getTitle());
        }
    }
}

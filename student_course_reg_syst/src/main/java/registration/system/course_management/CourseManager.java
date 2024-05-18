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
            System.out.println(count + ". " + course.getCourseCode() + " - " + course.getTitle());
            count++;
        }
    }

    public void printSchedule(Schedule schedule, String courseCode) {
        System.out.println("Schedule for " + courseCode + ":\n" +
                "Days: " + schedule.days() + "\n" +
                "Start Time - " + schedule.startTime() + "\n" +
                "End Time - " + schedule.endTime());
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
    public void checkSchedule(Student student) throws SQLException {
        List<Course> courses = DatabaseManager.getRegisteredCourses(student);
        if (!courses.isEmpty()) {
            for (Course course : courses) {
                String courseCode = course.getCourseCode();
                Schedule schedule = DatabaseManager.getScheduleByCourseCode(courseCode);

                if (schedule != null) {
                    printSchedule(schedule, courseCode);
                }
            }
        } else System.err.println("Schedule not found! You have no registered courses.");
    }

    @Override
    public void registerForCourse(Student student, Course course) throws SQLException {
        if (course.getSpacesLeft() > 0) {
            DatabaseManager.registerStudentForCourse(student.studentId(), course.getCourseCode());
            if (DatabaseManager.isStudentRegisteredForCourse(student, course.getCourseCode())) {
                course.setSpacesLeft(course.getSpacesLeft() - 1);
                course.courseCapacity();
                System.out.println("You have successfully registered for course: " + course.getTitle());
            }
        } else System.out.println("Course " + course.getTitle() + " is full.");
    }

    @Override
    public void deregisterFromCourse(Student student, Course course) throws SQLException {
        DatabaseManager.deregisterStudentFromCourse(student.studentId(), course.getCourseCode());
        if (!DatabaseManager.isStudentRegisteredForCourse(student, course.getCourseCode())) {
            course.setSpacesLeft(course.getSpacesLeft() + 1);
            course.courseCapacity();
            System.out.println("Successfully deregistered from course: " + course.getTitle());
        }
    }
}

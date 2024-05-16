package registration.system.course_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import registration.system.course_management.schedule.Schedule;
import registration.system.database_management.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Course {
    private String courseCode;
    private String title;
    private String description;
    private int spacesLeft;
    private Schedule schedule;

    public Course() {}

    public Course(String courseCode, String title, Schedule schedule) {
        this.courseCode = courseCode;
        this.title = title;
        this.schedule = schedule;
    }

    public Course(String courseCode, String title, String description, int spacesLeft) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.spacesLeft = spacesLeft;
    }

    public Course(String courseCode, String title, String description, int spacesLeft, Schedule schedule) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.spacesLeft = spacesLeft;
        this.schedule = schedule;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Schedule getSchedule() {return schedule;}

    public int getSpacesLeft() {
        return spacesLeft;
    }

    public void setSpacesLeft(int spacesLeft) {
        this.spacesLeft = spacesLeft;
    }

    public void courseCapacity() throws SQLException {
        DatabaseManager.updateCapacity(this.courseCode, this.spacesLeft);
    }

    public void saveToDatabase() throws SQLException {
        Course existingCourse = DatabaseManager.getCourseByCourseCode(this.getCourseCode());
        if (existingCourse == null) {
            DatabaseManager.insertCourse(this);
            DatabaseManager.insertSchedule(this.getCourseCode(), this.getSchedule());
        }
    }

    public static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        ObjectMapper mapper  = new ObjectMapper();
        try {
            Course[] courseAArray = mapper.readValue(new File("src/main/resources/courses.json"), Course[].class);
            courses.addAll(Arrays.asList(courseAArray));
        } catch (IOException e) {
            System.err.println("Error: failed to load courses.sql file.\n" + e.getMessage());
        }
        return courses;
    }

    public static void uploadCoursesToDatabase() {
        try {
            List<Course> courses = loadCourses();
            for (Course course : courses) {
                course.saveToDatabase();
            }
        } catch (SQLException e) {
            System.out.println("Error: failed to upload files.\n" + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", spacesLeft=" + spacesLeft +
                ", schedule=" + schedule +
                '}';
    }
}

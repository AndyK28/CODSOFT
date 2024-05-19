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

    /**
     * Constructs a Course object with default values.
     */
    public Course() {}

    /**
     * Constructs a Course object with the specified course code, title, and schedule.
     *
     * @param courseCode the course code
     * @param title the title of the course
     * @param schedule the schedule of the course
     */
    public Course(String courseCode, String title, Schedule schedule) {
        this.courseCode = courseCode;
        this.title = title;
        this.schedule = schedule;
    }

    /**
     * Constructs a Course object with the specified course code, title, description, and spaces left.
     *
     * @param courseCode the course code
     * @param title the title of the course
     * @param description the description of the course
     * @param spacesLeft the number of spaces left in the course
     */
    public Course(String courseCode, String title, String description, int spacesLeft) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.spacesLeft = spacesLeft;
    }

    /**
     * Constructs a Course object with the specified course code, title, description, spaces left, and schedule.
     *
     * @param courseCode the course code
     * @param title the title of the course
     * @param description the description of the course
     * @param spacesLeft the number of spaces left in the course
     * @param schedule the schedule of the course
     */
    public Course(String courseCode, String title, String description, int spacesLeft, Schedule schedule) {
        this.courseCode = courseCode;
        this.title = title;
        this.description = description;
        this.spacesLeft = spacesLeft;
        this.schedule = schedule;
    }

    /**
     * Returns the course code.
     *
     * @return the course code
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Returns the title of the course.
     *
     * @return the title of the course
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of the course.
     *
     * @return the description of the course
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the schedule of the course.
     *
     * @return the schedule of the course
     */
    public Schedule getSchedule() {
        return schedule;
    }

    /**
     * Returns the number of spaces left in the course.
     *
     * @return the number of spaces left
     */
    public int getSpacesLeft() {
        return spacesLeft;
    }

    /**
     * Sets the number of spaces left in the course.
     *
     * @param spacesLeft the number of spaces left to set
     */
    public void setSpacesLeft(int spacesLeft) {
        this.spacesLeft = spacesLeft;
    }

    /**
     * Updates the capacity of the course in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    public void courseCapacity() throws SQLException {
        DatabaseManager.updateCapacity(this.courseCode, this.spacesLeft);
    }

    /**
     * Saves the course information to the database.
     *
     * @throws SQLException if a database access error occurs
     */
    public void saveToDatabase() throws SQLException {
        Course existingCourse = DatabaseManager.getCourseByCourseCode(this.getCourseCode());
        if (existingCourse == null) {
            DatabaseManager.insertCourse(this);
            DatabaseManager.insertSchedule(this.getCourseCode(), this.getSchedule());
        }
    }

    /**
     * Loads the list of courses from a JSON file.
     *
     * @return the list of courses
     */
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

    /**
     * Uploads the courses to the database.
     */
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

    /**
     * Returns a string representation of the Course object.
     *
     * @return a string representation of the Course object
     */
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

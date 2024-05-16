package registration.system;

import registration.system.course_management.Course;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.StudentManager;

import java.io.IOException;
import java.sql.SQLException;

public class RegistrationApp {
    private static void initializeDatabase() throws SQLException, IOException {
        DatabaseManager.connect();
        DatabaseManager.createTables();
        Course.uploadCoursesToDatabase();
    }


    public static void main(String[] args) throws SQLException, IOException {
        StudentManager studentManager = new StudentManager();
        initializeDatabase();
        studentManager.start();
    }
}
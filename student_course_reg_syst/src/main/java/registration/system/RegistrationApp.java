package registration.system;

import registration.system.course_management.Course;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.StudentManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationApp {
    public static void initializeDatabase() throws SQLException, IOException {
        Connection conn = DatabaseManager.connect();
        DatabaseManager.createTables(conn);
        Course.uploadCoursesToDatabase();
    }


    public static void main(String[] args) throws SQLException, IOException {
        StudentManager studentManager = new StudentManager();
        initializeDatabase();
//        studentManager.start();
    }
}
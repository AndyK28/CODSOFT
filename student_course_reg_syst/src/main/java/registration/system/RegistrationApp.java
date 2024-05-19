package registration.system;

import registration.system.course_management.Course;
import registration.system.database_management.DatabaseManager;
import registration.system.student_management.StudentManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationApp {
    /**
     * Initializes the database by creating tables and uploading courses from a JSON file.
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void initializeDatabase() throws SQLException, IOException {
        Connection conn = DatabaseManager.connect();
        DatabaseManager.createTables(conn);
        Course.uploadCoursesToDatabase();
    }

    /**
     * Main method to start the student management system.
     * @param args the command line arguments
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws SQLException, IOException {
        StudentManager studentManager = new StudentManager();
        initializeDatabase();
        studentManager.start();
    }
}
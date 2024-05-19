package registration.system.student_management;

import java.sql.SQLException;

/**
 * Interface for managing student-related operations.
 */
public interface IStudentManagement {
    /**
     * Processes login for new users.
     *
     * @throws SQLException if a database access error occurs
     */
    void processNewUserLogin() throws SQLException;

    /**
     * Processes login for returning users.
     *
     * @throws SQLException if a database access error occurs
     */
    void processReturningUserLogin() throws SQLException;

    /**
     * Generates a unique student ID.
     *
     * @return the generated student ID
     * @throws SQLException if a database access error occurs
     */
    String generateStudentId() throws SQLException;
}

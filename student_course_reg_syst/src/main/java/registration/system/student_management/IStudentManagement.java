package registration.system.student_management;

import java.sql.SQLException;

public interface IStudentManagement {
    void processNewUserLogin() throws SQLException;
    void processReturningUserLogin() throws SQLException;
    String generateStudentId() throws SQLException;
}

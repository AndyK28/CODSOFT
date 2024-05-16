package registration.system.student_management;

import registration.system.course_management.Course;
import registration.system.course_management.CourseManager;
import registration.system.database_management.DatabaseManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class StudentManager extends CourseManager implements IStudentManagement {
    private final Scanner scanner;
    private boolean exit = false;

    public StudentManager() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the Student Registration System");
        try {
            processLogin();
        } catch (SQLException e ) {
            System.err.println("An error occurred while trying to process login: " + e.getMessage());
        }
    }

    private void processLogin() throws SQLException {
        System.out.println("Are you a registered student? (y/n): ");
        String isRegistered = scanner.nextLine().trim().toLowerCase();

        if (isRegistered.equals("y")) {
            processReturningUserLogin();
        } else if (isRegistered.equals("n")) {
            processNewUserLogin();
        } else {
            System.err.println("Invalid response, Please try again.");
            processLogin();
        }
    }

    @Override
    public void processReturningUserLogin() throws SQLException {
        System.out.println("Please enter student ID: ");
        String studentId = scanner.nextLine().toUpperCase();

        if (DatabaseManager.isValidStudentId(studentId)) {
            Student student = DatabaseManager.getStudentById(studentId);
            if (student != null) {
                System.out.println("Welcome back " + student.name() + "!");
                displayMenu(student);
            } else System.err.println("Student not found!");
        } else processLogin();
    }

    @Override
    public void processNewUserLogin() throws SQLException {
        System.out.println("Please create your student profile.");

        String name = enterName();
        String surname = enterSurname();
        String studentId = generateStudentId();
        DatabaseManager.saveStudent(new Student(studentId, name, surname));

        System.out.println("Your student ID is    : " + studentId + "\n" +
                "Profile created successfully..."
        );
        Student student = DatabaseManager.getStudentById(studentId);
        if (student != null) {
            displayMenu(student);
        } else System.err.println("Error: failed to create profile!");
    }

    private String enterName() {
        System.out.print("Enter your name       : ");
        return scanner.nextLine();
    }

    private String enterSurname() {
        System.out.print("Enter your surname    : ");
        return scanner.nextLine();
    }

    @Override
    public String generateStudentId() {
        String studentId;
        try {
            do {
                studentId = "SE" + String.format("%05d", (int) (Math.random() * 100000));
            } while (DatabaseManager.isValidStudentId(studentId));
        } catch (SQLException e) {
            System.err.println("Error: failed to generate student ID: " + e.getMessage());
            studentId = "SE00000";
        }
        return studentId;
    }

    private void printOptions() {
        System.out.println("1. Check for available courses\n" +
                "2. Check for courses registered for\n" +
                "3. Register for a course\n" +
                "4. Deregister from a course\n" +
                "5. Exit\n"
        );
    }

    public Course promptForCourse(List<Course> courses, String promptMessage) {
        System.out.println(promptMessage);
        String courseCode = scanner.nextLine();
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return course;
            }
        }
        return null;
    }

    public void register(Student student) throws SQLException {
        String message = "Enter course code to register: ";
        List<Course> courseList = DatabaseManager.getAllCourses();
        Course regCourse = promptForCourse(courseList, message);
        boolean isRegistered = DatabaseManager.isStudentRegisteredForCourse(student, regCourse.getCourseCode());

        if (!isRegistered) {
            registerForCourse(student, regCourse);
        } else System.err.println("You cannot register for same course twice!");
    }

    public void deregister(Student student) throws SQLException {
        String message = "Enter course code to deregister: ";
        List<Course> courseList1 = DatabaseManager.getAllCourses();
        Course deregCourse = promptForCourse(courseList1, message);

        if (deregCourse != null) {
            deregisterFromCourse(student, deregCourse);
        } else System.err.println("Course not found.");
    }

    public void userChoice(Student student, String userChoice) throws SQLException {
        int choice = Integer.parseInt(userChoice);
        switch (choice) {
            case 1: checkAvailableCourses(); break;
            case 2: checkRegisteredCourses(student); break;
            case 3: register(student); break;
            case 4: deregister(student); break;
            case 5: System.out.println("Exiting program..."); exit = true; break;
            default: System.err.println("Invalid choice. Please choose one of the following options:");
        }

        if (!exit) {
            System.out.println("\nDo you want to continue? (y/n)");
            String continueChoice = scanner.nextLine();
            if (!continueChoice.equalsIgnoreCase("y")) {
                System.out.println("Session Complete.\nGoodbye!");
                exit = true;
            }
        }
    }

    private void displayMenu(Student student) throws SQLException {
        while (!exit) {
            System.out.println("\nPlease choose one of the following options:");
            printOptions();
            String userInput = scanner.nextLine();
            userChoice(student, userInput);
        }
    }
}

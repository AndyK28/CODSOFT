package registration.system.student_management;

import registration.system.course_management.Course;
import registration.system.course_management.CourseManager;
import registration.system.database_management.DatabaseManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class StudentManager extends CourseManager implements IStudentManagement {
    private Scanner scanner;
    private boolean exit = false;

    /**
     * Constructs a StudentManager object with a scanner for user input.
     */
    public StudentManager() {
        this.scanner = new Scanner(System.in);
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

    @Override
    public void processReturningUserLogin() throws SQLException {
        System.out.println("Please enter student ID: ");
        String studentId = scanner.nextLine().toUpperCase();

        if (DatabaseManager.isValidStudentId(studentId)) {
            Student student = DatabaseManager.getStudentById(studentId);
            if (student != null) {
                System.out.println("Welcome back " + student.name() + "!");
                displayMenu(student);
            }
        } else { System.err.println("Student not found! Please enter valid studentId i.e SE12345"); processLogin(); }
    }

    @Override
    public String generateStudentId() {
        String studentId;
        try {
            do {
                studentId = "SE" + String.format("%05d", (int) (Math.random() * 100000));
            } while (DatabaseManager.isValidStudentId(studentId));
        } catch (SQLException e) {
            System.err.println("Error: failed to generate student ID:\n" + e.getMessage());
            studentId = "SE00000";
        }
        return studentId;
    }

    /**
     * Sets the scanner for user input when testing.
     * @param scanner the scanner to be set
     */
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Starts the student registration system by processing login.
     */
    public void start() {
        System.out.println("Welcome to the Student Registration System");
        try {
            processLogin();
        } catch (SQLException e ) {
            System.err.println("An error occurred while trying to process login: " + e.getMessage());
        }
    }

    /**
     * Processes the login based on user input.
     * @throws SQLException if a database access error occurs
     */
    public void processLogin() throws SQLException {
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

    /**
     * Prompts the user to enter their name.
     * @return the entered name
     */
    public String enterName() {
        String name;
        while (true) {
            System.out.print("Enter your name       : ");
            name = scanner.nextLine();
            if (name.matches("^[a-zA-Z]+$")) {
                break;
            } else {
                System.err.println("Invalid input. Name should contain only letters. Please try again.");
            }
        }
        return name;
    }

    /**
     * Prompts the user to enter their surname.
     * @return the entered surname
     */
    public String enterSurname() {
        String surname;
        while (true) {
            System.out.print("Enter your surname    : ");
            surname = scanner.nextLine();
            if (surname.matches("^[a-zA-Z]+$")) {
                break;
            } else {
                System.err.println("Invalid input. Surname should contain only letters. Please try again.");
            }
        }
        return surname;
    }

    /**
     * Prints the options menu for the student.
     */
    private void printOptions() {
        System.out.println("""
                1. Check for available courses
                2. Check for registered courses
                3. Check Schedule
                4. Register for a course
                5. Deregister from a course
                6. Exit
                """
        );
    }

    /**
     * Prompts the user for a course code and returns the corresponding course.
     * @param courses the list of courses to choose from
     * @param promptMessage the prompt message
     * @return the chosen course or null if not found
     */
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

    /**
     * Registers the student for a course.
     * @param student the student to register
     * @throws SQLException if a database access error occurs
     */
    private void register(Student student) throws SQLException {
        String message = "Enter course code to register: ";
        List<Course> courseList = DatabaseManager.getAllCourses();
        Course regCourse = promptForCourse(courseList, message);
        boolean isRegistered = DatabaseManager.isStudentRegisteredForCourse(student, regCourse.getCourseCode());

        if (!isRegistered) {
            registerForCourse(student, regCourse);
        } else System.err.println("You cannot register for same course twice!");
    }

    /**
     * Deregisters the student from a course.
     * @param student the student to deregister
     * @throws SQLException if a database access error occurs
     */
    private void deregister(Student student) throws SQLException {
        String message = "Enter course code to deregister: ";
        List<Course> courseList1 = DatabaseManager.getAllCourses();
        Course deregCourse = promptForCourse(courseList1, message);

        if (deregCourse != null) {
            deregisterFromCourse(student, deregCourse);
        } else System.err.println("Course not found.");
    }

    /**
     * Handles the user's choice of action.
     * @param student the current student
     * @param userChoice the user's choice
     * @throws SQLException if a database access error occurs
     */
    public void userChoice(Student student, String userChoice) throws SQLException {
        int choice = Integer.parseInt(userChoice);
        switch (choice) {
            case 1: checkAvailableCourses(); break;
            case 2: checkRegisteredCourses(student); break;
            case 3: checkSchedule(student); break;
            case 4: register(student); break;
            case 5: deregister(student); break;
            case 6: System.out.println("Exiting program..."); exit = true; break;
            default: System.err.println("Invalid choice. Please choose one of the following options:");
        }

        if (!exit) {
            System.out.println("\nDo you want to continue? (y/n)");
            String continueChoice = getYesOrNoInput();
            if (continueChoice.equalsIgnoreCase("n")) {
                System.out.println("Session Complete.\nGoodbye!");
                exit = true;
            }
        }
    }

    /**
     * Retrieves a yes or no input from the user.
     * @return "y" for yes, "n" for no
     */
    public String getYesOrNoInput() {
        String input;
        while (true) {
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n")) {
                break;
            } else {
                System.err.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
        return input;
    }

    /**
     * Displays the menu for the student and handles user input.
     * @param student the current student
     * @throws SQLException if a database access error occurs
     */
    private void displayMenu(Student student) throws SQLException {
        while (!exit) {
            System.out.println("\nPlease choose one of the following options:");
            printOptions();
            String userInput = scanner.nextLine();
            userChoice(student, userInput);
        }
    }
}

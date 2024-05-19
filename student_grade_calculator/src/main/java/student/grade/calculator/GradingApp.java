package student.grade.calculator;

import java.util.Scanner;

public class GradingApp {
    /**
     * The main method to start the grade calculation application.
     * Prompts the user to enter the number of subjects and initializes a GradeCalculator object.
     * Inputs marks for each subject, calculates and displays the total marks, average marks percentage, and grade.
     * Closes the scanner when finished.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter number of subjects: ");
        int numberOfSubjects = sc.nextInt();

        GradeCalculator gradeCalculator = new GradeCalculator(numberOfSubjects);

        gradeCalculator.inputMarks(sc);
        gradeCalculator.TotalMarks();
        gradeCalculator.AverageMarksPercentage();
        gradeCalculator.calculateGrade();
        sc.close();
    }
}
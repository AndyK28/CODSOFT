package student.grade.calculator;

import java.util.Scanner;

public class GradeCalculator {
    private int[] marks;
    private int totalMarks;
    private double averageMarksPercentage;
    private char grade;

    /**
     * Constructs a GradeCalculator object for a specified number of subjects.
     * Initializes the marks array, total marks, average marks percentage, and grade.
     *
     * @param numberOfSubjects the number of subjects to calculate grades for
     */
    public GradeCalculator(int numberOfSubjects) {
        marks = new int[numberOfSubjects];
        totalMarks = 0;
        averageMarksPercentage = 0;
        grade = ' ';
    }

    /**
     * Returns the marks obtained in each subject.
     *
     * @return an array of marks
     */
    public int[] getMarks() {
        return marks;
    }

    /**
     * Returns the total marks obtained.
     *
     * @return the total marks
     */
    public int getTotalMarks() {
        return totalMarks;
    }

    /**
     * Returns the average marks percentage.
     *
     * @return the average marks percentage
     */
    public double getAverageMarksPercentage() {
        return averageMarksPercentage;
    }

    /**
     * Returns the grade based on the average marks percentage.
     *
     * @return the grade
     */
    public char getGrade() {
        return grade;
    }

    /**
     * Sets the marks for each subject.
     *
     * @param marks an array of marks to set
     */
    public void setMarks(int[] marks) {
        this.marks = marks;
    }

    /**
     * Sets the total marks obtained.
     *
     * @param totalMarks the total marks to set
     */
    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    /**
     * Sets the average marks percentage.
     *
     * @param averageMarksPercentage the average marks percentage to set
     */
    public void setAverageMarksPercentage(double averageMarksPercentage) {
        this.averageMarksPercentage = averageMarksPercentage;
    }

    /**
     * Sets the grade based on the average marks percentage.
     *
     * @param grade the grade to set
     */
    public void setGrade(char grade) {
        this.grade = grade;
    }

    /**
     * Inputs the marks for each subject from the user.
     * Prompts the user to enter marks for each subject and calculates the total marks.
     *
     * @param scanner the scanner to read user input
     */
    public void inputMarks(Scanner scanner) {
        System.out.println("Enter marks obtained in each subject (out of 100): ");
        for (int i = 0; i < marks.length; i++) {
            System.out.println("Subject " + (i + 1) + ": ");
            marks[i] = scanner.nextInt();
            totalMarks += marks[i];
        }
    }

    /**
     * Displays the total marks obtained.
     */
    public void TotalMarks() {
        System.out.println("Total Marks obtained: " + totalMarks);
    }

    /**
     * Calculates and displays the average marks percentage.
     */
    public void AverageMarksPercentage() {
        averageMarksPercentage = (double) totalMarks / marks.length;
        System.out.println("Average Marks Percentage: " + averageMarksPercentage);
    }

    /**
     * Calculates and displays the grade based on the average marks percentage.
     * Sets the grade according to the specified grading criteria.
     */
    public void calculateGrade() {
        if (averageMarksPercentage >= 90) {
            grade = 'A';
        } else if (averageMarksPercentage >= 80) {
            grade = 'B';
        } else if (averageMarksPercentage >= 70) {
            grade = 'C';
        } else if (averageMarksPercentage >= 50) {
            grade = 'D';
        } else {
            grade = 'F';
        }
        System.out.println("Grade is: " + grade);
    }
}

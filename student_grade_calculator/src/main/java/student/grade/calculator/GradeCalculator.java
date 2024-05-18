package student.grade.calculator;

import java.util.Scanner;

public class GradeCalculator {
    private int[] marks;
    private int totalMarks;
    private double averageMarksPercentage;
    private char grade;

    public GradeCalculator(int numberOfSubjects) {
        marks = new int[numberOfSubjects];
        totalMarks = 0;
        averageMarksPercentage = 0;
        grade = ' ';
    }

    public int[] getMarks() {
        return marks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public double getAverageMarksPercentage() {
        return averageMarksPercentage;
    }

    public char getGrade() {
        return grade;
    }

    public void setMarks(int[] marks) {
        this.marks = marks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public void setAverageMarksPercentage(double averageMarksPercentage) {
        this.averageMarksPercentage = averageMarksPercentage;
    }

    public void setGrade(char grade) {
        this.grade = grade;
    }

    public void inputMarks(Scanner scanner) {
        System.out.println("Enter marks obtained in each subject (out of 100): ");
        for (int i = 0; i < marks.length; i++) {
            System.out.println("Subject " + (i + 1) + ": ");
            marks[i] = scanner.nextInt();
            totalMarks += marks[i];
        }
    }

    public void TotalMarks() {
        System.out.println("Total Marks obtained: " + totalMarks);
    }

    public void AverageMarksPercentage() {
        averageMarksPercentage = (double) totalMarks / marks.length;
        System.out.println("Average Marks Percentage: " + averageMarksPercentage);
    }

    public void calculateGrade() {
        if (averageMarksPercentage >= 90) {
            grade = 'A';
        } else if (averageMarksPercentage >= 80) {
            grade = 'B';
        } else if (averageMarksPercentage >= 70) {
            grade = 'C';
        } else if (averageMarksPercentage >= 50) {
            grade = 'D';
        } else grade = 'F';
        System.out.println("Grade is: " + grade);
    }
}

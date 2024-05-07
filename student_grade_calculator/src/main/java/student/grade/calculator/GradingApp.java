package student.grade.calculator;

import java.util.Scanner;

public class GradingApp {
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
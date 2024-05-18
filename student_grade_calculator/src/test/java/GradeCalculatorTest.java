import org.junit.jupiter.api.*;
import student.grade.calculator.GradeCalculator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradeCalculatorTest {
    private GradeCalculator calculator;
    private int sumOfMarks;
    private int mark1;
    private int mark2;
    private int mark3;
    private String input;

    @BeforeEach
    void setUp() {
        int numOfSubjects = 3;
        mark1 = 80;
        mark2 = 90;
        mark3 = 75;

        sumOfMarks = mark1 + mark2 + mark3;
        input = mark1 + "\n" + mark2 + "\n" + mark3 + "\n";

        calculator = new GradeCalculator(numOfSubjects);
        calculator.setMarks(new int[]{mark1, mark2, mark3});
        calculator.setTotalMarks(sumOfMarks);
    }

    @Test
    public void testInputMarks() {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        calculator.inputMarks(new Scanner(in));

        assertEquals(mark1, calculator.getMarks()[0]);
        assertEquals(mark2, calculator.getMarks()[1]);
        assertEquals(mark3, calculator.getMarks()[2]);
    }

    @Test
    public void testTotalMarks() {
        int result = calculator.getTotalMarks();

        assertEquals(sumOfMarks, result);
    }

    @Test
    public void testAverageMarksPercentage() {
        double result = (double) calculator.getTotalMarks() / calculator.getMarks().length;

        calculator.AverageMarksPercentage();

        assertEquals(result, calculator.getAverageMarksPercentage());
    }

    @Test
    public void testCalculateGrade() {
        double avPercentage = 90.0;

        calculator.setAverageMarksPercentage(avPercentage);
        calculator.calculateGrade();

        assertEquals('A', calculator.getGrade());
    }
}

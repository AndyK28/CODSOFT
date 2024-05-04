package NumberGameTest;

import numberGame.NumberGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class PlayRoundTest {
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(123);
    }

    @Test
    public void testPlayRound_CorrectGuess() {
        String input = "3\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        NumberGame numberGame = new NumberGame(1, 10, 5, random);
        boolean result = numberGame.playRound();

        assertTrue(result);
        assertTrue(outputStream.toString().contains("Congratulations! You guessed the number correctly."));

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testPlayRound_TooLow() {
        String input = "1\n2\n1\n2\n1";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        NumberGame numberGame = new NumberGame(1, 10, 5, random);
        boolean result = numberGame.playRound();

        assertFalse(result);
        assertTrue(outputStream.toString().contains("Too low. Try again."));

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testPlayRound_TooHigh() {
        String input = "5\n6\n7\n8\n9";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        NumberGame numberGame = new NumberGame(1, 10, 5, random);
        boolean result = numberGame.playRound();

        assertFalse(result);
        assertTrue(outputStream.toString().contains("Too high. Try again."));

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void testPlayRound_RunOutOfAttempts() {
        String input = "1\n2\n3\n4\n5\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        NumberGame numberGame = new NumberGame(1, 10, 2, random);
        boolean result = numberGame.playRound();

        assertFalse(result);
        assertTrue(outputStream.toString().contains("Sorry, you've run out of attempts."));

        System.setIn(System.in);
        System.setOut(System.out);
    }
}

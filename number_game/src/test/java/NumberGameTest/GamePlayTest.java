package NumberGameTest;

import numberGame.NumberGame;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GamePlayTest {
    @Test
    public void gamePlayTest() {
        String input = "11\nno\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        NumberGame game = new NumberGame(1, 10, 1);
        game.gamePlay();
        int generatedNumber = game.getRandomNumber();

        System.setIn(System.in);
        System.setOut(System.out);

        String expectedOutput = "Welcome to the number game!\n" +
                "Guess the number between 1 and 10.\n" +
                "Attempts left: 1\n" +
                "Enter your guess: \n" +
                "Too high. Try again.\n" +
                "Sorry, you've run out of attempts. The number was: " + generatedNumber + "\n" +
                "Would you like to play again? (yes/no)\n" +
                "Game over. Your total score is: 0\n" +
                "Thank you for playing!\n";
        assertEquals(expectedOutput, outputStream.toString());
        assertTrue(generatedNumber >= 1 && generatedNumber <= 10);
    }
}

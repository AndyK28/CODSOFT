package numberGame;

public class Main {
    /**
     * The main method to start the number guessing game application.
     * Initializes a NumberGame object with a range from 1 to 100 and a maximum of 10 attempts.
     * Starts the game by calling the gamePlay method.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        NumberGame game = new NumberGame(1, 100, 10);
        game.gamePlay();
    }
}
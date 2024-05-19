package numberGame;

import java.util.Random;
import java.util.Scanner;

public class NumberGame {
    private final int minRange;
    private final int maxRange;
    private final int maxAttempts;
    private int score;
    private final Scanner scanner;
    private final Random random;
    private int randomNumber;

    /**
     * Constructs a NumberGame object with the specified range and maximum number of attempts.
     * Initializes the score to 0, and sets up a Scanner and Random instance.
     *
     * @param minRange the minimum range of the number to guess
     * @param maxRange the maximum range of the number to guess
     * @param maxAttempts the maximum number of attempts allowed
     */
    public NumberGame(int minRange, int maxRange, int maxAttempts) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.maxAttempts = maxAttempts;
        this.score = 0;

        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    /**
     * Constructs a NumberGame object with the specified range, maximum number of attempts, and a Random instance.
     * Initializes the score to 0, and sets up a Scanner instance.
     * This constructor is intended for testing purposes to use a provided Random instance.
     *
     * @param minRange the minimum range of the number to guess
     * @param maxRange the maximum range of the number to guess
     * @param maxAttempts the maximum number of attempts allowed
     * @param random the Random instance to use for generating random numbers
     */
    public NumberGame(int minRange, int maxRange, int maxAttempts, Random random) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.maxAttempts = maxAttempts;
        this.score = 0;

        this.scanner = new Scanner(System.in);
        this.random = random;
    }

    /**
     * Returns the random number generated for the current round.
     * This method is intended for testing purposes only.
     *
     * @return the random number for the current round
     */
    public int getRandomNumber() {
        return randomNumber;
    }

    /**
     * Plays a single round of the number guessing game.
     * Generates a random number within the specified range and allows the user to guess the number.
     * Provides feedback on the user's guesses and returns true if the user guesses correctly within the allowed attempts.
     * Returns false if the user fails to guess the number within the allowed attempts.
     *
     * @return true if the user guesses the number correctly, false otherwise
     */
    public boolean playRound() {
        randomNumber = random.nextInt(maxRange - minRange + 1) + minRange;
        int attempts = 0;

        System.out.println("Guess the number between " + minRange + " and " + maxRange + ".");

        while (attempts < maxAttempts) {
            System.out.println("Attempts left: " + (maxAttempts - attempts));
            System.out.println("Enter your guess: ");

            String userInput = scanner.nextLine();
            try {
                int userGuess = Integer.parseInt(userInput);
                attempts++;

                if (userGuess == randomNumber) {
                    System.out.println("Congratulations! You guessed the number correctly.");
                    return true;
                } else if (userGuess < randomNumber) {
                    System.out.println("Too low. Try again.");
                } else {
                    System.out.println("Too high. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("You entered an invalid number.");
            }
        }

        System.out.println("Sorry, you've run out of attempts. The number was: " + randomNumber);
        return false;
    }

    /**
     * Starts the number guessing game, allowing the user to play multiple rounds.
     * Displays a welcome message and loops until the user chooses not to play again.
     * Updates and displays the user's score, and closes the scanner when the game ends.
     */
    public void gamePlay() {
        System.out.println("Welcome to the number game!");
        boolean playAgain = true;

        while (playAgain) {
            if (playRound()) {
                score++;
            }
            System.out.println("Would you like to play again? (yes/no)");
            String answer = scanner.nextLine();
            playAgain = answer.equalsIgnoreCase("yes");
        }
        System.out.println("Game over. Your total score is: " + score);
        System.out.println("Thank you for playing!");
        scanner.close();
    }
}

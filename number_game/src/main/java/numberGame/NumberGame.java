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

    public NumberGame(int minRange, int maxRange, int maxAttempts) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.maxAttempts = maxAttempts;
        this.score = 0;

        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    // for testing purposes only
    public NumberGame(int minRange, int maxRange, int maxAttempts, Random random) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.maxAttempts = maxAttempts;
        this.score = 0;

        this.scanner = new Scanner(System.in);
        this.random = random;
    }

    // For Testing purposes only
    public int getRandomNumber() {
        return randomNumber;
    }

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
                } else System.out.println("Too high. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("You entered an invalid number.");
            }
        }

        System.out.println("Sorry, you've run out of attempts. The number was: " + randomNumber);
        return false;
    }

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

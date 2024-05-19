package quiz.application;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Quiz {
    private final List<Question> questions;
    private int score;
    private final int totalQuestions;
    private final int timePerQuestion;
    private boolean timeIsUp = false;
    private ScheduledExecutorService scheduler;

    /**
     * Constructs a Quiz object with the provided list of questions.
     * Initializes the score to 0, sets the total number of questions to 5,
     * and sets the time allowed per question to 10 seconds.
     *
     * @param questions the list of questions for the quiz
     */
    public Quiz(List<Question> questions) {
        this.questions = questions;
        score = 0;
        this.totalQuestions = 5;
        this.timePerQuestion = 10;
    }

    /**
     * Starts the quiz, displaying a welcome message and running the quiz loop.
     * Prompts the user to play again after completing the quiz.
     * Ends the quiz and closes the scanner when the user chooses not to play again.
     */
    public void startQuiz() {
        welcomeMessage();
        boolean playAgain = true;
        Scanner scanner = new Scanner(System.in);

        while (playAgain) {
            for (int i = 0; i < totalQuestions; i++) {
                List<Question> shuffledQuestions = new ArrayList<>(questions);
                Collections.shuffle(shuffledQuestions);
                Question currentQuestion = shuffledQuestions.get(i);

                System.out.println("Question " + (i + 1));
                displayCurrentQuestion(currentQuestion);

                scheduler = Executors.newSingleThreadScheduledExecutor();

                int userChoice = userChoiceWithTimer(scanner, currentQuestion);

                if (userChoice == -1) {
                    System.out.println("You ran out of time...\n");
                } else {
                    checkAnswer(currentQuestion, userChoice);
                }
            }
            displayResult();

            System.out.println("Do you want to play again? (y/n)");
            String playAgainInput = scanner.next().toLowerCase();
            playAgain = playAgainInput.equals("y");
        }
        System.out.print("Thank you for playing Quiz!");
        stopQuestionTimer();
        scanner.close();
    }

    /**
     * Displays a welcome message to the user, providing information about the quiz.
     */
    private void welcomeMessage() {
        System.out.println("Welcome to The Quiz Game!\n" +
                "You are presented with " + totalQuestions + " multiple choice questions.\n" +
                "You have " + timePerQuestion + " seconds to answer per question.\n"
        );
    }

    /**
     * Displays the current question and its options.
     *
     * @param question the current question to be displayed
     */
    public void displayCurrentQuestion(Question question) {
        System.out.println(question.getQuestion());
        for (String option : question.getOptions()) {
            System.out.println(option);
        }
        System.out.println();
    }

    /**
     * Starts a timer for the current question, setting a flag when the time is up.
     */
    private void startQuestionTimer() {
        timeIsUp = false;
        scheduler.schedule(() -> timeIsUp = true, timePerQuestion, TimeUnit.SECONDS);
    }

    /**
     * Stops the timer for the current question if it is still running.
     */
    public void stopQuestionTimer() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    /**
     * Prompts the user for their choice within the allotted time.
     * Returns -1 if the time runs out before a choice is made.
     *
     * @param scanner the scanner to read user input
     * @param currentQuestion the current question being answered
     * @return the index of the user's choice, or -1 if time runs out
     */
    private int userChoiceWithTimer(Scanner scanner, Question currentQuestion) {
        int userChoice = -1;
        int numberOfOptions = currentQuestion.getOptions().length;
        timeIsUp = false;

        System.out.println("Enter your choice: ");
        startQuestionTimer();

        while ((!timeIsUp && userChoice == -1)) {
            if (scanner.hasNextLine()) {
                userChoice = getUserChoice(scanner, numberOfOptions);
                stopQuestionTimer();
            }
        }
        return timeIsUp ? -1 : userChoice;
    }

    /**
     * Reads and validates the user's choice, ensuring it is within the range of valid options.
     *
     * @param scanner the scanner to read user input
     * @param numberOfOptions the number of valid options for the current question
     * @return the index of the user's valid choice
     */
    public int getUserChoice(Scanner scanner, int numberOfOptions) {
        int choiceIndex = -1;

        while (choiceIndex < 0 || choiceIndex >= numberOfOptions) {
            if (scanner.hasNextLine()) {
                String choice = scanner.nextLine().toUpperCase();
                choiceIndex = choice.charAt(0) - 'A';

                if (choiceIndex < 0 || choiceIndex >= numberOfOptions) {
                    System.out.println("Invalid choice. Please enter a valid choice!");
                }
            } else {
                break;
            }
        }
        return choiceIndex;
    }

    /**
     * Checks the user's answer against the correct answer for the question.
     * Updates the score if the answer is correct and displays feedback to the user.
     *
     * @param question the current question being answered
     * @param userChoice the user's choice for the answer
     */
    private void checkAnswer(Question question, int userChoice) {
        if (userChoice == question.getCorrectAnswerIndex()) {
            System.out.println("Correct!");
            score++;
        } else {
            System.out.println("Incorrect. The correct answer is " + question.getOptions()[question.getCorrectAnswerIndex()]);
        }
        System.out.println();
    }

    /**
     * Displays the user's final score after the quiz is completed.
     */
    public void displayResult() {
        System.out.println("Quiz completed!");
        System.out.println("Your score is " + score + "/" + totalQuestions);
    }
}
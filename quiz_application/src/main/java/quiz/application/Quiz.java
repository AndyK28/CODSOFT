package quiz.application;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Quiz {
    private final List<Question> questions;
    private int score;
    private final int totalQuestions;
    private final int timePerQuestion;
    private volatile boolean timeIsUp = false;

    public Quiz(List<Question> questions) {
        this.questions = questions;
        this.totalQuestions = 5;
        this.timePerQuestion = 5;
    }

    public void startQuiz() {
        welcomeMessage();
        boolean playAgain = true;
        Scanner scanner = new Scanner(System.in);

        while (playAgain) {
            score = 0;

            for (int i = 0; i < totalQuestions; i++) {
                List<Question> shuffledQuestions = new ArrayList<>(questions);
                Collections.shuffle(shuffledQuestions);
                Question currentQuestion = shuffledQuestions.get(i);

                System.out.println("Question " + (i+1));
                displayCurrentQuestion(currentQuestion);

                int userChoice = userChoiceWithTimer(scanner, currentQuestion);

                if (!(userChoice == -1)) {
                    checkAnswer(currentQuestion, userChoice);
                } else System.out.println("Time's up! Moving on to the next question\n");
            }

            displayResult();

            System.out.println("Do you want to play again? (y/n)");
            String playAgainInput = scanner.next().toLowerCase();
            playAgain = playAgainInput.equals("y");
        }
        System.out.print("Thank you for playing Quiz!");
        scanner.close();
    }

    private void welcomeMessage() {
        System.out.println("Welcome to The Quiz Game!\n" +
                "You are presented with " + totalQuestions + " multiple choice questions.\n" +
                "You have " + timePerQuestion + " seconds to answer per question.\n"
        );
    }

    public void displayCurrentQuestion(Question question) {
        System.out.println(question.getQuestion());
        for (String option : question.getOptions()) {
            System.out.println(option);
        }
        System.out.println();
    }

    private void startTimerThread(Scanner scanner, CountDownLatch latch) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> {
            synchronized (scanner) {
                if (!timeIsUp) {
                    scanner.notify();
                }
                timeIsUp = true;
                latch.countDown();
            }
        }, timePerQuestion, TimeUnit.SECONDS);
    }

    private int userChoiceWithTimer(Scanner scanner, Question currentQuestion) {
        AtomicInteger userChoice = new AtomicInteger();
        int numberOfOptions = currentQuestion.getOptions().length;

        CountDownLatch latch = new CountDownLatch(1);

        Thread userInputThread = new Thread(() -> {
            synchronized (scanner) {
                userChoice.set(getUserChoice(scanner, numberOfOptions));
                if (!timeIsUp) {
                    System.out.println("User choice: " + userChoice);
                }
                latch.countDown();
            }
        });
        userInputThread.start();

        synchronized (scanner) {
            try {
                System.out.println("Enter your choice: ");
                startTimerThread(scanner, latch);
                scanner.wait();
            } catch (InterruptedException ignore) {

            } finally {
                timeIsUp = false;
            }
        }
        return userChoice.get();
    }

    public int getUserChoice(Scanner scanner, int numberOfOptions) {
        int choiceIndex = -1;

        while ((choiceIndex < 0 || choiceIndex >= numberOfOptions) && !timeIsUp) {
            String choice = scanner.nextLine().toUpperCase();
            System.out.println(">>>> " + choice);
            choiceIndex = choice.charAt(0) - 'A';

            if (choiceIndex < 0 || choiceIndex >= numberOfOptions) {
                System.out.println("Invalid choice. Please enter a valid choice!");
            }
        }
        return choiceIndex;
    }

    private void checkAnswer(Question question, int userChoice) {
        if (userChoice == question.getCorrectAnswerIndex()) {
            System.out.println("Correct!");
            score++;
        } else System.out.println("Incorrect. The correct answer is " + question.getOptions()[question.getCorrectAnswerIndex()]);
        System.out.println();
    }

    public void displayResult() {
        System.out.println("Quiz completed!");
        System.out.println("Your score is " + score + "/" + totalQuestions);
    }
}

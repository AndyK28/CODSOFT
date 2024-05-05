package quiz.application;

import java.util.*;

public class Quiz {
    private final List<Question> questions;
    private int score;
    private final int totalQuestions;
    private final int timePerQuestion;
    private boolean timeIsUp = false;

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

                if (userChoice == -1) {
                System.out.println("Time's up! Moving on to the next question\n");
                } else checkAnswer(currentQuestion, userChoice);
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

    private void startQuestionTimer(Scanner scanner) {
        Thread timerThread = new Thread(() -> {
            try {
                Thread.sleep( timePerQuestion * 1000L);
                synchronized (scanner) {
                    scanner.notify();
                    timeIsUp = true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timerThread.start();
    }

    private int userChoiceWithTimer(Scanner scanner, Question currentQuestion) {
        int userChoice = -1;

        synchronized
        (scanner) {
            try {
                System.out.println("Enter your choice: ");
                startQuestionTimer(scanner);
                if (!timeIsUp) {
                    scanner.wait();
                    userChoice = getUserChoice(scanner, currentQuestion.getOptions().length);
                }
            } catch (InterruptedException ignore) {

            } finally {
                timeIsUp = false;
            }
        }
        return userChoice;
    }

    public int getUserChoice(Scanner scanner, int numberOfOptions) {
        int choiceIndex = -1;

        while ((choiceIndex < 0 || choiceIndex >= numberOfOptions) && !timeIsUp) {
            String choice = scanner.nextLine().toUpperCase();
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

package quiz.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class QuizApp {
    /**
     * Reads questions from a JSON file and returns a list of Question objects.
     * Uses Jackson's ObjectMapper to deserialize the JSON data.
     * If an IOException occurs, prints an error message and returns an empty list.
     *
     * @return a list of Question objects read from the JSON file
     */
    public static List<Question> readQuestions() {
        List<Question> questions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = QuizApp.class.getResourceAsStream("/questions.json");
            questions = objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            System.out.println("Error reading questions.json");
        }

        return questions;
    }

    /**
     * The main method to start the quiz application.
     * Reads the questions from the JSON file, initializes the Quiz object, and starts the quiz.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        List<Question> questions = readQuestions();

        Quiz quiz = new Quiz(questions);
        quiz.startQuiz();
    }
}
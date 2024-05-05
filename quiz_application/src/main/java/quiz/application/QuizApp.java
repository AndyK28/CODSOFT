package quiz.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class QuizApp {

    public static List<Question> readQuestions() {
        List<Question> questions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = QuizApp.class.getResourceAsStream("/questions.json");
            questions = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.out.println("Error reading questions.json");
        }

        return questions;
    }

    public static void main(String[] args) {
        List<Question> questions = readQuestions();

        Quiz quiz = new Quiz(questions);
        quiz.startQuiz();
    }
}
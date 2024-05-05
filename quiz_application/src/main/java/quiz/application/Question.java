package quiz.application;

public class Question {
    private String question;
    private String[] options;
    private int correctAnswerIndex;

    public String getQuestion() {
        return question;
    }

    public String[] getOptions() {
        return options != null ? options : new String[0];
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex >= 0 && correctAnswerIndex < options.length ? correctAnswerIndex : -1;
    }
}
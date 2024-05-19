package quiz.application;

public class Question {
    private final String question;
    private final String[] options;
    private final int correctAnswerIndex;

    public Question(String question, String[] options, int correctAnswerIndex) {
        this.question = question;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
    }

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
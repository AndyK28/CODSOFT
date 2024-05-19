package quiz.application;

public class Question {
    private String question;
    private String[] options;
    private int correctAnswerIndex;

    /**
     * Returns the text of the question.
     *
     * @return the question text
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Returns the options for the question.
     * If the options are null, returns an empty array.
     *
     * @return an array of options for the question
     */
    public String[] getOptions() {
        return options != null ? options : new String[0];
    }

    /**
     * Returns the index of the correct answer.
     * Ensures the index is within the valid range of options.
     * If the index is invalid, returns -1.
     *
     * @return the index of the correct answer, or -1 if invalid
     */
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex >= 0 && correctAnswerIndex < options.length ? correctAnswerIndex : -1;
    }
}
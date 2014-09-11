package sss.dialog;

public class BasicQA {
    private String question;
    private String answer;
    private String normalizedQuestion;
    private String normalizedAnswer;

    public BasicQA(String question, String answer, String normalizedQuestion, String normalizedAnswer) {
        this.question = question;
        this.answer = answer;
        this.normalizedQuestion = normalizedQuestion;
        this.normalizedAnswer = normalizedAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getNormalizedQuestion() {
        return normalizedQuestion;
    }

    public String getNormalizedAnswer() {
        return normalizedAnswer;
    }

    public String toString() {
        return "Q - " + normalizedQuestion +
                "\nA - " + normalizedAnswer;
    }
}

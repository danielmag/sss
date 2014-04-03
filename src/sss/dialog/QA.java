package sss.dialog;

import java.util.Arrays;
import java.util.List;

public class QA {

    private double score;
    private String question;
    private String answer;
    private long diff;
    private String questionNormalized;
    private String answerNormalized;
    private List<String> questionListNormalized = null; //I am using null values to allow lazy initialization
    private List<String> answerListNormalized = null;

    public QA(String q, String a, String questionNormalized, String answerNormalized, long diff) {
        this.question = q;
        this.answer = a;
        this.questionNormalized = questionNormalized;
        this.answerNormalized = answerNormalized;
        this.score = 0.0;
        this.diff = diff;
    }

    public double getScore() {
        return score;
    }

    public void addScore(double score) {
        this.score += score;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public long getDiff() {
        return diff;
    }

    public String getAnswerNormalized() {
        return answerNormalized;
    }

    public String getQuestionNormalized() {
        return questionNormalized;
    }

    public List<String> getQuestionListNormalized() {
        if (questionListNormalized != null) {
            return questionListNormalized;
        } else {
            String questionLemma = getQuestionNormalized();
            return questionListNormalized = Arrays.asList(questionLemma.split("\\s+"));
        }
    }

    public List<String> getAnswerListNormalized() {
        if (answerListNormalized != null) {
            return answerListNormalized;
        } else {
            String answerLemma = getAnswerNormalized();
            return answerListNormalized = Arrays.asList(answerLemma.split("\\s+"));
        }
    }
}
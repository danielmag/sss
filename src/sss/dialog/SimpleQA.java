package sss.dialog;

import java.io.Serializable;

public class SimpleQA implements Serializable {
    private String previousQA;
    private String question;
    private String answer;
    private String lemmatizedQuestion;
    private String lemmatizedAnswer;
    private long diff;

    public SimpleQA(String previousQA, String question, String answer, String lemmatizedQuestion, String lemmatizedAnswer, long diff) {
        this.previousQA = previousQA;
        this.question = question;
        this.answer = answer;
        this.lemmatizedAnswer = lemmatizedAnswer;
        this.lemmatizedQuestion = lemmatizedQuestion;
        this.diff = diff;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getLemmatizedQuestion() {
        return lemmatizedQuestion;
    }

    public String getLemmatizedAnswer() {
        return lemmatizedAnswer;
    }

    public long getDiff() {
        return diff;
    }

    public String getPreviousQA() {
        return previousQA;
    }
}

package sss.dialog;

import java.io.Serializable;

public class SimpleL2RQA implements Serializable {
    private long previousQA;
    private int questionHeadWordIndex;
    private int answerHeadWordIndex;
    private String question;
    private String answer;
    private String normalizedQuestion;
    private String normalizedAnswer;
    private long diff;

    public SimpleL2RQA(long previousQA, int questionHeadWordIndex, int answerHeadWordIndex, String question, String answer, String normalizedQuestion, String normalizedAnswer, long diff) {
        this.previousQA = previousQA;
        this.questionHeadWordIndex = questionHeadWordIndex;
        this.answerHeadWordIndex = answerHeadWordIndex;
        this.question = question;
        this.answer = answer;
        this.normalizedAnswer = normalizedAnswer;
        this.normalizedQuestion = normalizedQuestion;
        this.diff = diff;
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

    public long getDiff() {
        return diff;
    }

    public long getPreviousQA() {
        return previousQA;
    }

    public int getQuestionHeadWordIndex() {
        return questionHeadWordIndex;
    }

    public int getAnswerHeadWordIndex() {
        return answerHeadWordIndex;
    }
}

package sss.dialog;

import edu.stanford.nlp.util.CoreMap;

import java.io.Serializable;
import java.util.List;

public class SimpleQA implements Serializable {
    private String question;
    private String answer;
    private String lemmatizedQuestion;
    private String lemmatizedAnswer;
    private long diff;

    public SimpleQA(String question, String answer, String lemmatizedQuestion, String lemmatizedAnswer, long diff) {
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
}

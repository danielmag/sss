package sss.dialog;

import l2f.nlp.NormalizerSimple;
import l2f.nlp.SimpleTokenizer;

import java.util.*;

public class QA {

    private double score;
    private String question;
    private String answer;
    private long diff;
    private String answerNormalized = null; //I am using null values to allow lazy initialization
    private String questionNormalized = null;
    private List<String> questionList = null;
    private List<String> answerList = null;
    private List<String> questionListNormalized = null;
    private List<String> answerListNormalized = null;

    public QA(String q, String a, long diff) {
        this.question = q;
        this.answer = a;
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
        if (answerNormalized != null) {
            return answerNormalized;
        } else {
            return answerNormalized = NormalizerSimple.normPunctLCaseDMarks(answer);
        }
    }

    public String getQuestionNormalized() {
        if (questionNormalized != null) {
            return questionNormalized;
        } else {
            return questionNormalized = NormalizerSimple.normPunctLCaseDMarks(question);
        }
    }

    public List<String> getQuestionList() {
        if (questionList != null) {
            return questionList;
        } else {
            return questionList = (new SimpleTokenizer()).tokenize(question);
        }
    }

    public List<String> getAnswerList() {
        if (answerList != null) {
            return answerList;
        } else {
            return answerList = (new SimpleTokenizer()).tokenize(answer);
        }
    }

    public List<String> getQuestionListNormalized() {
        if (questionListNormalized != null) {
            return questionListNormalized;
        } else {
            String questionNorm = getQuestionNormalized();
            return questionListNormalized = (new SimpleTokenizer()).tokenize(questionNorm);
        }
    }

    public List<String> getAnswerListNormalized() {
        if (answerListNormalized != null) {
            return answerListNormalized;
        } else {
            String answerNorm = getAnswerNormalized();
            return answerListNormalized = (new SimpleTokenizer()).tokenize(answerNorm);
        }
    }
}
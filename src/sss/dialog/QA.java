package sss.dialog;

import edu.stanford.nlp.util.CoreMap;

import java.util.Arrays;
import java.util.List;

public class QA {

    private double score;
    private String question;
    private String answer;
    private long diff;
    private String questionLemmatized;
    private String answerLemmatized;
    private List<CoreMap> questionSentences;
    private List<CoreMap> answerSentences;
    private List<String> questionListLemmatized = null; //I am using null values to allow lazy initialization
    private List<String> answerListLemmatized = null;

    public QA(String q, String a, String questionLemmatized, String answerLemmatized, List<CoreMap> questionSentences, List<CoreMap> answerSentences, long diff) {
        this.question = q;
        this.answer = a;
        this.questionLemmatized = questionLemmatized;
        this.answerLemmatized = answerLemmatized;
        this.questionSentences = questionSentences;
        this.answerSentences = answerSentences;
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

    public String getAnswerLemmatized() {
        return answerLemmatized;
    }

    public String getQuestionLemmatized() {
        return questionLemmatized;
    }

    public List<CoreMap> getQuestionSentences() {
        return questionSentences;
    }

    public List<CoreMap> getAnswerSentences() {
        return answerSentences;
    }

    public List<String> getQuestionListLemmatized() {
        if (questionListLemmatized != null) {
            return questionListLemmatized;
        } else {
            String questionLemma = getQuestionLemmatized();
            return questionListLemmatized = Arrays.asList(questionLemma.split("\\s+"));
        }
    }

    public List<String> getAnswerListLemmatized() {
        if (answerListLemmatized != null) {
            return answerListLemmatized;
        } else {
            String answerLemma = getAnswerLemmatized();
            return answerListLemmatized = Arrays.asList(answerLemma.split("\\s+"));
        }
    }
}
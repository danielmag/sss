package sss.dialog;

import edu.stanford.nlp.util.CoreMap;

import java.io.Serializable;
import java.util.List;

public class SimpleQA implements Serializable {
    private long previousQA;
    private String question;
    private String answer;
    private String lemmatizedQuestion;
    private String lemmatizedAnswer;
    private List<CoreMap> questionSentences;
    private List<CoreMap> answerSentences;
    private long diff;

    public SimpleQA(long previousQA, String question, String answer, String lemmatizedQuestion, String lemmatizedAnswer, List<CoreMap> questionSentences, List<CoreMap> answerSentences, long diff) {
        this.previousQA = previousQA;
        this.question = question;
        this.answer = answer;
        this.lemmatizedAnswer = lemmatizedAnswer;
        this.lemmatizedQuestion = lemmatizedQuestion;
        this.questionSentences = questionSentences;
        this.answerSentences = answerSentences;
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

    public long getPreviousQA() {
        return previousQA;
    }

    public List<CoreMap> getQuestionSentences() {
        return questionSentences;
    }

    public List<CoreMap> getAnswerSentences() {
        return answerSentences;
    }
}

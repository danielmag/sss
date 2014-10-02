package sss.dialog;

import sss.exceptions.dialog.NoPreviousQAException;
import sss.lucene.LuceneManager;
import sss.main.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class L2RQA extends QA {

    private long previousQA;
    private int questionHeadWordIndex;
    private int answerHeadWordIndex;
    private double score;
    private ArrayList<Double> scores;
    private long diff;
    private List<String> questionListNormalized = null; //I am using null values to allow lazy initialization
    private List<String> answerListNormalized = null;

    public L2RQA(long previousQA, String q, String a, String questionNormalized, String answerNormalized, int questionHeadWordIndex, int answerHeadWordIndex, long diff) {
        super(previousQA, q, a, questionNormalized, answerNormalized, diff);
        this.questionHeadWordIndex = questionHeadWordIndex;
        this.answerHeadWordIndex = answerHeadWordIndex;
        this.score = 0.0;
        this.scores = new ArrayList<>();
    }

    public L2RQA getPreviousQA() throws NoPreviousQAException {
        if (previousQA != -1) {
            SimpleQA simpleQA = LuceneManager.getSimpleQA(previousQA);
            return new L2RQA(simpleQA.getPreviousQA(),
                    simpleQA.getQuestion(), simpleQA.getAnswer(),
                    simpleQA.getNormalizedQuestion(), simpleQA.getNormalizedAnswer(),
                    questionHeadWordIndex, answerHeadWordIndex, simpleQA.getDiff());
        } else {
            throw new NoPreviousQAException(this);
        }
    }

    public double getScore() {
        return score;
    }

    public void addScore(double score, double weight) {
        this.score += score * weight;
        scores.add(score);
    }

    public long getDiff() {
        return diff;
    }

    public List<String> getQuestionListNormalized() {
        if (questionListNormalized != null) {
            return questionListNormalized;
        } else {
            String questionLemma = super.getNormalizedQuestion();
            return questionListNormalized = Arrays.asList(questionLemma.split("\\s+"));
        }
    }

    public List<String> getAnswerListNormalized() {
        if (answerListNormalized != null) {
            return answerListNormalized;
        } else {
            String answerLemma = super.getNormalizedAnswer();
            return answerListNormalized = Arrays.asList(answerLemma.split("\\s+"));
        }
    }

    public void printScores() {
        for (int i = 0; i < this.scores.size(); i++) {
            double d = this.scores.get(i);
            System.out.print(((i+1) + ":" + String.format("%.5f", d)).replace(",", "."));
            if (i+1 != this.scores.size()) {
                System.out.print(" ");
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(2 + " " + "qid:" + Main.qid++ + " ");
        int i = 0;
        for (; i < this.scores.size(); i++) {
            double d = this.scores.get(i);
            stringBuilder.append(((i + 1) + ":" + String.format("%.5f", d) + " ").replace(",", "."));
        }
        stringBuilder.append((i + 1) + ":" + (getAnswerListNormalized().size() > 8 ? 1 : String.format("%.5f", getAnswerListNormalized().size()/8.0 - 1/8.0).replace(",",".")) + " ");
        i++;
        int j = 0;
        for (; j < this.questionHeadWordIndex; j++) {
            stringBuilder.append((i+1) + ":0 ");
            i++;
        }
        if (questionHeadWordIndex != -1) {
            stringBuilder.append((i+1) + ":1 ");
            i++;
            j++;
        }
        for (; j < Main.questionHeadWords.length; j++) {
            stringBuilder.append((i + 1) + ":0 ");
            i++;
        }


        j = 0;
        for (; j < this.answerHeadWordIndex; j++) {
            stringBuilder.append((i+1) + ":0 ");
            i++;
        }
        if (answerHeadWordIndex != -1) {
            stringBuilder.append((i+1) + ":1 ");
            i++;
            j++;
        }
        for (; j < Main.answerHeadWords.length; j++) {
            stringBuilder.append((i + 1) + ":0 ");
            i++;
        }
        return stringBuilder.toString();
    }

    public int compareTo(L2RQA qa) {
        return super.compareTo(qa);
    }
}
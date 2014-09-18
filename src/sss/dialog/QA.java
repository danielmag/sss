package sss.dialog;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import org.apache.commons.lang3.StringUtils;
import sss.exceptions.dialog.NoPreviousQAException;
import sss.lucene.LuceneManager;

import java.text.Format;
import java.util.*;

public class QA extends BasicQA implements Comparable<QA> {

    private long previousQA;
    private double score;
    private ArrayList<Double> scores;
    private long diff;
    private List<String> questionListNormalized = null; //I am using null values to allow lazy initialization
    private List<String> answerListNormalized = null;

    public QA(long previousQA, String q, String a, String questionNormalized, String answerNormalized, long diff) {
        super(q, a, questionNormalized, answerNormalized);
        this.previousQA = previousQA;
        this.score = 0.0;
        this.scores = new ArrayList<>();
        this.diff = diff;
    }

    public QA getPreviousQA() throws NoPreviousQAException{
        if (previousQA != -1) {
            SimpleQA simpleQA = LuceneManager.getSimpleQA(previousQA);
            return new QA(simpleQA.getPreviousQA(),
                    simpleQA.getQuestion(), simpleQA.getAnswer(),
                    simpleQA.getNormalizedQuestion(), simpleQA.getNormalizedAnswer(),
                    simpleQA.getDiff());
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
            System.out.print(((i+1) + ":" + String.format("%.5f", d) + " ").replace(",", "."));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.scores.size(); i++) {
            double d = this.scores.get(i);
            stringBuilder.append(((i+1) + ":" + String.format("%.5f", d) + " ").replace(",", "."));
        }
        return stringBuilder.toString();
    }

    @Override
    public int compareTo(QA qa) {
        double compareScore = qa.getScore();

        //descending order
        return (int) Math.signum(compareScore - this.score);
    }
}
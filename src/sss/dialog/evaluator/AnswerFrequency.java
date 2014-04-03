package sss.dialog.evaluator;

import l2f.evaluator.distance.algorithms.jaccard.JaccardAlgorithm;
import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;
import sss.dialog.QA;

import java.util.List;

public class AnswerFrequency extends QaScorer {

    public AnswerFrequency(double weight) {
        super(weight);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        double[] scores = new double[qas.size()];
        for (int i = 0; i < qas.size(); i++) {
            for (int j = i + 1; j < qas.size(); j++) {
                QA qa1 = qas.get(i);
                QA qa2 = qas.get(j);
                JaccardAlgorithm jaccardAlgorithm = new JaccardAlgorithm(new RegularSetIntersection());
                double score = jaccardAlgorithm.distance(qa1.getAnswerListNormalized(), qa2.getAnswerListNormalized());
                scores[i] += score;
                scores[j] += score;
            }
        }
        double max = getMax(scores);
        scoreQas(qas, scores, max);
    }

    private double getMax(double[] scores) {
        double max = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > max) {
                max = scores[i];
            }
        }
        return max;
    }

    private void scoreQas(List<QA> qas, double[] scores, double max) {
        for (int i = 0; i < scores.length; i++) {
            double qaScore = (scores[i] / max) * super.getWeight();
            qas.get(i).addScore(qaScore);
        }
    }
}

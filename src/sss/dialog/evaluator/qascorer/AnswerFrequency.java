package sss.dialog.evaluator.qascorer;

import sss.dialog.QA;
import sss.distance.algorithms.DistanceAlgorithm;

import java.util.List;

public class AnswerFrequency extends QaScorer {

    public AnswerFrequency(double weight, DistanceAlgorithm distanceAlgorithm) {
        super(weight, distanceAlgorithm);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        double[] scores = new double[qas.size()];
        for (int i = 0; i < qas.size(); i++) {
            for (int j = i + 1; j < qas.size(); j++) {
                QA qa1 = qas.get(i);
                QA qa2 = qas.get(j);
                double score = getDistanceAlgorithm().distance(qa1.getAnswerListNormalized(), qa2.getAnswerListNormalized());
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
            double qaScore = (max == 0) ? 0 : ((scores[i] / max));
            super.scoreQA(qas.get(i), qaScore);
        }
    }
}

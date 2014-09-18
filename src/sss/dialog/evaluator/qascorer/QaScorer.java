package sss.dialog.evaluator.qascorer;

import sss.dialog.QA;
import sss.dialog.evaluator.Evaluator;
import sss.distance.algorithms.DistanceAlgorithm;

import java.util.List;

public abstract class QaScorer implements Evaluator {
    private double weight; //has to be between 0 and 100
    private DistanceAlgorithm distanceAlgorithm;

    protected QaScorer(double weight, DistanceAlgorithm distanceAlgorithm) {
        this.weight = weight;
        this.distanceAlgorithm = distanceAlgorithm;
    }

    public abstract void score(String userQuestion, List<QA> qas);

    protected double getWeight() {
        return this.weight;
    }

    public void scoreQA(QA qa, double qaScore) {
        qa.addScore(qaScore, getWeight());
    }

    protected DistanceAlgorithm getDistanceAlgorithm() {
        return distanceAlgorithm;
    }
}

package sss.distance.algorithms;

import l2f.evaluator.distance.algorithms.set.intersection.SetIntersection;

import java.util.List;

public class JaccardOverlapAlgorithm implements DistanceAlgorithm {
    private JaccardAlgorithm jaccarAlg;
    private OverlapAlgorithm overlapAlg;
    private double jaccardWeight;

    public JaccardOverlapAlgorithm(SetIntersection setInterstion, double jaccardWeight) {
        this.jaccarAlg = new JaccardAlgorithm(setInterstion);
        this.overlapAlg = new OverlapAlgorithm(setInterstion);
        this.jaccardWeight = jaccardWeight;
    }

    public double distance(List<String> wordSetA, List<String> wordSetB) {
        double score = this.jaccardWeight * this.jaccarAlg.distance(wordSetA, wordSetB) + (1.0D - this.jaccardWeight) * this.overlapAlg.distance(wordSetA, wordSetB);
        return score;
    }
}

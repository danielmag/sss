package sss.distance.algorithms;

import l2f.evaluator.distance.algorithms.NgramGenerator;
import l2f.evaluator.distance.algorithms.set.intersection.SetIntersection;

import java.util.LinkedList;
import java.util.List;

public class JaccardOverlapBigramAlgorithm implements DistanceAlgorithm {
    private JaccardAlgorithm jaccarAlg;
    private OverlapAlgorithm overlapAlg;
    private double jaccardWeight;

    public JaccardOverlapBigramAlgorithm(SetIntersection setInterstion, double jaccardWeight) {
        this.jaccarAlg = new JaccardAlgorithm(setInterstion);
        this.overlapAlg = new OverlapAlgorithm(setInterstion);
        this.jaccardWeight = jaccardWeight;
    }

    public double distance(List<String> wordSetA, List<String> wordSetB) {
        double jaccardScore = this.jaccarAlg.distance(wordSetA, wordSetB);
        double overlapScore = this.overlapAlg.distance(NgramGenerator.getNGrams(2, new LinkedList(wordSetA)), NgramGenerator.getNGrams(2, new LinkedList(wordSetB)));
        double score = this.jaccardWeight * jaccardScore + (1.0D - this.jaccardWeight) * overlapScore;
        return score;
    }
}

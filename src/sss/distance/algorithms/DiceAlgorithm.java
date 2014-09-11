package sss.distance.algorithms;

import l2f.evaluator.distance.algorithms.set.intersection.SetIntersection;

import java.util.List;

public class DiceAlgorithm implements DistanceAlgorithm {
    private SetIntersection setIntersection;

    public DiceAlgorithm(SetIntersection setIntersection) {
        this.setIntersection = setIntersection;
    }

    public double distance(List<String> wordSetA, List<String> wordSetB) {
        double aCount = wordSetA.size();
        double bCount = wordSetB.size();

        return 2.0D * this.setIntersection.intersection(wordSetA, wordSetB) / (aCount + bCount);
    }
}

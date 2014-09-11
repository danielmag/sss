package sss.distance.algorithms;

import l2f.evaluator.distance.algorithms.set.intersection.SetIntersection;
import java.util.List;

public class JaccardAlgorithm implements DistanceAlgorithm {
    private SetIntersection setInterstion;

    public JaccardAlgorithm(SetIntersection setInterstion) {
        this.setInterstion = setInterstion;
    }

    public double distance(List<String> wordSetA, List<String> wordSetB) {
        double aCount = wordSetA.size();
        double bCount = wordSetB.size();

        double cCount = this.setInterstion.intersection(wordSetA, wordSetB);
        return cCount / (aCount + bCount - cCount);
    }
}

package sss.distance.algorithms;

import java.util.List;
import l2f.evaluator.distance.algorithms.set.intersection.SetIntersection;

public class OverlapAlgorithm
        implements DistanceAlgorithm
{
    private SetIntersection setIntersection;

    public OverlapAlgorithm(SetIntersection setIntersection)
    {
        this.setIntersection = setIntersection;
    }

    public double distance(List<String> wordSetA, List<String> wordSetB)
    {
        double aCount = wordSetA.size();
        double bCount = wordSetB.size();
        double cCount = this.setIntersection.intersection(wordSetA, wordSetB);
        return cCount / Math.min(aCount, bCount);
    }
}

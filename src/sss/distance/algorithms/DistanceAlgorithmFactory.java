package sss.distance.algorithms;

import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;

public class DistanceAlgorithmFactory {

    public DistanceAlgorithm getDistanceAlgorithm(String distanceAlgorithm) {
        String[] split = distanceAlgorithm.split(",");
        switch (split[0]) {
            case "Dice":
                return new DiceAlgorithm(new RegularSetIntersection());
            case "Jaccard":
                return new JaccardAlgorithm(new RegularSetIntersection());
            case "Overlap":
                return new OverlapAlgorithm(new RegularSetIntersection());
            case "JaccardOverlap":
                return new JaccardOverlapAlgorithm(new RegularSetIntersection(), Double.parseDouble(split[1]));
            default:
                throw new RuntimeException("You have inserted a Distance Algorithm that does not exist");
        }
    }
}
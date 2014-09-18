package sss.dialog.evaluator;

import sss.dialog.QA;
import sss.distance.algorithms.DistanceAlgorithm;

import java.util.List;

public interface Evaluator {
    public void score(String userQuestion, List<QA> qas);

}

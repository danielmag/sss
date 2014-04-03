package sss.dialog.evaluator;

import l2f.evaluator.distance.algorithms.jaccard.JaccardAlgorithm;
import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;
import sss.dialog.QA;

import java.util.List;

public class SimpleTimeDifference extends QaScorer {

    public SimpleTimeDifference(double weight) {
        super(weight);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        for (QA qa : qas) {
            double score;
            long diff = qa.getDiff();
            if (diff == 0) {
                score = 1;
            } else if (diff <= 80) {
                score = 0.3;
            } else if (diff <= 1000)  {
                score = 0.9 - (diff/5000.0); // ranges from approximately 0.8838 to 0.7
            } else {
                score = 0.7 - (diff/2500.0); // ranges from 0.8 to 0
                if (score < 0) {
                    score = 0;
                }
            }
            assert(score >= 0);
            qa.addScore(score*super.getWeight());
        }
    }
}
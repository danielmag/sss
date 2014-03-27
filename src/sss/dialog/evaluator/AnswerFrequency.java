package sss.dialog.evaluator;

import l2f.evaluator.distance.algorithms.jaccard.JaccarOverlapAlgorithm;
import l2f.evaluator.distance.algorithms.jaccard.JaccardAlgorithm;
import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;
import sss.dialog.QA;

import java.util.List;

public class AnswerFrequency extends QaScorer {

    public AnswerFrequency(double weight) {
        super(weight);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        for (int i = 0; i < qas.size(); i++) {
            for (int j = i + 1; j < qas.size(); j++) {
                QA qa1 = qas.get(i);
                QA qa2 = qas.get(j);
                JaccardAlgorithm jaccardAlgorithm = new JaccardAlgorithm(new RegularSetIntersection());
                double score = jaccardAlgorithm.distance(qa1.getAnswerListNormalized(), qa2.getAnswerListNormalized());
                double qascore = (score/qas.size())*super.getWeight();
                qa1.addScore(qascore);
                qa2.addScore(qascore);
            }
        }
    }
}

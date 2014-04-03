package sss.dialog.evaluator;

import l2f.evaluator.distance.algorithms.jaccard.JaccardAlgorithm;
import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;
import sss.dialog.QA;

import java.util.Arrays;
import java.util.List;

public class AnswerSimilarityToUserQuestion extends QaScorer {

    public AnswerSimilarityToUserQuestion(double weight) {
        super(weight);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        List<String> tokenizedQuestion = Arrays.asList(userQuestion.split("\\s+"));
        for (QA qa : qas) {
            JaccardAlgorithm jaccardAlgorithm = new JaccardAlgorithm(new RegularSetIntersection());
            double score = jaccardAlgorithm.distance(tokenizedQuestion, qa.getAnswerListNormalized());
            qa.addScore(score*super.getWeight());
        }
    }
}

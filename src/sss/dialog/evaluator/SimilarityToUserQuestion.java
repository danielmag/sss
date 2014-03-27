package sss.dialog.evaluator;

import l2f.evaluator.distance.algorithms.jaccard.JaccarOverlapAlgorithm;
import l2f.evaluator.distance.algorithms.jaccard.JaccardAlgorithm;
import l2f.evaluator.distance.algorithms.overlap.OverlapAlgorithm;
import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;
import l2f.nlp.SimpleTokenizer;
import sss.dialog.QA;

import java.util.List;

public class SimilarityToUserQuestion extends QaScorer {

    public SimilarityToUserQuestion(double weight) {
        super(weight);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        SimpleTokenizer simpleTokenizer = new SimpleTokenizer();
        List<String> tokenizedQuestion = simpleTokenizer.tokenize(userQuestion);
        for (QA qa : qas) {
            JaccardAlgorithm jaccardAlgorithm = new JaccardAlgorithm(new RegularSetIntersection());
            double score = jaccardAlgorithm.distance(tokenizedQuestion, qa.getQuestionListNormalized());
            qa.addScore(score*super.getWeight());
        }
    }
}

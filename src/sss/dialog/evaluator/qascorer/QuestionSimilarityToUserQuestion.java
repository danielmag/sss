package sss.dialog.evaluator.qascorer;

import sss.dialog.QA;
import sss.distance.algorithms.DistanceAlgorithm;

import java.util.Arrays;
import java.util.List;

public class QuestionSimilarityToUserQuestion extends QaScorer {

    public QuestionSimilarityToUserQuestion(double weight, DistanceAlgorithm distanceAlgorithm) {
        super(weight, distanceAlgorithm);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        List<String> tokenizedQuestion = Arrays.asList(userQuestion.split("\\s+"));
        for (QA qa : qas) {
            double score = getDistanceAlgorithm().distance(tokenizedQuestion, qa.getQuestionListNormalized());
            super.scoreQA(qa, score);
        }
    }
}
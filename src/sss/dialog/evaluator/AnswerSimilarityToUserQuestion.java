package sss.dialog.evaluator;

import l2f.evaluator.distance.algorithms.jaccard.JaccardAlgorithm;
import l2f.evaluator.distance.algorithms.set.intersection.RegularSetIntersection;
import sss.dialog.QA;
import sss.texttools.StopWords;
import sss.texttools.normalizer.Normalizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AnswerSimilarityToUserQuestion extends QaScorer {

    public AnswerSimilarityToUserQuestion(double weight) {
        super(weight);
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        List<String> tokenizedQuestion = Arrays.asList(userQuestion.split("\\s+"));
        this.score(tokenizedQuestion, qas);
    }

    public void scoreWithoutStopWords(String userQuestion, List<QA> qas, String stopWordsLocation, List<Normalizer> normalizers) throws IOException {
        StopWords stopWords = new StopWords(stopWordsLocation, normalizers);
        List<String> tokenizedQuestion = Arrays.asList(userQuestion.split("\\s+"));
        List<String> tokenizedQuestionWithoutStopWords = stopWords.getStringListWithoutStopWords(tokenizedQuestion);
        if (!tokenizedQuestionWithoutStopWords.isEmpty()) {
            this.score(tokenizedQuestionWithoutStopWords, qas);
        } else {
            this.score(tokenizedQuestion, qas);
        }
    }

    private void score(List<String> tokenizedQuestion, List<QA> qas) {
        for (QA qa : qas) {
            JaccardAlgorithm jaccardAlgorithm = new JaccardAlgorithm(new RegularSetIntersection());
            double score = jaccardAlgorithm.distance(tokenizedQuestion, qa.getAnswerListNormalized());
            qa.addScore(score*super.getWeight());
        }
    }
}

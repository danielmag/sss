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
    private String stopWordsLocation;
    private List<Normalizer> normalizers;

    public AnswerSimilarityToUserQuestion(double weight, String stopWordsLocation, List<Normalizer> normalizers) {
        super(weight);
        this.stopWordsLocation = stopWordsLocation;
        this.normalizers = normalizers;
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        StopWords stopWords = null;
        try {
            stopWords = new StopWords(this.stopWordsLocation, this.normalizers);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

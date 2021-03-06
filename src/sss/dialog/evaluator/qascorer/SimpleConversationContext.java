package sss.dialog.evaluator.qascorer;

import sss.dialog.BasicQA;
import sss.dialog.QA;
import sss.distance.algorithms.DistanceAlgorithm;
import sss.exceptions.dialog.NoPreviousQAException;
import sss.lucene.LuceneManager;

import java.util.Arrays;
import java.util.List;

public class SimpleConversationContext extends QaScorer {
    private int nPreviousQAs;

    public SimpleConversationContext(double weight, int nPreviousQAs, DistanceAlgorithm distanceAlgorithm) {
        super(weight, distanceAlgorithm);
        this.nPreviousQAs = nPreviousQAs;
    }

    @Override
    public void score(String userQuestion, List<QA> qas) {
        for (QA qa : qas) {
            QA currentQA = qa;
            double totalScore = 0.0;
            for (int i = 0; i < nPreviousQAs; i++) {
                try {
                    BasicQA basicQA = LuceneManager.CONVERSATION.getNFromLastQA(i);
                    currentQA = currentQA.getPreviousQA();
                    List<String> tokenizedQuestion = Arrays.asList(basicQA.getNormalizedQuestion().split("\\s+"));
                    totalScore += getDistanceAlgorithm().distance(tokenizedQuestion, currentQA.getQuestionListNormalized());
                } catch (ArrayIndexOutOfBoundsException | NoPreviousQAException ex) {
                    break;
                }
            }
            super.scoreQA(qa, totalScore/nPreviousQAs);
        }
    }
}

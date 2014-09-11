package sss.dialog.evaluator;

import sss.distance.algorithms.DistanceAlgorithm;
import sss.texttools.normalizer.Normalizer;

import java.util.ArrayList;
import java.util.List;

public class QaScorerFactory {

    public List<QaScorer> createQaScorers(List<String> qaScorerStrings, String stopWordsLocation, List<Normalizer> normalizers, DistanceAlgorithm distanceAlgorithm) {
        List<QaScorer> qaScorers = new ArrayList<>();
        for (String s : qaScorerStrings) {
            String[] strings = s.split(",");
            switch (strings[0]) {
                case "AnswerFrequency":
                    qaScorers.add(new AnswerFrequency(Integer.parseInt(strings[1]) / 100f, distanceAlgorithm));
                    break;
                case "AnswerSimilarityToUserQuestion":
                    qaScorers.add(new AnswerSimilarityToUserQuestion(Integer.parseInt(strings[1]) / 100f, stopWordsLocation, normalizers, distanceAlgorithm));
                    break;
                case "QuestionSimilarityToUserQuestion":
                    qaScorers.add(new QuestionSimilarityToUserQuestion(Integer.parseInt(strings[1]) / 100f, distanceAlgorithm));
                    break;
                case "SimpleTimeDifference":
                    qaScorers.add(new SimpleTimeDifference(Integer.parseInt(strings[1]) / 100f, distanceAlgorithm));
                    break;
                case "SimpleConversationContext":
                    qaScorers.add(new SimpleConversationContext(Integer.parseInt(strings[1]) / 100f, Integer.parseInt(strings[2]), distanceAlgorithm));
                    break;
                default:
                    throw new RuntimeException("You have inserted a QaScorer that does not exist");
            }
        }

        return qaScorers;
    }
}

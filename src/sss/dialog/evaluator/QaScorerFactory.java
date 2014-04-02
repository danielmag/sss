package sss.dialog.evaluator;

import java.util.ArrayList;
import java.util.List;

public class QaScorerFactory {

    public List<QaScorer> createQaScorers(List<String> qaScorerStrings) {
        List<QaScorer> qaScorers = new ArrayList<>();
        for (String s : qaScorerStrings) {
            String[] strings = s.split(",");
            switch(strings[0]) {
                case "AnswerFrequency" : qaScorers.add(new AnswerFrequency(Integer.parseInt(strings[1])/100f)); break;
                case "AnswerSimilarityToUserQuestion" : qaScorers.add(new AnswerSimilarityToUserQuestion(Integer.parseInt(strings[1])/100f)); break;
                case "QuestionSimilarityToUserQuestion" : qaScorers.add(new QuestionSimilarityToUserQuestion(Integer.parseInt(strings[1])/100f)); break;
                case "SimpleTimeDifference" : qaScorers.add(new SimpleTimeDifference(Integer.parseInt(strings[1])/100f)); break;
                default: throw new RuntimeException("You have inserted a QaScorer that does not exist");
            }
        }

        return qaScorers;
    }
}

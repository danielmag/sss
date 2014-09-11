package sss.evaluatedtas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Daniel on 16/06/2014.
 */
public class Reader {
    private TAs<Evaluated> evaluatedTAs = new TAs<>();

    public Reader(String fileLoc) throws IOException {
        evaluatedTAs = loadEvaluatedAnswers(fileLoc);
    }

    private TAs<Evaluated> loadEvaluatedAnswers(String evaluatedLocation) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader((new File(evaluatedLocation)).getCanonicalPath()));
        String line = reader.readLine();
        TAs<Evaluated> evaluatedTAs = new TAs<>();
        while (line != null) {
            assert line.startsWith("T - ");
            String trigger = getSubstringAfterHyphen(line);
            evaluatedTAs.addTrigger(trigger);
            line = reader.readLine();
            while (line != null && line.startsWith("\tA - ")) {
                String answerWithEval = getSubstringAfterHyphen(line);
                String[] strings = splitLast(answerWithEval, ':');
                evaluatedTAs.addAnswer(trigger, new Evaluated(strings[0], getEvaluation(strings[1])));
                line = reader.readLine();
            }
        }
        return evaluatedTAs;
    }



    private String[] splitLast(String answerWithEval, char s) {
        String[] res = new String[2];
        res[0] = answerWithEval.substring(0, answerWithEval.lastIndexOf(s)).trim();
        res[1] = answerWithEval.substring(answerWithEval.lastIndexOf(s) + 1, answerWithEval.length()).trim();
        return res;
    }

    private Evaluation getEvaluation(String s) {
        switch (s) {
            case "y": return Evaluation.GOOD;
            case "m": return Evaluation.MAYBE;
            case "n": return Evaluation.BAD;
            default : throw new RuntimeException();
        }
    }

    private String getSubstringAfterHyphen(String temp) {
        return temp.substring(temp.indexOf('-') + 2, temp.length());
    }

    public TAs<Evaluated> getEvaluatedTAs() {
        return evaluatedTAs;
    }
}

package sss.evaluatedtas;

import java.util.*;

/**
 * Created by Daniel on 28/05/2014.
 */
public class TAs<T extends Answer> {
    private Map<String, List<T>> tas;

    public TAs() {
        this.tas = new HashMap<>();
    }

    public void addTrigger(String t) {
        if (this.tas.containsKey(t)) {
            return;
        } else {
            this.tas.put(t, new LinkedList<T>());
        }
    }

    public void addAnswer(String t, T a) {
        if (this.tas.containsKey(t)) {
            if (!this.tas.get(t).contains(a)) {
                this.tas.get(t).add(a);
            }
        } else {
            throw new RuntimeException();
        }
    }

    public String getAnswerEvaluation(String trigger, String answer) {
        List<T> answers = this.tas.get(trigger);
        if (answer.startsWith("No, l") && answer.endsWith("m sorry.") && answer.length() == "No, l?m sorry.".length()) {
            answer = "No, l'm sorry.";
        }
        if (answer.startsWith("Couple o' stagecoaches over by Oletha, a few stray travellers a little east of here, caf")) {
            answer = "Couple o' stagecoaches over by Oletha, a few stray travellers a little east of here, café down in Mansfield, and then this.";
        }
        if (answer.endsWith(" You don't know") && answer.length() == "? You don't know".length()) {
            answer = "You don't know";
        }

        try {
            String ret = ((Evaluated)answers.get(answers.indexOf(new Evaluated(answer, Evaluation.GOOD)))).evalToString();
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("T - " + trigger + "\n" +
                "A - " + answer);
        }
    }
}

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
            answer = "Couple o' stagecoaches over by Oletha, a few stray travellers a little east of here, cafe down in Mansfield, and then this.";
        }
        if (answer.endsWith(" You don't know") && answer.length() == "? You don't know".length()) {
            answer = "You don't know";
        }
        if (answer.startsWith("Sit down l") && answer.endsWith("m sorry") && answer.length() == "Sit down l?ve ruined your evening lt was a slip of the tongue, l?m sorry".length()) {
            answer = "Sit down l've ruined your evening lt was a slip of the tongue, l'm sorry";
        }
        if (answer.startsWith("Are you trying to tell me if I don") && answer.endsWith("t love you you'll slobber over me like a hog... ... why you gonna do something about it?")) {
            answer = "Are you trying to tell me if I dont love you you'll slobber over me like a hog... ... why you gonna do something about it?";
        }
        if (answer.startsWith("I'm a videograp") && answer.endsWith("her.")) {
            answer = "I'm a videograpµher.";
        }
        if (answer.startsWith("Don") && answer.endsWith("t call me stupid.")) {
            answer = "Dont call me stupid.";
        }/*
        if (answer.endsWith(" le escribiste?") && answer.length() == "iQu� le escribiste?".length()) {
            answer = "iQué le escribiste?";
        }
        if (answer.startsWith("You don't even know who Warren is. Oh, he's my fianc") && answer.endsWith(".")) {
            answer = "You don't even know who Warren is. Oh, he's my fiancé.";
        }
        if (answer.startsWith("The Caf") && answer.endsWith(", sir? Just a moment, please.")) {
            answer = "The Café, sir? Just a moment, please.";
        }*/
        try {
            String ret = ((Evaluated)answers.get(answers.indexOf(new Evaluated(answer, Evaluation.GOOD)))).evalToString();
            return ret;
        } catch (Exception e) {
            throw new RuntimeException("T - " + trigger + "\n" +
                "A - " + answer);
        }
    }
}

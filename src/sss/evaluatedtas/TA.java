package sss.evaluatedtas;

/**
 * Created by Daniel on 28/05/2014.
 */
public class TA {
    private String trigger;
    private Answer answer;

    public TA(String trigger, Answer answer) {
        this.trigger = trigger;
        this.answer = answer;
    }

    public String getTrigger() {
        return trigger;
    }

    public Answer getAnswer() {
        return answer;
    }

    @Override
    public String toString() {
        return "trigger - " + trigger + "\n" +
                "answer - " + answer.toString() + "\n";
    }
}

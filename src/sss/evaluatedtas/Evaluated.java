package sss.evaluatedtas;

/**
 * Created by Daniel on 28/05/2014.
 */
public class Evaluated extends Answer {
    private Evaluation eval;

    protected Evaluated(String answer, Evaluation eval) {
        super(answer);
        this.eval = eval;
    }

    @Override
    public String toString() {
        return super.toString() + " : " + evalToString(eval);
    }

    private String evalToString(Evaluation eval) {
        switch (eval) {
            case GOOD : return "y";
            case MAYBE: return "m";
            case BAD : return "n";
            default : throw new RuntimeException();
        }
    }

    public String evalToString() {
        switch (eval) {
            case GOOD : return "2";
            case MAYBE: return "1";
            case BAD : return "0";
            default : throw new RuntimeException();
        }
    }
}

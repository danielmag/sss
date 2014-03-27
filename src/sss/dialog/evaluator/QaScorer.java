package sss.dialog.evaluator;

import sss.dialog.QA;
import sss.exceptions.dialog.evaluator.WeightException;

import java.math.BigDecimal;
import java.util.List;

public abstract class QaScorer {
    private double weight; //has to be between 0 and 100

    protected QaScorer(double weight) {
        if(weight < 0.0 || weight > 1.0) { //TODO: transfer to config parser
            throw new WeightException();
        }
        this.weight = weight;
    }

    public abstract void score(String userQuestion, List<QA> qas);

    protected double getWeight() {
        return weight;
    }
}

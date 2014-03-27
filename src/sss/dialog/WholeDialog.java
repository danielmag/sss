package sss.dialog;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Daniel on 24-02-2014.
 */
public class WholeDialog implements Serializable {
    private ArrayList<SimpleQA> simpleQAs;

    public WholeDialog() {
        simpleQAs = new ArrayList<>();
    }

    public SimpleQA getSimpleQA(int i) {
        return simpleQAs.get(i);
    }

    public void addSimpleQA(SimpleQA simpleQA) {
        this.simpleQAs.add(simpleQA);
    }
}

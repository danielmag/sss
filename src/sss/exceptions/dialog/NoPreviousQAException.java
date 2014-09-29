package sss.exceptions.dialog;

import sss.dialog.L2RQA;
import sss.dialog.QA;

public class NoPreviousQAException extends Exception {
    public NoPreviousQAException(QA qa) {
        super("QA: Q - " + qa.getQuestion() + "\n" +  "A - " + qa.getAnswer() + "\n"
                + "has no previous QA");
    }
}

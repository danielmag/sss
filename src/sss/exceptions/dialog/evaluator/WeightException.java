package sss.exceptions.dialog.evaluator;

public class WeightException extends RuntimeException {
    public WeightException() {
        super("All weights need to be between 0 and 100 and their sum must amount to 100\n" +
                "Please fix this in the configuration file");
    }
}

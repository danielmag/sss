package sss.exceptions.dialog.evaluator;

public class WeightException extends RuntimeException {
    public WeightException() {
        super("All weights need to be between 0.0 and 1.0 and their sum must be 1.0\n" +
                "Please fix this in the configuration file");
    }
}

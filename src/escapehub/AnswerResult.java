package escapehub;

/** Το αποτέλεσμα μιας απάντησης: τι έγινε και ένα μήνυμα για τον παίκτη. */
public class AnswerResult {
    public enum Outcome {
        SOLVED, // ο γρίφος λύθηκε
        PARTIAL, // σωστό βήμα/κομμάτι, αλλά ο γρίφος δεν τελείωσε
        WRONG, // λάθος (μετράει ποινή)
        LOCKED, // ο γρίφος είναι κλειδωμένος
        ALREADY_SOLVED // έχει ήδη λυθεί
    }
    private final Outcome outcome;
    private final String message;
    private AnswerResult(Outcome outcome, String message) {
        this.outcome = outcome;
        this.message = message;
    }
    public static AnswerResult solved(String message) {
        return new AnswerResult(Outcome.SOLVED, message);
    }
    public static AnswerResult partial(String message) {
        return new AnswerResult(Outcome.PARTIAL, message);
    }
    public static AnswerResult wrong(String message) {
        return new AnswerResult(Outcome.WRONG, message);
    }
    public static AnswerResult locked(String message) {
        return new AnswerResult(Outcome.LOCKED, message);
    }
    public static AnswerResult alreadySolved(String message) {
        return new AnswerResult(Outcome.ALREADY_SOLVED, message);
    }
    public Outcome getOutcome() {
        return outcome;
    }
    public String getMessage() {
        return message;
    }
}

package escapehub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Γρίφος σειράς: τα γεγονότα δίνονται ένα-ένα με τη σωστή σειρά.
 * Η πρόοδος είναι κοινή για την ομάδα (αποθηκεύεται στο GameSession).
 * Σε λάθος βήμα, η σειρά ξεκινά από την αρχή.
 */
public class SequencePuzzle extends Puzzle {
    private final List<String> steps; // με τη σωστή σειρά
    public SequencePuzzle(String id, String pathId, String title, String question,
                          List<String> requires, String rewardEvidence, boolean finalStep,
                          List<String> steps) {
        super(id, pathId, title, question, requires, rewardEvidence, finalStep);
        this.steps = new ArrayList<>(steps);
    }
    @Override
    public AnswerResult check(GameSession session, String username, String answer) {
        int done = session.getSequenceProgress(getId());
        String expected = steps.get(done);
        if (!AnswerNormalizer.matches(answer, expected)) {
            session.setSequenceProgress(getId(), 0);
            ;
            return AnswerResult.wrong("Λάθος σειρά. Η σειρά ξεκινά από την αρχή.");
        }
        done++;
        if (done == steps.size()) {
            session.setSequenceProgress(getId(), done);
            return AnswerResult.solved("Σωστή σειρά! Η επιστολή συμπληρώθηκε.");
        }
        session.setSequenceProgress(getId(), done);
        return AnswerResult.partial("Σωστό βήμα (" + done + "/" + steps.size() + "). Συνέχισε.");
    }
    /** Τα βήματα αλφαβητικά, ώστε να μη φαίνεται η σωστή σειρά. */
    @Override
    public List<String> options() {
        List<String> sorted = new ArrayList<>(steps);
        Collections.sort(sorted);
        return sorted;
    }
    @Override
    public String typeName() {
        return "σωστή σειρά";
    }
    public List<String> getSteps() {
        return new ArrayList<>(steps);
    }
}

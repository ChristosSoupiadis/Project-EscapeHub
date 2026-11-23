
package escapehub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ελέγχει ότι μια αποστολή είναι σωστή πριν ξεκινήσει το πρόγραμμα.
 * Μαζεύει ΟΛΑ τα λάθη και τα δείχνει μαζί.
 */
public class MissionValidator {

    private MissionValidator() {
    }

    public static void validate(Mission m) {
        List<String> errors = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        int finals = 0;

        if (m.getPaths().isEmpty()) {
            errors.add("δεν υπάρχει καμία διαδρομή");
        }

        for (Puzzle p : m.getPuzzles()) {
            String where = "γρίφος " + p.getId() + ": ";

            if (!ids.add(p.getId())) {
                errors.add(where + "διπλό id");
            }

            if (p.getPathId() != null
                    && m.getPath(p.getPathId()) == null) {
                errors.add(where + "άγνωστη διαδρομή "
                        + p.getPathId());
            }

            for (String req : p.getRequires()) {
                if (m.getEvidence(req) == null) {
                    errors.add(where + "απαιτεί άγνωστο στοιχείο "
                            + req);
                }
            }

            if (p.getRewardEvidence() != null
                    && m.getEvidence(p.getRewardEvidence()) == null) {
                errors.add(where + "δίνει άγνωστο στοιχείο "
                        + p.getRewardEvidence());
            }

            if (p.isFinalStep()) {
                finals++;
            }

            checkType(p, where, errors);
        }

        if (finals != 1) {
            errors.add("πρέπει να υπάρχει ακριβώς ένας τελικός γρίφος "
                    + "(βρέθηκαν " + finals + ")");
        }

        if (!errors.isEmpty()) {
            throw new IllegalStateException(
                    "Αποστολή " + m.getId() + ":\n - "
                            + String.join("\n - ", errors));
        }
    }

    /** Έλεγχοι που εξαρτώνται από τον τύπο του γρίφου. */
    private static void checkType(
            Puzzle p,
            String where,
            List<String> errors) {

        if (p instanceof ExternalDataPuzzle ext) {

            if (ext.getAcceptedAnswers().isEmpty()) {
                errors.add(where
                        + "δεν έχει αποδεκτές απαντήσεις (snapshot)");
            }

            if (!ext.getSource().equals("wikidata")
                    && !ext.getSource().equals("openlibrary")) {
                errors.add(where + "άγνωστη πηγή "
                        + ext.getSource());
            }

        } else if (p instanceof TextPuzzle text) {

            if (text.getAcceptedAnswers().isEmpty()) {
                errors.add(where + "δεν έχει απάντηση");
            }

        } else if (p instanceof SequencePuzzle seq) {

            List<String> steps = seq.getSteps();

            if (steps.size() < 2) {
                errors.add(where + "χρειάζονται τουλάχιστον 2 βήματα");
            }

            List<String> sorted = new ArrayList<>(steps);
            Collections.sort(sorted);

            if (sorted.equals(steps)) {
                errors.add(where
                        + "η σωστή σειρά είναι ίδια με την αλφαβητική "
                        + "(ο γρίφος θα λυνόταν μόνος του)");
            }

        } else if (p instanceof CollaborativePuzzle col) {

            if (col.getRequiredContributors() < 2) {
                errors.add(where
                        + "η συνεργασία θέλει τουλάχιστον 2 μέλη");
            }

            if (col.getParts().size()
                    < col.getRequiredContributors()) {
                errors.add(where
                        + "λιγότερα κομμάτια από τα απαιτούμενα μέλη");
            }
        }
    }
}

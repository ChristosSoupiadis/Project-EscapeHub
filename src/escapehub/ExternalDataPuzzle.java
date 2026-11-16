package escapehub;
import java.util.List;
/**
 * Γρίφος που λύνεται ψάχνοντας σε δημόσια πηγή (Wikidata ή Open Library).
 *
 * Η απάντηση ΔΕΝ ελέγχεται με ζωντανή κλήση στο API. Οι αποδεκτές απαντήσεις
 * είναι γραμμένες στο αρχείο της αποστολής («snapshot»), άρα είναι πάντα ίδιες (R7)
 * και το παιχνίδι δουλεύει ακόμα κι αν πέσει το internet.
 */
public class ExternalDataPuzzle extends TextPuzzle {
    private final String source; // "wikidata" ή "openlibrary"
    public ExternalDataPuzzle(String id, String pathId, String title, String question,
                              List<String> requires, String rewardEvidence, boolean finalStep,
                              List<String> acceptedAnswers, String source) {
        super(id, pathId, title, question, requires, rewardEvidence, finalStep, acceptedAnswers);
        this.source = source;
    }
    @Override
    public String typeName() {
        return "ψάξε στο " + (source.equals("wikidata") ? "Wikidata" : "Open Library") + " (με την
        Αναζήτηση)";
    }
    public String getSource() {
        return source;
    }
}

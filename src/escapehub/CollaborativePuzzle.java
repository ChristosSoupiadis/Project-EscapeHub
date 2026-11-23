package escapehub;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Γρίφος συνεργασίας (R5): η απάντηση έχει κομμάτια και κάθε κομμάτι
 * πρέπει να το δώσει διαφορετικό μέλος της ομάδας.
 * Η απάντηση έρχεται ως "αριθμός_κομματιού τιμή", π.χ. "1 Shelley".
 */
public class CollaborativePuzzle extends Puzzle {
    private final List<String> parts;
    private final int requiredContributors;
    public CollaborativePuzzle(String id, String pathId, String title, String question,
                               List<String> requires, String rewardEvidence, boolean finalStep,
                               List<String> parts, int requiredContributors) {
        super(id, pathId, title, question, requires, rewardEvidence, finalStep);
        this.parts = new ArrayList<>(parts);
        this.requiredContributors = requiredContributors;
    }
    @Override
    public AnswerResult check(GameSession session, String username, String answer) {
        String[] pieces = answer.trim().split("\\s+", 2);
        int partNumber;
        try {
            partNumber = Integer.parseInt(pieces[0]);
        } catch (NumberFormatException e) {
            return AnswerResult.partial("Δώσε πρώτα τον αριθμό του κομματιού.");
        }

        if (partNumber < 1 || partNumber > parts.size() || pieces.length < 2) {
            return AnswerResult.partial("Άκυρο κομμάτι.");
        }
// κλειδί = αριθμός κομματιού, τιμή = ποιος το συμπλήρωσε
        Map<Integer, String> contributions = session.getContributions(getId());
        if (contributions.containsValue(username)) {
            return AnswerResult.partial("Έχεις ήδη συμπληρώσει ένα κομμάτι. Το επόμενο πρέπει να το δώσει άλλο μέλος.");
        }
        if (contributions.containsKey(partNumber)) {
            return AnswerResult.partial("Αυτό το κομμάτι έχει ήδη συμπληρωθεί σωστά.");
        }
        if (!AnswerNormalizer.matches(pieces[1], parts.get(partNumber - 1))) {
            return AnswerResult.wrong("Λάθος τιμή για το κομμάτι " + partNumber + ".");
        }
        contributions.put(partNumber, username);
        Set<String> people = new HashSet<>(contributions.values());
        if (contributions.size() == parts.size() && people.size() >= requiredContributors) {
            return AnswerResult.solved("Όλα τα κομμάτια είναι σωστά. Το αρχείο ανοίγει!");
        }
        int left = parts.size() - contributions.size();
        return AnswerResult.partial("Σωστό! Λείπουν ακόμα " + left + " κομμάτια από άλλα μέλη.");
    }
    @Override
    public String typeName()
    {
        return "συνεργασία: χρειάζονται " + requiredContributors + " διαφορετικά μέλη";
    }
    public List<String> getParts() {
        return new ArrayList<>(parts);
    }
    public int getRequiredContributors() {
        return requiredContributors;
    }
}

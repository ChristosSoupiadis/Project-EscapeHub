package escapehub;
import java.util.ArrayList;
import java.util.List;
/**
 * Βασική (abstract) κλάση για όλους τους γρίφους.
 * Κάθε τύπος γρίφου είναι υποκλάση και υλοποιεί με τον δικό του τρόπο τη μέθοδο check (πολυμορφισμός).
 */
public abstract class Puzzle {
    private final String id;
    private final String pathId; // null για τον τελικό γρίφο
    private final String title;
    private final String question;
7
    private final List<String> requires; // ποια στοιχεία πρέπει να έχει η ομάδα
    private final String rewardEvidence; // ποιο στοιχείο παίρνει όταν τον λύσει
    private final boolean finalStep;

    protected Puzzle(String id, String pathId, String title, String question,
                     List<String> requires, String rewardEvidence, boolean finalStep) {
        this.id = id;
        this.pathId = pathId;
        this.title = title;
        this.question = question;
        this.requires = new ArrayList<>(requires);
        this.rewardEvidence = rewardEvidence;
        this.finalStep = finalStep;
    }

    /**
     * Ελέγχει μια απάντηση. Κάθε υποκλάση έχει τη δική της λογική.
     */
    public abstract AnswerResult check(GameSession session, String username, String answer);

    /**
     * Όνομα του τύπου για εμφάνιση στον χρήστη.
     */
    public abstract String typeName();

    /**
     * Επιλογές προς εμφάνιση (μόνο ο γρίφος σειράς έχει).
     */
    public List<String> options() {
        return new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getPathId() {
        return pathId;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getRequires() {
        return new ArrayList<>(requires);
    }

    public String getRewardEvidence() {
        return rewardEvidence;
    }

    public boolean isFinalStep() {
        return finalStep;
    }
}
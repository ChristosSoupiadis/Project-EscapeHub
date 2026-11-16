package escapehub;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Η κοινή κατάσταση μιας ομάδας σε μια αποστολή (R2, R4).
 * Όλα τα μέλη βλέπουν και αλλάζουν το ίδιο αντικείμενο.
 */
public class GameSession implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String teamCode;
    private final String missionId;
    private final long startedAt;
    private long completedAt = 0; // 0 = δεν έχει ολοκληρωθεί
    private int wrongAnswers = 0;
    private final Set<String> solvedPuzzles = new HashSet<>();
    private final List<FoundEvidence> foundEvidence = new ArrayList<>();
    private final Map<String, Integer> sequenceProgress = new HashMap<>();
    private final Map<String, Map<Integer, String>> contributions = new HashMap<>();
    public GameSession(String teamCode, String missionId, long startedAt) {
        this.teamCode = teamCode;
        this.missionId = missionId;
        this.startedAt = startedAt;
    }
    /** Το κλειδί με το οποίο αποθηκεύεται: μία συνεδρία ανά ομάδα και αποστολή. */
    public static String key(String teamCode, String missionId) {
        return teamCode + "/" + missionId;
    }
    // ---- γρίφοι ----
    public boolean isSolved(String puzzleId) {
        return solvedPuzzles.contains(puzzleId);
    }
    public void markSolved(String puzzleId) {
        solvedPuzzles.add(puzzleId);
    }
    public int getSolvedCount() {
        return solvedPuzzles.size();
    }
    // ---- στοιχεία (κοινό ημερολόγιο) ----
    public boolean hasEvidence(String evidenceId) {
        for (FoundEvidence f : foundEvidence) {
            if (f.getEvidenceId().equals(evidenceId)) {
                return true;
            }
        }
        return false;
    }
    /** Προσθέτει στοιχείο μόνο αν δεν υπάρχει ήδη. */
    public void addEvidence(String evidenceId, String foundBy, long time) {
        if (!hasEvidence(evidenceId)) {
            foundEvidence.add(new FoundEvidence(evidenceId, foundBy, time));
        }
    }
    public List<FoundEvidence> getFoundEvidence() {
        return new ArrayList<>(foundEvidence);
    }
    // ---- γρίφος σειράς ----
    public int getSequenceProgress(String puzzleId) {
        return sequenceProgress.getOrDefault(puzzleId, 0);
    }
    public void setSequenceProgress(String puzzleId, int steps) {
        sequenceProgress.put(puzzleId, steps);
    }
// ---- γρίφος συνεργασίας ----
/** Επιστρέφει τον πραγματικό χάρτη (όχι αντίγραφο), ώστε ο γρίφος να προσθέτει συνεισφορές. */
public Map<Integer, String> getContributions(String puzzleId) {
    return contributions.computeIfAbsent(puzzleId, k -> new HashMap<>());
}
    // ---- λάθη και χρόνος ----
    public void addWrongAnswer() {
        wrongAnswers++;
    }
    public int getWrongAnswers() {
        return wrongAnswers;
    }
    public boolean isCompleted() {
        return completedAt != 0;
    }
    public void markCompleted(long time) {
        if (completedAt == 0) { // ο χρόνος ολοκλήρωσης δεν αλλάζει μετά
            completedAt = time;
        }
    }
    public String getTeamCode() {
        return teamCode;
    }
    public String getMissionId() {
        return missionId;
    }
    public long getStartedAt() {
        return startedAt;
    }
    public long getCompletedAt() {
        return completedAt;
    }
}
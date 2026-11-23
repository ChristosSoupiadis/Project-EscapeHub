package escapehub;
import java.util.ArrayList;
import java.util.List;
/**
 * Όλοι οι κανόνες ξεκλειδώματος σε ένα σημείο:
 * - λυμένος γρίφος → SOLVED
 * - λείπει κάποιο απαιτούμενο στοιχείο → LOCKED
 * - αλλιώς → AVAILABLE
 */
public class RuleEngine {
    private RuleEngine() {
// μόνο static μέθοδοι
    }
    public static PuzzleStatus status(Puzzle puzzle, GameSession session) {
        if (session.isSolved(puzzle.getId())) {
            return PuzzleStatus.SOLVED;
        }
        if (!missing(puzzle, session).isEmpty()) {
            return PuzzleStatus.LOCKED;
        }
        return PuzzleStatus.AVAILABLE;
    }
    /** Ποια στοιχεία λείπουν για να ανοίξει ο γρίφος. */
    public static List<String> missing(Puzzle puzzle, GameSession session) {
        List<String> result = new ArrayList<>();
        for (String evidenceId : puzzle.getRequires()) {
            if (!session.hasEvidence(evidenceId)) {
                result.add(evidenceId);
            }
        }
        return result;
    }
}

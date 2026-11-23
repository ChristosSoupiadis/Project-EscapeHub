package escapehub;

import java.util.ArrayList;
import java.util.List;

/**
 * Η «καρδιά» του παιχνιδιού: έναρξη/συνέχεια συνεδρίας και έλεγχος απαντήσεων.
 * Η κατάσταση διαβάζεται πάντα από το GameSession, ποτέ από αυτό που λέει ο παίκτης,
 * οπότε κανείς δεν μπορεί να λύσει κλειδωμένο γρίφο (R3).
 */
public class GameService {

    private final AppData data;

    public GameService(AppData data) {
        this.data = data;
    }

    /** Αν η ομάδα έχει ήδη ξεκινήσει την αποστολή, επιστρέφει την ίδια συνεδρία (R2). */
    public GameSession startOrResume(Team team, Mission mission, String username) {
        if (!team.isMember(username)) {
            throw new IllegalStateException("Δεν είσαι μέλος της ομάδας " + team.getName() + ".");
        }
        String key = GameSession.key(team.getInviteCode(), mission.getId());
        GameSession session = data.getSessions().get(key);
        if (session == null) {
            session = new GameSession(team.getInviteCode(), mission.getId(), System.currentTimeMillis());
            data.getSessions().put(key, session);
            System.out.println("Νέα συνεδρία για την ομάδα " + team.getName() + ".");
        } else {
            System.out.println("Συνέχεια από εκεί που σταματήσατε.");
        }
        return session;
    }

    public AnswerResult answer(GameSession session, Mission mission, String puzzleId, String username, String answer) {
        Puzzle puzzle = mission.getPuzzle(puzzleId);
        if (puzzle == null) {
            throw new IllegalArgumentException("Δεν υπάρχει γρίφος " + puzzleId);
        }

        // 1. Έλεγχος κατάστασης ΠΡΙΝ από την απάντηση
        PuzzleStatus status = RuleEngine.status(puzzle, session);
        if (status == PuzzleStatus.SOLVED) {
            return AnswerResult.alreadySolved("Ο γρίφος έχει ήδη λυθεί.");
        }
        if (status == PuzzleStatus.LOCKED) {
            List<String> titles = new ArrayList<>();
            for (String id : RuleEngine.missing(puzzle, session)) {
                titles.add(mission.getEvidence(id).getTitle());
            }
            return AnswerResult.locked("Ο γρίφος είναι κλειδωμένος. Λείπουν: " + titles);
        }

        // 2. Ο ίδιος ο γρίφος ελέγχει την απάντηση (πολυμορφισμός)
        AnswerResult result = puzzle.check(session, username, answer);

        // 3. Ενημέρωση κατάστασης
        if (result.getOutcome() == AnswerResult.Outcome.SOLVED) {
            long now = System.currentTimeMillis();
            session.markSolved(puzzle.getId());
            if (puzzle.getRewardEvidence() != null) {
                session.addEvidence(puzzle.getRewardEvidence(), username, now);
            }
            if (puzzle.isFinalStep()) {
                session.markCompleted(now);
            }
        } else if (result.getOutcome() == AnswerResult.Outcome.WRONG) {
            session.addWrongAnswer();
        }
        return result;
    }
}
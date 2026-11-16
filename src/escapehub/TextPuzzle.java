package escapehub;
import java.util.ArrayList;
import java.util.List;
/** Γρίφος με γραπτή απάντηση. Δέχεται μία ή περισσότερες σωστές μορφές της απάντησης. */
public class TextPuzzle extends Puzzle {
    private final List<String> acceptedAnswers;
8
    public TextPuzzle(String id, String pathId, String title, String question,
                      List<String> requires, String rewardEvidence, boolean finalStep,
                      List<String> acceptedAnswers) {
        super(id, pathId, title, question, requires, rewardEvidence, finalStep);
        this.acceptedAnswers = new ArrayList<>(acceptedAnswers);
    }
    @Override
    public AnswerResult check(GameSession session, String username, String answer) {
        for (String accepted : acceptedAnswers) {
            if (AnswerNormalizer.matches(answer, accepted)) {
                return AnswerResult.solved("Σωστό!");
            }
        }
        return AnswerResult.wrong("Λάθος απάντηση.");
    }
    @Override
    public String typeName() {
        return "απάντηση με κείμενο";
    }
    public List<String> getAcceptedAnswers() {
        return new ArrayList<>(acceptedAnswers);
    }
}
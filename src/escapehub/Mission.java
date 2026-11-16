package escapehub;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Μια αποστολή, όπως διαβάστηκε από αρχείο κειμένου.
 * Περιέχει διαδρομές, στοιχεία (evidence) και γρίφους.
 19
 */
public class Mission {
    private final String id;
    private final String title;
    private final String description;
    private final int penaltySeconds;
    private final Map<String, MissionPath> paths = new LinkedHashMap<>();
    private final Map<String, Evidence> evidence = new LinkedHashMap<>();
    private final List<Puzzle> puzzles = new ArrayList<>();
    public Mission(String id, String title, String description, int penaltySeconds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.penaltySeconds = penaltySeconds;
    }
    public void addPath(MissionPath path) {
        if (paths.containsKey(path.getId())) {
            throw new IllegalStateException("Διπλή διαδρομή: " + path.getId());
        }
        paths.put(path.getId(), path);
    }
    public void addEvidence(Evidence e) {
        if (evidence.containsKey(e.getId())) {
            throw new IllegalStateException("Διπλό στοιχείο: " + e.getId());
        }
        evidence.put(e.getId(), e);
    }
    public void addPuzzle(Puzzle p) {
        puzzles.add(p);
    }
    public Puzzle getPuzzle(String puzzleId) {
        for (Puzzle p : puzzles) {
            if (p.getId().equals(puzzleId)) {
                return p;
            }
        }
        return null;
    }
    public Puzzle getFinalPuzzle() {
        for (Puzzle p : puzzles) {
            if (p.isFinalStep()) {
                return p;
            }
        }
        return null;
    }
    public MissionPath getPath(String pathId) {
        return paths.get(pathId);
    }
    public Evidence getEvidence(String evidenceId) {
        return evidence.get(evidenceId);
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public int getPenaltySeconds() {
        return penaltySeconds;
    }
    public List<MissionPath> getPaths() {
        return new ArrayList<>(paths.values());
    }
    public List<Evidence> getAllEvidence() {
        return new ArrayList<>(evidence.values());
    }
    public List<Puzzle> getPuzzles() {
        return new ArrayList<>(puzzles);
    }
    @Override
    public String toString() {
        return title + " (" + paths.size() + " διαδρομές, " + puzzles.size() + " γρίφοι)";
    }
}

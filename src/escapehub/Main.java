package escapehub;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * Το σημείο εκκίνησης του προγράμματος (η μοναδική main).
 * Εδώ υπάρχει μόνο το μενού της κονσόλας: διαβάζει τι πληκτρολογεί ο χρήστης
 * και καλεί τις κλάσεις service. Η λογική του παιχνιδιού είναι στις άλλες κλάσεις.
 *
 * ΕΚΔΟΣΗ ΕΒΔΟΜΑΔΑΣ 2: ομάδες, παιχνίδι στην κονσόλα, πρόοδος, αναζήτηση βιβλίων.
 * Την εβδομάδα 3 προστίθενται η αναζήτηση στο Wikidata και ο browser.
 */
public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static DataStore store;
    private static AppData data;
    private static MissionCatalog missions;
    private static UserService userService;
    private static TeamService teamService;
    private static GameService gameService;
    private static OpenLibraryClient openLibrary;
    private static User currentUser = null;
    public static void main(String[] args) {
        // 1. Φόρτωση αποστολών. Αν κάποιο αρχείο είναι λάθος, το πρόγραμμα σταματά εδώ.
        try {
            missions = new MissionCatalog(MissionLoader.loadFolder("missions"));
        } catch (IllegalStateException e) {
            System.out.println("Λάθος στα αρχεία αποστολών:\n" + e.getMessage());
            return;
        }
// 2. Φόρτωση αποθηκευμένων δεδομένων (χρήστες, ομάδες, συνεδρίες).
        store = new DataStore("data/escapehub.dat");
        data = store.load();
// 3. Δημιουργία των services.
        userService = new UserService(data);
        teamService = new TeamService(data);
        gameService = new GameService(data);
        openLibrary = new OpenLibraryClient(new HttpHelper());
        System.out.println("=== EscapeHub (εβδομάδα 2) ===");
        System.out.println("Φορτώθηκαν " + missions.all().size() + " αποστολές.");
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = guestMenu();
            } else {
                userMenu();
            }
        }
        System.out.println("Αντίο!");
    }
    // ---------------- Μενού χωρίς σύνδεση ----------------
    private static boolean guestMenu() {
        System.out.println();
        System.out.println("1. Εγγραφή");
        System.out.println("2. Σύνδεση");
        System.out.println("0. Έξοδος");
        String choice = ask("Επιλογή");
        try {
            switch (choice) {
                case "1" -> {
                    String username = ask("Όνομα χρήστη");
                    String password = ask("Κωδικός");
                    userService.register(username, password);
                    save();
                    System.out.println("Η εγγραφή έγινε. Τώρα κάνε σύνδεση.");
                }
                case "2" -> {
                    String username = ask("Όνομα χρήστη");
                    String password = ask("Κωδικός");
                    currentUser = userService.login(username, password);
                    if (currentUser == null) {
                        System.out.println("Λάθος όνομα ή κωδικός.");
                    } else {
                        System.out.println("Καλώς ήρθες, " + currentUser.getUsername() + "!");
                    }
                }
                case "0" -> {
                    return false;
                }
                default -> System.out.println("Άγνωστη επιλογή.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Σφάλμα: " + e.getMessage());
        }
        return true;
    }
    // ---------------- Μενού συνδεδεμένου χρήστη ----------------
    private static void userMenu() {
        System.out.println();
        System.out.println("--- " + currentUser.getUsername() + " ---");
        System.out.println("1. Νέα ομάδα");
        System.out.println("2. Συμμετοχή σε ομάδα με κωδικό");
        System.out.println("3. Οι ομάδες μου");
        System.out.println("4. Παίξε αποστολή");
        System.out.println("5. Αναζήτηση βιβλίου στο Open Library");
        System.out.println("9. Αποσύνδεση (για να μπει άλλο μέλος)");
        String choice = ask("Επιλογή");
        try {
            switch (choice) {
                case "1" -> {
                    Team team = teamService.createTeam(ask("Όνομα ομάδας"), currentUser.getUsername());
                    save();
                    System.out.println("Η ομάδα δημιουργήθηκε. Κωδικός πρόσκλησης: " + team.getInviteCode());
                }
                case "2" -> {
                    Team team = teamService.joinTeam(ask("Κωδικός πρόσκλησης"), currentUser.getUsername());
                    save();
                    System.out.println("Μπήκες στην ομάδα " + team.getName() + ".");
                }
                case "3" -> showMyTeams();
                case "4" -> chooseAndPlay();
                case "5" -> searchBooks();
                case "9" -> {
                    currentUser = null;
                    System.out.println("Αποσυνδέθηκες.");
                }
                default -> System.out.println("Άγνωστη επιλογή.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Σφάλμα: " + e.getMessage());
        }
    }
    private static void showMyTeams() {
        List<Team> teams = teamService.teamsOf(currentUser.getUsername());
        if (teams.isEmpty()) {
            System.out.println("Δεν είσαι σε καμία ομάδα.");
            return;
        }
        for (Team t : teams) {
            System.out.println("- " + t.getName() + " (κωδικός " + t.getInviteCode() + ") μέλη: " +
                    t.getMembers()
                    + ", δημιουργός: " + t.getOwner());
        }
    }
    private static void chooseAndPlay() {
        List<Team> teams = teamService.teamsOf(currentUser.getUsername());
        if (teams.isEmpty()) {
            System.out.println("Πρώτα φτιάξε ή μπες σε μια ομάδα.");
            return;
        }
        Team team = pick("Διάλεξε ομάδα", teams);
        List<Mission> all = missions.all();
        Mission mission = pick("Διάλεξε αποστολή", all);
        GameSession session = gameService.startOrResume(team, mission, currentUser.getUsername());
        save();
        playLoop(session, mission);
    }
    // ---------------- Το παιχνίδι ----------------
    private static void playLoop(GameSession session, Mission mission) {
        while (true) {
            showBoard(session, mission);
            System.out.println("Γράψε τον αριθμό γρίφου για να απαντήσεις, Η = ημερολόγιο, Π = πρόοδος, Α = αναζήτηση βιβλίου, 0 = πίσω");
                    String choice = ask("Επιλογή").toUpperCase();
            if (choice.equals("0")) {
                return;
            } else if (choice.equals("Η") || choice.equals("H")) {
                showEvidence(session, mission);
            } else if (choice.equals("Π") || choice.equals("P")) {
                System.out.println(ProgressReport.of(session, mission));
            } else if (choice.equals("Α") || choice.equals("A")) {
                searchBooks();
            } else {
                Puzzle puzzle = puzzleByNumber(mission, choice);
                if (puzzle == null) {
                    System.out.println("Δεν υπάρχει τέτοιος γρίφος.");
                } else {
                    answerPuzzle(session, mission, puzzle);
                }
            }
        }
    }
    /** Δείχνει διαδρομές και γρίφους με την κατάστασή τους. */
    private static void showBoard(GameSession session, Mission mission) {
        System.out.println();
        System.out.println("##### " + mission.getTitle() + " #####");
        System.out.println(mission.getDescription());
        int number = 1;
        for (Puzzle p : mission.getPuzzles()) {
            PuzzleStatus status = RuleEngine.status(p, session);
            String where = p.getPathId() == null ? "Τελικό" : mission.getPath(p.getPathId()).getTitle();
            System.out.printf("%d. [%s] %-10s %s%n", number, statusSymbol(status), where, p.getTitle());
            if (status == PuzzleStatus.LOCKED) {
                System.out.println(" λείπουν: " + titlesOf(mission, RuleEngine.missing(p, session)));
            }
            number++;
        }
        if (session.isCompleted()) {
            System.out.println("*** Η αποστολή ολοκληρώθηκε! ***");
        }
    }
    private static void answerPuzzle(GameSession session, Mission mission, Puzzle puzzle) {
        System.out.println();
        System.out.println(puzzle.getTitle() + " (" + puzzle.typeName() + ")");
        System.out.println(puzzle.getQuestion());
        String answer;
        if (puzzle instanceof SequencePuzzle seq) {
            System.out.println("Πρόοδος σειράς: " + session.getSequenceProgress(puzzle.getId()) + "/" + seq.getSteps().size());
            List<String> options = puzzle.options();
            for (int i = 0; i < options.size(); i++) {
                System.out.println(" " + (i + 1) + ") " + options.get(i));
            }
            int n = askNumber("Ποιο είναι το επόμενο γεγονός", options.size());
            answer = options.get(n - 1);
        } else if (puzzle instanceof CollaborativePuzzle col) {
            System.out.println("Δίσκοι που έχουν γυρίσει: " + session.getContributions(puzzle.getId()).keySet());
            int part = askNumber("Ποιον δίσκο γυρίζεις (1-" + col.getParts().size() + ")", col.getParts().size());
            answer = part + " " + ask("Τιμή");
        } else {
            answer = ask("Απάντηση");
        }
        AnswerResult result = gameService.answer(session, mission, puzzle.getId(), currentUser.getUsername(), answer);
        save();
        System.out.println(">> " + result.getMessage());
        if (result.getOutcome() == AnswerResult.Outcome.SOLVED && puzzle.getRewardEvidence() != null) {
            Evidence ev = mission.getEvidence(puzzle.getRewardEvidence());
            System.out.println(">> Νέο στοιχείο στο ημερολόγιο: " + ev.getTitle() + " - " + ev.getText());
        }
    }
    private static void showEvidence(GameSession session, Mission mission) {
        System.out.println("--- Κοινό ημερολόγιο ομάδας ---");
        if (session.getFoundEvidence().isEmpty()) {
            System.out.println("(άδειο)");
        }
        for (FoundEvidence f : session.getFoundEvidence()) {
            Evidence ev = mission.getEvidence(f.getEvidenceId());
            System.out.println("- " + ev.getTitle() + ": " + ev.getText() + " [βρέθηκε από " + f.getFoundBy() + "]");
        }
    }
    // ---------------- Αναζήτηση ----------------
    private static void searchBooks() {
        try {
            List<Book> books = openLibrary.search(ask("Τίτλος βιβλίου"));
            if (books.isEmpty()) {
                System.out.println(" Δεν βρέθηκε βιβλίο.");
            }
            for (Book b : books) {
                System.out.println(" " + b);
            }
        } catch (ExternalApiException e) {
            System.out.println("Η αναζήτηση απέτυχε: " + e.getMessage());
            System.out.println("Το παιχνίδι συνεχίζει κανονικά. Δοκίμασε ξανά σε λίγο.");
        }
    }
    // ---------------- Βοηθητικές ----------------
    private static String ask(String prompt) {
        System.out.print(prompt + ": ");
        if (!in.hasNextLine()) {
            System.exit(0); // τέλος εισόδου
        }
        return in.nextLine().trim();
    }
    private static int askNumber(String prompt, int max) {
        while (true) {
            String text = ask(prompt);
            try {
                int n = Integer.parseInt(text);
                if (n >= 1 && n <= max) {
                    return n;
                }
            } catch (NumberFormatException ignored) {
// θα ξαναρωτήσουμε
            }
            System.out.println("Δώσε αριθμό από 1 έως " + max + ".");
        }
    }
    private static <T> T pick(String prompt, List<T> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        return items.get(askNumber(prompt, items.size()) - 1);
    }
    private static Puzzle puzzleByNumber(Mission mission, String text) {
        try {
            int n = Integer.parseInt(text);
            if (n >= 1 && n <= mission.getPuzzles().size()) {
                return mission.getPuzzles().get(n - 1);
            }
        } catch (NumberFormatException ignored) {
// όχι αριθμός
        }
        return null;
    }
    private static String statusSymbol(PuzzleStatus status) {
        return switch (status) {
            case LOCKED -> "κλειδωμένος";
            case AVAILABLE -> "ανοιχτός ";
            case SOLVED -> "λύθηκε ";
        };
    }
    private static List<String> titlesOf(Mission mission, List<String> evidenceIds) {
        List<String> titles = new ArrayList<>();
        for (String id : evidenceIds) {
            titles.add(mission.getEvidence(id).getTitle());
        }
        return titles;
    }
    private static void save() {
        store.save(data);
    }
}
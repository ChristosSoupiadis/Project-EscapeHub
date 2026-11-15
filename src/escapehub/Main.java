package escapehub;
import java.util.List;
import java.util.Scanner;
/**
 * Το σημείο εκκίνησης (η μοναδική main).
 *
 * ΕΚΔΟΣΗ ΕΒΔΟΜΑΔΑΣ 1: φορτώνει τις αποστολές, κάνει εγγραφή/σύνδεση χρηστών
 * (αποθηκεύονται σε αρχείο) και δείχνει τις αποστολές με τους γρίφους τους.
 * Ομάδες, παιχνίδι και browser προστίθενται τις επόμενες εβδομάδες.
 */
public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static DataStore store;
    private static AppData data;
    private static MissionCatalog missions;
    private static UserService userService;
    private static User currentUser = null;
    public static void main(String[] args) {
// 1. Αποστολές από τον φάκελο missions/ (Μέλος Δ)
        try {
            missions = new MissionCatalog(MissionLoader.loadFolder("missions"));
        } catch (IllegalStateException e) {
            System.out.println("Λάθος στα αρχεία αποστολών:\n" + e.getMessage());
            return;
        }
// 2. Αποθηκευμένα δεδομένα (Μέλος Γ) και χρήστες (Μέλος Β)
        store = new DataStore("data/escapehub.dat");
        data = store.load();
        userService = new UserService(data);
        System.out.println("=== EscapeHub (εβδομάδα 1) ===");
        System.out.println("Φορτώθηκαν " + missions.all().size() + " αποστολές και "
                + data.getUsers().size() + " χρήστες.");
        boolean running = true;
        while (running) {
            System.out.println();
            if (currentUser == null) {
                System.out.println("1. Εγγραφή");
                System.out.println("2. Σύνδεση");
            } else {
                System.out.println("Συνδεδεμένος: " + currentUser.getUsername());
                System.out.println("3. Δες τις αποστολές");
                System.out.println("9. Αποσύνδεση");
            }
            System.out.println("0. Έξοδος");
            String choice = ask("Επιλογή");
            try {
                switch (choice) {
                    case "1" -> register();
                    case "2" -> login();
                    case "3" -> showMissions();
                    case "9" -> currentUser = null;
                    case "0" -> running = false;
                    default -> System.out.println("Άγνωστη επιλογή.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Σφάλμα: " + e.getMessage());
            }
        }
        System.out.println("Αντίο!");
    }
    private static void register() {
        String username = ask("Όνομα χρήστη");
        String password = ask("Κωδικός");
        userService.register(username, password);
        store.save(data);
        System.out.println("Η εγγραφή έγινε. Τώρα κάνε σύνδεση.");
    }
    private static void login() {
        currentUser = userService.login(ask("Όνομα χρήστη"), ask("Κωδικός"));
        if (currentUser == null) {
            System.out.println("Λάθος όνομα ή κωδικός.");
        } else {
            System.out.println("Καλώς ήρθες, " + currentUser.getUsername() + "!");
        }
    }
    private static void showMissions() {
        if (currentUser == null) {
            System.out.println("Πρώτα κάνε σύνδεση.");
            return;
        }
        for (Mission m : missions.all()) {
            System.out.println("## " + m.getTitle());
            System.out.println(" " + m.getDescription());
            for (MissionPath p : m.getPaths()) {
                System.out.println(" Διαδρομή: " + p.getTitle() + " - " + p.getDescription());
            }
            List<Puzzle> puzzles = m.getPuzzles();
            for (Puzzle pz : puzzles) {
                System.out.println(" * " + pz.getTitle() + " (" + pz.typeName() + ")");
            }
        }
    }
    private static String ask(String prompt) {
        System.out.print(prompt + ": ");
        if (!in.hasNextLine()) {
            System.exit(0);
        }
        return in.nextLine().trim();
    }
}

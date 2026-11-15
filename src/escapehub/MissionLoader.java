package escapehub;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
21
import java.util.stream.Stream;
/**
 * Διαβάζει αποστολές από αρχεία κειμένου (R9: νέα αποστολή = νέο αρχείο, χωρίς αλλαγή κώδικα).
 *
 * Μορφή: μία εγγραφή ανά γραμμή, πεδία χωρισμένα με '|'. Οι γραμμές με '#' είναι σχόλια.
 * (Όχι ';', γιατί το ελληνικό ερωτηματικό είναι ';' και θα «έσπαγε» τις ερωτήσεις.)
 * MISSION|id|τίτλος|περιγραφή|ποινή_σε_δευτερόλεπτα
 * PATH|id|τίτλος|περιγραφή
 * EVIDENCE|id|τίτλος|κείμενο
 * PUZZLE|τύπος|id|διαδρομή|τίτλος|ερώτηση|απαιτεί|ανταμοιβή|τελικός|απαντήσεις|επιπλέον
 * Όπου δεν υπάρχει τιμή γράφουμε '-'. Οι λίστες (απαιτεί, απαντήσεις) χωρίζονται με ','.
 *
 * ΕΚΔΟΣΗ ΕΒΔΟΜΑΔΑΣ 1: μόνο οι τύποι TEXT και EXTERNAL_DATA.
 * Την εβδομάδα 2 προστίθενται SEQUENCE, COLLABORATIVE και ο MissionValidator.
 */
public class MissionLoader {
    private MissionLoader() {
    }
    /** Φορτώνει όλα τα .txt του φακέλου. */
    public static List<Mission> loadFolder(String folder) {
        Path dir = Paths.get(folder);
        if (!Files.isDirectory(dir)) {
            throw new IllegalStateException("Δεν βρέθηκε ο φάκελος " + dir.toAbsolutePath());
        }
        List<Mission> result = new ArrayList<>();
        try (Stream<Path> files = Files.list(dir)) {
            List<Path> sorted = files.filter(p -> p.toString().endsWith(".txt")).sorted().toList();
            for (Path file : sorted) {
                result.add(loadFile(file));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Δεν διαβάζεται ο φάκελος: " + e.getMessage());
        }
        return result;
    }
    public static Mission loadFile(Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Δεν διαβάζεται το " + file + ": " + e.getMessage());
        }
        Mission mission = null;
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] f = line.split("\\|", -1); // το | είναι ειδικός χαρακτήρας στο regex, γι' αυτό \\|
            try {
                switch (f[0]) {
                    case "MISSION" -> {
                        need(f, 5);
                        mission = new Mission(f[1], f[2], f[3], Integer.parseInt(f[4].trim()));
                    }
                    case "PATH" -> {
                        need(f, 4);
                        requireMission(mission).addPath(new MissionPath(f[1], f[2], f[3]));
                    }
                    case "EVIDENCE" -> {
                        need(f, 4);
                        requireMission(mission).addEvidence(new Evidence(f[1], f[2], f[3]));
                    }
                    case "PUZZLE" -> {
                        need(f, 11);
                        requireMission(mission).addPuzzle(createPuzzle(f));
                    }
                    default -> throw new IllegalStateException("άγνωστη εγγραφή '" + f[0] + "'");
                }
            } catch (RuntimeException e) {
                throw new IllegalStateException(file.getFileName() + " γραμμή " + lineNumber + ": " +
                        e.getMessage());
                22
            }
        }
        if (mission == null) {
            throw new IllegalStateException(file.getFileName() + ": λείπει η γραμμή MISSION");
        }
        return mission;
    }
    /** Φτιάχνει το σωστό είδος γρίφου ανάλογα με τον τύπο (factory). */
    private static Puzzle createPuzzle(String[] f) {
        String type = f[1];
        String id = f[2];
        String pathId = empty(f[3]) ? null : f[3];
        String title = f[4];
        String question = f[5];
        List<String> requires = empty(f[6]) ? new ArrayList<>() :
                Arrays.asList(f[6].trim().split("\\s*,\\s*"));
        String reward = empty(f[7]) ? null : f[7];
        boolean isFinal = f[8].trim().equalsIgnoreCase("yes");
        List<String> answers = empty(f[9]) ? new ArrayList<>() :
                Arrays.asList(f[9].trim().split("\\s*,\\s*"));
        String extra = f[10].trim();
        return switch (type) {
            case "TEXT" -> new TextPuzzle(id, pathId, title, question, requires, reward, isFinal, answers);
            case "EXTERNAL_DATA" -> new ExternalDataPuzzle(id, pathId, title, question, requires, reward,
                    isFinal, answers, extra);
            default -> throw new IllegalStateException("ο τύπος γρίφου " + type + " δεν υποστηρίζεται ακόμα");
        };
    }
    private static boolean empty(String s) {
        return s.isBlank() || s.trim().equals("-");
    }
    private static void need(String[] fields, int count) {
        if (fields.length != count) {
            throw new IllegalStateException("περίμενα " + count + " πεδία, βρήκα " + fields.length);
        }
    }
    private static Mission requireMission(Mission m) {
        if (m == null) {
            throw new IllegalStateException("η γραμμή MISSION πρέπει να είναι πρώτη");
        }
        return m;
    }
}

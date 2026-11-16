package escapehub;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
/**
 * Όλα τα δεδομένα που πρέπει να αποθηκεύονται ανάμεσα στις εκτελέσεις.
 * Οι αποστολές ΔΕΝ είναι εδώ: διαβάζονται κάθε φορά από τα αρχεία τους.
 */
public class AppData implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, User> users = new HashMap<>();              // κλειδί: username
    private final Map<String, Team> teams = new HashMap<>();              // κλειδί: κωδικός πρόσκλησης

    private final Map<String, GameSession> sessions = new HashMap<>();    // κλειδί: ομάδα/αποστολή
    public Map<String, User> getUsers() {
        return users;
    }
    public Map<String, Team> getTeams() {
        return teams;
    }
    public Map<String, GameSession> getSessions() {
        return sessions;
    }
}
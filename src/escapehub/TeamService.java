package escapehub;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/** Δημιουργία ομάδας και συμμετοχή με κωδικό πρόσκλησης (R1). */
public class TeamService {
    // χωρίς 0/O και 1/I/L, που μπερδεύονται όταν ο κωδικός διαβάζεται
    private static final String ALPHABET = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 6;
    private final AppData data;
    private final SecureRandom random = new SecureRandom();
    public TeamService(AppData data) {
        this.data = data;
    }

    public Team createTeam(String name, String owner) {
        if (name == null || name.isBlank() || name.length() > 40) {
            throw new IllegalArgumentException("Το όνομα ομάδας θέλει 1-40 χαρακτήρες.");

        }
        Team team = new Team(name.trim(), newCode(), owner);
        data.getTeams().put(team.getInviteCode(), team);
        return team;
    }
    public Team joinTeam(String code, String username) {
        Team team = data.getTeams().get(code.trim().toUpperCase(Locale.ROOT));
        if (team == null) {
            throw new IllegalArgumentException("Δεν υπάρχει ομάδα με αυτόν τον κωδικό.");
        }
        if (team.isMember(username)) {
            throw new IllegalArgumentException("Είσαι ήδη μέλος της ομάδας.");
        }
        team.addMember(username);
        return team;
    }
    public List<Team> teamsOf(String username) {
        List<Team> result = new ArrayList<>();
        for (Team t : data.getTeams().values()) {
            if (t.isMember(username)) {
                result.add(t);
            }
        }
        return result;
    }
    private String newCode() {
        while (true) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
            }
            String code = sb.toString();
            if (!data.getTeams().containsKey(code)) { // σπάνιο, αλλά ελέγχουμε για διπλό
                return code;
            }
        }
    }
}
package escapehub;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
/** Εγγραφή και σύνδεση χρηστών (R1). */
public class UserService {
    private final AppData data;
    public UserService(AppData data) {
        this.data = data;
    }
    public User register(String username, String password) {
        String name = normalize(username);
        if (!name.matches("[a-z0-9_]{3,30}")) {
            throw new IllegalArgumentException("Το όνομα θέλει 3-30 λατινικά γράμματα, αριθμούς ή _.");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Ο κωδικός θέλει τουλάχιστον 6 χαρακτήρες.");
        }
        if (data.getUsers().containsKey(name)) {
            throw new IllegalArgumentException("Το όνομα χρησιμοποιείται ήδη.");
        }
        User user = new User(name, hash(name, password));
        data.getUsers().put(name, user);
        return user;
    }
    /** Επιστρέφει τον χρήστη ή null αν το όνομα ή ο κωδικός είναι λάθος. */
    public User login(String username, String password) {
        String name = normalize(username);
        User user = data.getUsers().get(name);
        if (user == null || !user.getPasswordHash().equals(hash(name, password))) {
            return null;
        }
        return user;
    }
    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
    /**
     * SHA-256 του "username:κωδικός". Το username λειτουργεί ως salt, ώστε δύο χρήστες
     * με τον ίδιο κωδικό να έχουν διαφορετικό hash.
     * (Σε πραγματική εφαρμογή θα χρησιμοποιούσαμε BCrypt, που είναι πιο αργό και άρα πιο ασφαλές.)
     */
    private String hash(String username, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Δεν υπάρχει SHA-256", e);
        }
    }
}
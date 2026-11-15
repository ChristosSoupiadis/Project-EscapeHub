package escapehub;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
/**
 * Κάνει αιτήματα HTTP GET με τον HttpClient της Java (χωρίς εξωτερικές βιβλιοθήκες).
 * - timeout 5 δευτερόλεπτα, ώστε να μην «κολλάει» το πρόγραμμα
 * - User-Agent με στοιχεία επικοινωνίας (το ζητά η Wikimedia)
 * - το πολύ ένα αίτημα ανά δευτερόλεπτο (σεβασμός στα όρια των υπηρεσιών)
 */
public class HttpHelper {
    // ΑΛΛΑΞΤΕ το email με το email της ομάδας
    private static final String USER_AGENT = "EscapeHub/1.0 (university project; contact: team@example.com)";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final long MIN_INTERVAL_MS = 1000;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    private long lastRequest = 0;
    public String get(String url, String accept) {
        waitForTurn();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("Accept", accept)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            if (code == 429) {
                throw new ExternalApiException("πάρα πολλά αιτήματα, δοκίμασε σε λίγο (429)");
            }
            if (code != 200) {
                throw new ExternalApiException("η υπηρεσία απάντησε με σφάλμα " + code);
            }
            return response.body();
        } catch (IOException e) {
// εδώ έρχονται και τα timeouts (HttpTimeoutException) και η έλλειψη δικτύου
            throw new ExternalApiException("δεν υπάρχει σύνδεση ή η υπηρεσία άργησε (" +
                    e.getClass().getSimpleName() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiException("το αίτημα διακόπηκε");
        }
    }
    /** Κωδικοποιεί κείμενο για να μπει με ασφάλεια σε URL (κενά, ελληνικά κ.λπ.). */
    public static String encode(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }
    private synchronized void waitForTurn() {
        long wait = lastRequest + MIN_INTERVAL_MS - System.currentTimeMillis();
        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        lastRequest = System.currentTimeMillis();
    }
}

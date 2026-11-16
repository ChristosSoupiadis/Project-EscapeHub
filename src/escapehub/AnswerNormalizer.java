package escapehub;

import java.text.Normalizer;
import java.util.Locale;
/**
 * Κάνει τις απαντήσεις συγκρίσιμες: αγνοεί κεφαλαία, τόνους και περιττά κενά.
 * Π.χ. " ΓΕΝΕΎΗ " και "Γενεύη" γίνονται και τα δύο "γενευη".
 */
public class AnswerNormalizer {
    private AnswerNormalizer() {
    }
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String noAccents = Normalizer.normalize(text, Normalizer.Form.NFD) // "ά" → "α" + τόνος
                .replaceAll("\\p{M}", ""); // σβήνει τόνους/διαλυτικά
        return noAccents.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
    public static boolean matches(String given, String expected) {
        String e = normalize(expected);
        return !e.isEmpty() && e.equals(normalize(given)); // κενή σωστή απάντηση δεν ταιριάζει ποτέ
    }
}

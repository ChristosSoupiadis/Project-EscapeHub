package escapehub;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Πολύ απλή ανάγνωση JSON με regex, για να μη χρειαστεί εξωτερική βιβλιοθήκη.
 * Δουλεύει για τα απλά, «επίπεδα» JSON που επιστρέφουν τα δύο APIs μας.
 * (Σε μεγαλύτερο project θα χρησιμοποιούσαμε βιβλιοθήκη όπως Jackson ή Gson.)
 */
public class JsonText {
    private static final String STRING_VALUE = "\"((?:[^\"\\\\]|\\\\.)*)\"";
    private JsonText() {

    }
    /** Η τιμή κειμένου ενός πεδίου, π.χ. "title":"Frankenstein" → Frankenstein. null αν δεν υπάρχει. */
    public static String stringField(String json, String name) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(name) + "\"\\s*:\\s*" + STRING_VALUE).matcher(json);
        return m.find() ? unescape(m.group(1)) : null;
    }
    /** Ακέραιος, π.χ. "first_publish_year":1818 → "1818". null αν δεν υπάρχει. */
    public static String numberField(String json, String name) {
        Matcher m = Pattern.compile("\"" + Pattern.quote(name) + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? m.group(1) : null;
    }
    /** Λίστα κειμένων, π.χ. "author_name":["A","B"] → [A, B]. */
    public static List<String> stringArray(String json, String name) {
        List<String> result = new ArrayList<>();
        Matcher m = Pattern.compile("\"" + Pattern.quote(name) + "\"\\s*:\\s*\\[(.*?)\\]").matcher(json);
        if (m.find()) {
            Matcher item = Pattern.compile(STRING_VALUE).matcher(m.group(1));
            while (item.find()) {
                result.add(unescape(item.group(1)));
            }
        }
        return result;
    }
    /** Μετατρέπει τα \" \\ \/ \n é κ.λπ. σε κανονικούς χαρακτήρες. */
    public static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i + 1 >= s.length()) {
                sb.append(c);
                continue;
            }
            char next = s.charAt(++i);
            switch (next) {
                case 'n' -> sb.append('\n');
                case 't' -> sb.append('\t');
                case 'u' -> {
                    if (i + 4 < s.length()) {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                        i += 4;
                    }
                }
                default -> sb.append(next); // \" \\ \/
            }
        }
        return sb.toString();
    }
}

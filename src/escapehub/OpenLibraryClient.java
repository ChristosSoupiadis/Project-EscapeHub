package escapehub;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/** Αναζήτηση βιβλίων στο Open Library (openlibrary.org/search.json). */
public class OpenLibraryClient {
    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final Pattern OBJECT = Pattern.compile("\\{[^{}]*\\}"); // ένα αντικείμενο χωρίς άλλα μέσα του
    private final HttpHelper http;
    public OpenLibraryClient(HttpHelper http) {
        this.http = http;
    }
    public List<Book> search(String query) {
        if (query == null || query.isBlank()) {
            return new ArrayList<>(); // κενή αναζήτηση: καμία κλήση
        }
// ζητάμε μόνο τα πεδία που χρειαζόμαστε → μικρότερη απάντηση
        String url = SEARCH_URL + "?limit=5&fields=title,author_name,first_publish_year&q=" +
                HttpHelper.encode(query.trim());
        return parse(http.get(url, "application/json"));
    }
    public static List<Book> parse(String json) {
        List<Book> books = new ArrayList<>();
        int docs = json.indexOf("\"docs\"");
        if (docs < 0) {
            throw new ExternalApiException("απρόσμενη απάντηση από το Open Library");
        }
        Matcher m = OBJECT.matcher(json.substring(docs));
        while (m.find()) {
            String doc = m.group();
            String title = JsonText.stringField(doc, "title");
            if (title == null) {
                continue;
            }
            List<String> authors = JsonText.stringArray(doc, "author_name");
            String author = authors.isEmpty() ? null : authors.get(0);
            books.add(new Book(title, author, JsonText.numberField(doc, "first_publish_year")));
        }
        return books;
    }
}

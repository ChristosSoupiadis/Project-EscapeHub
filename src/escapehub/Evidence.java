package escapehub;
/** Ένα στοιχείο που μπορεί να βρει η ομάδα. Ο τίτλος φαίνεται πάντα, το κείμενο μόνο όταν βρεθεί. */
public class Evidence {
    private final String id;
    private final String title;
    private final String text;
    public Evidence(String id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getText() {
        return text;
    }
}

package escapehub;
/** Μια διαδρομή διερεύνησης (π.χ. «Βιβλίο»). Ονομάζεται MissionPath για να μη μπερδεύεται με το
 java.nio.file.Path. */
public class MissionPath {
18
    private final String id;
    private final String title;
    private final String description;
    public MissionPath(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
}

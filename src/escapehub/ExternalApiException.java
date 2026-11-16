package escapehub;
/** Κάθε πρόβλημα με εξωτερικό API (χωρίς δίκτυο, αργή απάντηση, 429, 500…) γίνεται αυτή η εξαίρεση. */
public class ExternalApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ExternalApiException(String message) {
        super(message);
    }
}
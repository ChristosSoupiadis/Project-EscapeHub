package escapehub;
import java.io.Serializable;
/** Ένα στοιχείο που βρήκε η ομάδα: ποιο, ποιος το βρήκε και πότε. */
public class FoundEvidence implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String evidenceId;
    private final String foundBy;
    private final long foundAt;
    public FoundEvidence(String evidenceId, String foundBy, long foundAt) {
        this.evidenceId = evidenceId;
        this.foundBy = foundBy;
        this.foundAt = foundAt;
    }
    public String getEvidenceId() {
        return evidenceId;
    }
    public String getFoundBy() {
        return foundBy;
    }
    public long getFoundAt() {
        return foundAt;
    }
}
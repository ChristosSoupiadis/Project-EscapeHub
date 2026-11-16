package escapehub;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/** Κρατά όλες τις αποστολές που φορτώθηκαν, με το id τους. */
public class MissionCatalog {
    private final Map<String, Mission> missions = new LinkedHashMap<>();
    public MissionCatalog(List<Mission> list) {
        for (Mission m : list) {
            if (missions.containsKey(m.getId())) {
                throw new IllegalStateException("Δύο αποστολές με το ίδιο id: " + m.getId());
            }
            missions.put(m.getId(), m);
        }
        if (missions.isEmpty()) {
            throw new IllegalStateException("Δεν βρέθηκε καμία αποστολή.");
        }
    }
    public Mission get(String id) {
        return missions.get(id);
    }
    public List<Mission> all() {
        return new ArrayList<>(missions.values());
    }
}

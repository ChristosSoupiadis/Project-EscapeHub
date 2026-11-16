package escapehub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Αποθηκεύει και φορτώνει το AppData σε ένα αρχείο με Java serialization.
 * Έτσι οι χρήστες, οι ομάδες και η πρόοδος μένουν και όταν κλείσει το πρόγραμμα (R2: συνέχεια).
 */
public class DataStore {

    private final File file;

    public DataStore(String path) {
        this.file = new File(path);
    }

    public AppData load() {
        if (!file.exists()) {
            return new AppData(); // πρώτη εκτέλεση
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (AppData) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Προσοχή: δεν διαβάστηκαν τα αποθηκευμένα δεδομένα (" + e.getMessage() + "). Ξεκινάμε από την αρχή.");
            return new AppData();
        }
    }

    public void save(AppData data) {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(data);
        } catch (IOException e) {
            System.out.println("Προσοχή: δεν αποθηκεύτηκαν τα δεδομένα: " + e.getMessage());
        }
    }
}

package escapehub;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Μια ομάδα: όνομα, κωδικός πρόσκλησης, δημιουργός και μέλη (R1). */
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final String inviteCode;
    private final String owner;
    private final List<String> members = new ArrayList<>();

    public Team(String name, String inviteCode, String owner) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.owner = owner;
        this.members.add(owner); // ο δημιουργός είναι και μέλος
    }

    public boolean isMember(String username) {
        return members.contains(username);
    }

    public boolean isOwner(String username) {
        return owner.equals(username);
    }

    public void addMember(String username) {
        members.add(username);
    }

    public String getName() {
        return name;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getMembers() {
        return new ArrayList<>(members); // αντίγραφο, για να μην αλλάξει κανείς τη λίστα απ' έξω
    }

    @Override
    public String toString() {
        return name + " (" + members.size() + " μέλη)";
    }
}
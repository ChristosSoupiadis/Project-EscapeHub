package escapehub;

/** Υπολογίζει την πρόοδο: γρίφοι, ποσοστό, χρόνος και ποινή για λάθη. */
public class ProgressReport {

    private final int solved;
    private final int total;
    private final long elapsedSeconds;
    private final long penaltySeconds;
    private final boolean completed;

    public ProgressReport(int solved, int total, long elapsedSeconds, long penaltySeconds, boolean completed) {
        this.solved = solved;
        this.total = total;
        this.elapsedSeconds = elapsedSeconds;
        this.penaltySeconds = penaltySeconds;
        this.completed = completed;
    }

    public static ProgressReport of(GameSession session, Mission mission) {
        return calculate(session, mission, System.currentTimeMillis());
    }

    /** Ξεχωριστή μέθοδος με το «τώρα» ως παράμετρο, ώστε να ελέγχεται εύκολα. */
    public static ProgressReport calculate(GameSession session, Mission mission, long now) {
        long end = session.isCompleted() ? session.getCompletedAt() : now;
        long elapsed = Math.max(0, (end - session.getStartedAt()) / 1000);
        long penalty = (long) session.getWrongAnswers() * mission.getPenaltySeconds();
        return new ProgressReport(session.getSolvedCount(), mission.getPuzzles().size(), elapsed, penalty, session.isCompleted());
    }

    public int getPercent() {
        return total == 0 ? 0 : solved * 100 / total;
    }

    public long getTotalSeconds() {
        return elapsedSeconds + penaltySeconds;
    }

    public long getPenaltySeconds() {
        return penaltySeconds;
    }

    private static String time(long seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    @Override
    public String toString() {
        return "Πρόοδος: " + solved + "/" + total + " γρίφοι (" + getPercent() + "%)"
                + "\nΧρόνος: " + time(elapsedSeconds)
                + " + ποινή " + time(penaltySeconds)
                + " = " + time(getTotalSeconds())
                + (completed ? "\nΗ αποστολή ολοκληρώθηκε!" : "");
    }
}
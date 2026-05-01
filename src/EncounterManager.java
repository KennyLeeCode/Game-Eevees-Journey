import java.util.Random;

public class EncounterManager {

    private static final int STEPS_BETWEEN_CHECKS = 8;  // check every N movement steps
    private static final double ENCOUNTER_CHANCE   = 0.20; // 20% per check

    private final Random rng = new Random();
    private int stepCount = 0;

    private Eeveelution pending = null;

    // call each frame the player actually moved; returns true when encounter triggers
    public boolean onStep(TileType currentTile, Collection collection) {
        Eeveelution e = Eeveelution.forZone(currentTile);
        if (e == null || collection.has(e)) return false;

        stepCount++;
        if (stepCount < STEPS_BETWEEN_CHECKS) return false;

        stepCount = 0;
        if (rng.nextDouble() < ENCOUNTER_CHANCE) {
            pending = Eeveelution.forZone(currentTile);
            return true;
        }
        return false;
    }

    public Eeveelution getPending() { return pending; }

    public void clearPending() { pending = null; }
}

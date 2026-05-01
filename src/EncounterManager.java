import java.util.Random;

// Decides when a wild Eeveelution encounter should trigger
// Uses a step counter so encounters don't happen every single frame
public class EncounterManager {

    private static final int    STEPS_BETWEEN_CHECKS = 8;    // minimum steps before a roll can happen
    private static final double ENCOUNTER_CHANCE      = 0.20; // 20% chance per check

    private final Random rng = new Random();
    private int stepCount = 0;
    private Eeveelution pending = null;

    // Call this every frame the player actually moved
    // Returns true when an encounter should start
    public boolean onStep(TileType currentTile, Collection collection) {
        Eeveelution e = Eeveelution.forZone(currentTile);
        // no encounter on regular tiles, and skip zones whose Eeveelution is already caught
        if (e == null || collection.has(e)) return false;

        stepCount++;
        if (stepCount < STEPS_BETWEEN_CHECKS) return false;

        stepCount = 0; // reset counter regardless of whether roll succeeds
        if (rng.nextDouble() < ENCOUNTER_CHANCE) {
            pending = Eeveelution.forZone(currentTile);
            return true;
        }
        return false;
    }

    public Eeveelution getPending()  { return pending; }
    public void        clearPending() { pending = null; }
}

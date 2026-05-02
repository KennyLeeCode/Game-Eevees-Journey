// Controls which screen is currently active
public enum GameState {
    EXPLORE,    // player is walking around the map
    ENCOUNTER,  // flashing transition when a wild Eeveelution appears
    BATTLE,     // battle screen is open
    COLLECTION, // collection overlay is open
    WIN         // player caught all Eeveelutions
}

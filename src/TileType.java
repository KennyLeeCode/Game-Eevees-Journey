import java.awt.Color;

public enum TileType {
    PATH        (new Color(180, 160, 120), false),
    GRASS       (new Color(60,  140,  60), false),
    WATER       (new Color(40,   80, 180), true),
    TREE        (new Color(20,   80,  20), true),
    MOON_SHRINE (new Color(180, 160, 220), false),
    FOREST      (new Color(30,  100,  30), false),
    SAND        (new Color(210, 190, 130), false),
    // Eeveelution encounter zones
    ICE_ZONE    (new Color(180, 220, 240), false),  // Glaceon
    FIRE_ZONE   (new Color(210,  80,  30), false),  // Flareon
    ELECTRIC_ZONE(new Color(240, 210,  40), false), // Jolteon
    DARK_ZONE   (new Color(40,   30,  60), false),  // Umbreon
    FAIRY_ZONE  (new Color(240, 160, 200), false),  // Sylveon
    PSYCHIC_ZONE(new Color(200,  80, 160), false);  // Espeon

    public final Color color;
    public final boolean solid;

    TileType(Color color, boolean solid) {
        this.color = color;
        this.solid = solid;
    }
}

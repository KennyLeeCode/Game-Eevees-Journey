import java.awt.Color;

// Every tile on the map has a color and a solid flag.
// solid = true means the player cannot walk through it (trees, water).
// color is used as a fallback for non-zone tiles and for UI elements (collection screen, zone hints).
// Zone tiles (ICE_ZONE, FIRE_ZONE, etc.) are drawn using overworldBackground.png instead of their color.
public enum TileType {
    PATH         (new Color(180, 160, 120), false),
    GRASS        (new Color( 60, 140,  60), false),
    WATER        (new Color( 40,  80, 180), false), // walkable so Vaporeon encounters trigger
    TREE         (new Color( 20,  80,  20), true),
    STARTER_PLAINS (new Color(180, 160, 220), false),
    FOREST       (new Color( 30, 100,  30), false),
    SAND         (new Color(210, 190, 130), false),
    ICE_ZONE        (new Color(180, 220, 240), false),
    FIRE_ZONE       (new Color(210,  80,  30), false),
    ELECTRIC_ZONE   (new Color(240, 210,  40), false),
    DARK_ZONE       (new Color( 40,  30,  60), false),
    FAIRY_ZONE      (new Color(240, 160, 200), false),
    PSYCHIC_ZONE    (new Color(200,  80, 160), false),
    HEALING_MACHINE (new Color(180, 220, 180), true);

    public final Color   color;
    public final boolean solid;

    TileType(Color color, boolean solid) {
        this.color = color;
        this.solid = solid;
    }
}

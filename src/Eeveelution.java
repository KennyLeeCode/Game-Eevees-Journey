import java.awt.Color;

public enum Eeveelution {
    VAPOREON ("Vaporeon",  TileType.WATER,         new Color(100, 160, 240), 45),
    LEAFEON  ("Leafeon",   TileType.FOREST,         new Color(100, 180,  80), 40),
    GLACEON  ("Glaceon",   TileType.ICE_ZONE,       new Color(140, 210, 240), 40),
    FLAREON  ("Flareon",   TileType.FIRE_ZONE,      new Color(230, 100,  40), 45),
    JOLTEON  ("Jolteon",   TileType.ELECTRIC_ZONE,  new Color(240, 220,  60), 40),
    UMBREON  ("Umbreon",   TileType.DARK_ZONE,      new Color( 60,  50,  90), 50),
    SYLVEON  ("Sylveon",   TileType.FAIRY_ZONE,     new Color(240, 160, 200), 40),
    ESPEON   ("Espeon",    TileType.PSYCHIC_ZONE,   new Color(200, 120, 200), 40),
    EEVEE    ("Eevee",     TileType.MOON_SHRINE,    new Color(200, 160, 100), 35);

    public final String name;
    public final TileType zone;
    public final Color color;
    public final int maxHp;

    Eeveelution(String name, TileType zone, Color color, int maxHp) {
        this.name  = name;
        this.zone  = zone;
        this.color = color;
        this.maxHp = maxHp;
    }

    public static Eeveelution forZone(TileType tile) {
        for (Eeveelution e : values())
            if (e.zone == tile) return e;
        return null;
    }
}

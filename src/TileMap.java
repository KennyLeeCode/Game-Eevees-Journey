import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class TileMap {

    public static final int MAP_COLS = 32;
    public static final int MAP_ROWS = 24;

    private final TileType[][] tiles = new TileType[MAP_ROWS][MAP_COLS];

    // camera offset (top-left tile in world coords)
    public int camX = 0;
    public int camY = 0;

    public TileMap() {
        buildMap();
    }

    private void buildMap() {
        // zones extend to every edge - no tree border, out-of-bounds check in tileAt() stops the player
        fillZone(0,  7,  0,  9,  TileType.STARTER_PLAINS);  // Eevee Grove      (top-left)
        fillZone(0,  7,  10, 20, TileType.ELECTRIC_ZONE);  // Static Field     (top-center)
        fillZone(0,  7,  21, 31, TileType.WATER);          // Ripple Creek     (top-right)
        fillZone(8,  15, 0,  9,  TileType.FIRE_ZONE);      // Ember Clearing   (mid-left)
        fillZone(8,  15, 10, 20, TileType.DARK_ZONE);      // Moonlit Ridge    (mid-center)
        fillZone(8,  15, 21, 31, TileType.PSYCHIC_ZONE);   // Sunpetal Meadow  (mid-right)
        fillZone(16, 23, 0,  9,  TileType.FOREST);         // Verdant Canopy   (bot-left)
        fillZone(16, 23, 10, 20, TileType.ICE_ZONE);       // Frostbite Pass   (bot-center)
        fillZone(16, 23, 21, 31, TileType.FAIRY_ZONE);     // Fairy Bloom Gdn  (bot-right)

        // horizontal path separators (full width)
        for (int c = 0; c < MAP_COLS; c++) {
            tiles[8][c]  = TileType.PATH;
            tiles[16][c] = TileType.PATH;
        }
        // vertical path separators (full height)
        for (int r = 0; r < MAP_ROWS; r++) {
            tiles[r][10] = TileType.PATH;
            tiles[r][21] = TileType.PATH;
        }

        // healing machine - at the crossroads where the top path meets the left vertical path
        tiles[8][10] = TileType.HEALING_MACHINE;
    }

    private void fillZone(int r1, int r2, int c1, int c2, TileType type) {
        for (int r = r1; r <= r2; r++)
            for (int c = c1; c <= c2; c++)
                tiles[r][c] = type;
    }

    public TileType tileAt(int worldX, int worldY) {
        int col = worldX / GameWindow.TILE_SIZE;
        int row = worldY / GameWindow.TILE_SIZE;
        if (row < 0 || row >= MAP_ROWS || col < 0 || col >= MAP_COLS)
            return TileType.TREE;
        return tiles[row][col];
    }

    public void updateCamera(int playerX, int playerY) {
        int ts = GameWindow.TILE_SIZE;
        camX = playerX - GameWindow.SCREEN_WIDTH  / 2 + ts / 2;
        camY = playerY - GameWindow.SCREEN_HEIGHT / 2 + ts / 2;

        int maxCamX = MAP_COLS * ts - GameWindow.SCREEN_WIDTH;
        int maxCamY = MAP_ROWS * ts - GameWindow.SCREEN_HEIGHT;
        camX = Math.max(0, Math.min(camX, maxCamX));
        camY = Math.max(0, Math.min(camY, maxCamY));
    }

    public void draw(Graphics2D g2) {
        int ts = GameWindow.TILE_SIZE;
        int startCol = camX / ts;
        int startRow = camY / ts;
        int endCol   = Math.min(startCol + GameWindow.SCREEN_COLS + 1, MAP_COLS);
        int endRow   = Math.min(startRow + GameWindow.SCREEN_ROWS + 1, MAP_ROWS);

        BufferedImage tileset = SpriteLoader.getOverworldTileset();
        int panelW = tileset != null ? tileset.getWidth()  / 3 : 0;
        int panelH = tileset != null ? tileset.getHeight() / 3 : 0;

        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                TileType tile = tiles[r][c];
                int sx = c * ts - camX;
                int sy = r * ts - camY;

                int[] panel = getZonePanelCoord(tile);

                // healing machine - drawn exactly one tile in size
                if (tile == TileType.HEALING_MACHINE) {
                    g2.setColor(new Color(180, 220, 180));
                    g2.fillRect(sx, sy, ts, ts);
                    BufferedImage hm = SpriteLoader.getHealingMachine();
                    if (hm != null) {
                        g2.drawImage(hm, sx, sy, ts, ts, null);
                    }
                } else if (panel != null && tileset != null) {
                    // sample a tile-sized chunk from the panel, tiling across the zone
                    int originX = panel[0] * panelW;
                    int originY = panel[1] * panelH;
                    int srcX = originX + (c * ts) % Math.max(1, panelW - ts);
                    int srcY = originY + (r * ts) % Math.max(1, panelH - ts);
                    g2.drawImage(tileset,
                        sx, sy, sx + ts, sy + ts,
                        srcX, srcY, srcX + ts, srcY + ts, null);
                } else {
                    // fallback to solid color for tiles without a panel
                    g2.setColor(tile.color);
                    g2.fillRect(sx, sy, ts, ts);

                    switch (tile) {
                        case TREE -> {
                            g2.setColor(new Color(10, 60, 10));
                            g2.fillOval(sx + 6, sy + 4, ts - 12, ts - 8);
                            g2.setColor(new Color(100, 70, 30));
                            g2.fillRect(sx + ts/2 - 4, sy + ts - 14, 8, 12);
                        }
                        case WATER -> {
                            g2.setColor(new Color(100, 160, 255, 80));
                            g2.fillRect(sx + 4,      sy + ts/3,     ts/3, 4);
                            g2.fillRect(sx + ts/2+2, sy + ts/3*2-2, ts/4, 4);
                        }
                        default -> {
                            g2.setColor(new Color(0, 0, 0, 20));
                            g2.drawRect(sx, sy, ts, ts);
                        }
                    }
                }
            }
        }
    }

    // Returns true if any tile directly adjacent to the given world position is a healing machine
    public boolean isAdjacentToHealingMachine(int worldX, int worldY) {
        int ts = GameWindow.TILE_SIZE;
        int cx = worldX + ts / 2;
        int cy = worldY + ts / 2;
        return tileAt(cx, cy - ts) == TileType.HEALING_MACHINE
            || tileAt(cx, cy + ts) == TileType.HEALING_MACHINE
            || tileAt(cx - ts, cy) == TileType.HEALING_MACHINE
            || tileAt(cx + ts, cy) == TileType.HEALING_MACHINE;
    }

    // Maps each zone tile to its (col, row) panel in the overworldBackground tileset
    // Tileset is a 3×3 grid of panels matching the in-game zone layout exactly
    private static int[] getZonePanelCoord(TileType tile) {
        return switch (tile) {
            case STARTER_PLAINS -> new int[]{0, 0}; // Eevee Grove     - top-left panel
            case ELECTRIC_ZONE -> new int[]{1, 0}; // Static Field    - top-center panel
            case WATER         -> new int[]{2, 0}; // Ripple Creek    - top-right panel
            case FIRE_ZONE     -> new int[]{0, 1}; // Ember Clearing  - mid-left panel
            case DARK_ZONE     -> new int[]{1, 1}; // Moonlit Ridge   - mid-center panel
            case PSYCHIC_ZONE  -> new int[]{2, 1}; // Sunpetal Meadow - mid-right panel
            case FOREST        -> new int[]{0, 2}; // Verdant Canopy  - bot-left panel
            case ICE_ZONE      -> new int[]{1, 2}; // Frostbite Pass  - bot-center panel
            case FAIRY_ZONE    -> new int[]{2, 2}; // Fairy Bloom Gdn - bot-right panel
            default            -> null;
        };
    }
}

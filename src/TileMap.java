import java.awt.Color;
import java.awt.Graphics2D;

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
        // fill base with grass
        for (int r = 0; r < MAP_ROWS; r++)
            for (int c = 0; c < MAP_COLS; c++)
                tiles[r][c] = TileType.GRASS;

        // border trees
        for (int r = 0; r < MAP_ROWS; r++) {
            tiles[r][0] = TileType.TREE;
            tiles[r][MAP_COLS - 1] = TileType.TREE;
        }
        for (int c = 0; c < MAP_COLS; c++) {
            tiles[0][c] = TileType.TREE;
            tiles[MAP_ROWS - 1][c] = TileType.TREE;
        }

        // main horizontal path across middle
        for (int c = 1; c < MAP_COLS - 1; c++)
            tiles[MAP_ROWS / 2][c] = TileType.PATH;

        // vertical path down center
        for (int r = 1; r < MAP_ROWS - 1; r++)
            tiles[r][MAP_COLS / 2] = TileType.PATH;

        // water area — top-right quadrant
        for (int r = 2; r < 9; r++)
            for (int c = 18; c < 28; c++)
                tiles[r][c] = TileType.WATER;

        // sand border around water
        for (int r = 1; r < 10; r++)
            for (int c = 17; c < 29; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.SAND;

        // forest area — bottom-left quadrant
        for (int r = 14; r < 22; r++)
            for (int c = 2; c < 14; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.FOREST;

        // tree clusters inside forest
        int[][] forestTrees = {
            {15,3},{15,7},{16,11},{17,4},{18,8},{19,3},{20,10},{21,6}
        };
        for (int[] t : forestTrees)
            tiles[t[0]][t[1]] = TileType.TREE;

        // moon shrine — center-ish, small clearing
        for (int r = 9; r < 13; r++)
            for (int c = 13; c < 18; c++)
                tiles[r][c] = TileType.MOON_SHRINE;
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

        for (int r = startRow; r < endRow; r++) {
            for (int c = startCol; c < endCol; c++) {
                TileType tile = tiles[r][c];
                int screenX = c * ts - camX;
                int screenY = r * ts - camY;

                g2.setColor(tile.color);
                g2.fillRect(screenX, screenY, ts, ts);

                // subtle grid lines for grass/path/forest
                if (tile != TileType.TREE && tile != TileType.WATER) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.drawRect(screenX, screenY, ts, ts);
                }

                // tree detail
                if (tile == TileType.TREE) {
                    g2.setColor(new Color(10, 60, 10));
                    g2.fillOval(screenX + 6, screenY + 4, ts - 12, ts - 8);
                    g2.setColor(new Color(100, 70, 30));
                    g2.fillRect(screenX + ts/2 - 4, screenY + ts - 14, 8, 12);
                }

                // water shimmer
                if (tile == TileType.WATER) {
                    g2.setColor(new Color(100, 160, 255, 80));
                    g2.fillRect(screenX + 4,      screenY + ts/3,     ts/3, 4);
                    g2.fillRect(screenX + ts/2+2, screenY + ts/3*2-2, ts/4, 4);
                }

                // moon shrine symbol
                if (tile == TileType.MOON_SHRINE) {
                    g2.setColor(new Color(220, 200, 255));
                    g2.drawOval(screenX + ts/4, screenY + ts/4, ts/2, ts/2);
                    g2.setColor(new Color(255, 240, 180));
                    g2.fillOval(screenX + ts/2 - 4, screenY + ts/2 - 4, 8, 8);
                }
            }
        }
    }
}

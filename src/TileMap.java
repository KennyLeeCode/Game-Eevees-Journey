import java.awt.Color;
import java.awt.Font;
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

        // --- existing zones ---

        // water / Vaporeon — top-right
        for (int r = 2; r < 9; r++)
            for (int c = 18; c < 28; c++)
                tiles[r][c] = TileType.WATER;
        for (int r = 1; r < 10; r++)
            for (int c = 17; c < 29; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.SAND;

        // forest / Leafeon — bottom-left
        for (int r = 14; r < 22; r++)
            for (int c = 2; c < 14; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.FOREST;
        int[][] forestTrees = {{15,3},{15,7},{16,11},{17,4},{18,8},{19,3},{20,10},{21,6}};
        for (int[] t : forestTrees)
            tiles[t[0]][t[1]] = TileType.TREE;

        // moon shrine — center clearing
        for (int r = 9; r < 13; r++)
            for (int c = 13; c < 18; c++)
                tiles[r][c] = TileType.MOON_SHRINE;

        // --- new Eeveelution zones ---

        // ice / Glaceon — top-left
        for (int r = 2; r < 10; r++)
            for (int c = 2; c < 12; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.ICE_ZONE;

        // fire / Flareon — bottom-right
        for (int r = 15; r < 22; r++)
            for (int c = 19; c < 29; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.FIRE_ZONE;

        // electric / Jolteon — top-center
        for (int r = 2; r < 8; r++)
            for (int c = 13; c < 17; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.ELECTRIC_ZONE;

        // dark / Umbreon — bottom-center
        for (int r = 15; r < 22; r++)
            for (int c = 14; c < 18; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.DARK_ZONE;

        // fairy / Sylveon — right-middle
        for (int r = 10; r < 16; r++)
            for (int c = 19; c < 27; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.FAIRY_ZONE;

        // psychic / Espeon — left-middle
        for (int r = 9; r < 14; r++)
            for (int c = 2; c < 12; c++)
                if (tiles[r][c] == TileType.GRASS)
                    tiles[r][c] = TileType.PSYCHIC_ZONE;
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
                int sx = c * ts - camX;
                int sy = r * ts - camY;

                g2.setColor(tile.color);
                g2.fillRect(sx, sy, ts, ts);

                if (tile != TileType.TREE && tile != TileType.WATER) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.drawRect(sx, sy, ts, ts);
                }

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
                    case MOON_SHRINE -> {
                        g2.setColor(new Color(220, 200, 255));
                        g2.drawOval(sx + ts/4, sy + ts/4, ts/2, ts/2);
                        g2.setColor(new Color(255, 240, 180));
                        g2.fillOval(sx + ts/2 - 4, sy + ts/2 - 4, 8, 8);
                    }
                    case ICE_ZONE -> {
                        g2.setColor(new Color(220, 240, 255, 120));
                        g2.fillRect(sx + ts/3, sy + ts/3, ts/3, ts/3);
                    }
                    case FIRE_ZONE -> {
                        g2.setColor(new Color(255, 180, 40, 120));
                        g2.fillOval(sx + ts/3, sy + ts/4, ts/3, ts/2);
                    }
                    case ELECTRIC_ZONE -> {
                        g2.setColor(new Color(255, 255, 100, 140));
                        int[] xp = {sx+ts/2, sx+ts/3, sx+ts/2, sx+ts*2/3};
                        int[] yp = {sy+4,    sy+ts/2, sy+ts/2, sy+ts-4};
                        g2.drawPolyline(xp, yp, 4);
                    }
                    case DARK_ZONE -> {
                        g2.setColor(new Color(180, 140, 255, 100));
                        g2.fillOval(sx + ts/4, sy + ts/4, ts/2, ts/2);
                    }
                    case FAIRY_ZONE -> {
                        g2.setColor(new Color(255, 200, 220, 130));
                        g2.fillOval(sx + ts/2 - 4, sy + ts/2 - 4, 8, 8);
                        g2.fillOval(sx + ts/4,     sy + ts/4,     6, 6);
                        g2.fillOval(sx + ts*3/4-6, sy + ts/4,     6, 6);
                    }
                    case PSYCHIC_ZONE -> {
                        g2.setColor(new Color(255, 180, 255, 120));
                        g2.drawOval(sx + ts/4,     sy + ts/4,     ts/2, ts/2);
                        g2.drawOval(sx + ts/4 + 3, sy + ts/4 + 3, ts/2 - 6, ts/2 - 6);
                    }
                    default -> {}
                }
            }
        }

        drawLegend(g2);
    }

    private void drawLegend(Graphics2D g2) {
        int x = 8, y = 8;
        int sw = 12, sh = 12, pad = 2;
        g2.setFont(new Font("Arial", Font.PLAIN, 10));

        String[] labels = {"Vaporeon","Leafeon","Glaceon","Flareon","Jolteon","Umbreon","Sylveon","Espeon"};
        TileType[] zones = {TileType.WATER, TileType.FOREST, TileType.ICE_ZONE, TileType.FIRE_ZONE,
                            TileType.ELECTRIC_ZONE, TileType.DARK_ZONE, TileType.FAIRY_ZONE, TileType.PSYCHIC_ZONE};

        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(x - 4, y - 4, 100, labels.length * (sh + pad) + 8, 6, 6);

        for (int i = 0; i < labels.length; i++) {
            int iy = y + i * (sh + pad);
            g2.setColor(zones[i].color);
            g2.fillRect(x, iy, sw, sh);
            g2.setColor(Color.WHITE);
            g2.drawString(labels[i], x + sw + 4, iy + sh - 2);
        }
    }
}

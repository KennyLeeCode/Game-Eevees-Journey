import java.awt.Color;
import java.awt.Graphics2D;

public class Player {

    public int worldX, worldY;
    private final int speed = 4;
    private final KeyHandler keys;

    // facing direction: 0=down 1=up 2=left 3=right
    private int direction = 0;

    public Player(KeyHandler keys) {
        this.keys = keys;
        // start at center of world map
        worldX = (TileMap.MAP_COLS / 2) * GameWindow.TILE_SIZE;
        worldY = (TileMap.MAP_ROWS / 2) * GameWindow.TILE_SIZE;
    }

    public void update(TileMap map) {
        int nextX = worldX;
        int nextY = worldY;

        if (keys.up)    { nextY -= speed; direction = 1; }
        if (keys.down)  { nextY += speed; direction = 0; }
        if (keys.left)  { nextX -= speed; direction = 2; }
        if (keys.right) { nextX += speed; direction = 3; }

        // collision: check all four corners of player hitbox
        int ts = GameWindow.TILE_SIZE;
        int margin = 4;
        boolean blocked = false;

        int[] checkX = { nextX + margin, nextX + ts - margin - 1, nextX + margin, nextX + ts - margin - 1 };
        int[] checkY = { nextY + margin, nextY + margin, nextY + ts - margin - 1, nextY + ts - margin - 1 };

        for (int i = 0; i < 4; i++) {
            if (map.tileAt(checkX[i], checkY[i]).solid) { blocked = true; break; }
        }

        if (!blocked) {
            worldX = nextX;
            worldY = nextY;
        }
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int ts = GameWindow.TILE_SIZE;
        int screenX = worldX - camX;
        int screenY = worldY - camY;

        // head
        g2.setColor(new Color(220, 180, 120));
        g2.fillOval(screenX + ts / 4, screenY, ts / 2, ts / 2);

        // torso
        g2.setColor(new Color(60, 100, 200));
        g2.fillRect(screenX + ts / 4, screenY + ts / 2, ts / 2, ts / 2);

        // direction dot
        g2.setColor(Color.WHITE);
        int dotSize = 6;
        int cx = screenX + ts / 2;
        int cy = screenY + ts / 4;
        switch (direction) {
            case 0 -> g2.fillOval(cx - dotSize / 2, cy + dotSize,     dotSize, dotSize);
            case 1 -> g2.fillOval(cx - dotSize / 2, cy - dotSize,     dotSize, dotSize);
            case 2 -> g2.fillOval(cx - dotSize - 2, cy - dotSize / 2, dotSize, dotSize);
            case 3 -> g2.fillOval(cx + 2,           cy - dotSize / 2, dotSize, dotSize);
        }
    }
}

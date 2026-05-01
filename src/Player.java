import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Player {

    public int worldX, worldY;
    private final int speed = 4;
    private final KeyHandler keys;

    // facing direction: 0=down 1=up 2=left 3=right
    private int direction = 0;
    private boolean moving = false;

    // animation
    private int animFrame  = 0;
    private int animTimer  = 0;
    private static final int ANIM_SPEED = 10; // frames per animation step

    public Player(KeyHandler keys) {
        this.keys = keys;
        worldX = (TileMap.MAP_COLS / 2) * GameWindow.TILE_SIZE;
        worldY = (TileMap.MAP_ROWS / 2) * GameWindow.TILE_SIZE;
    }

    public boolean update(TileMap map) {
        int nextX = worldX;
        int nextY = worldY;
        moving = false;

        if (keys.up)    { nextY -= speed; direction = 1; moving = true; }
        if (keys.down)  { nextY += speed; direction = 0; moving = true; }
        if (keys.left)  { nextX -= speed; direction = 2; moving = true; }
        if (keys.right) { nextX += speed; direction = 3; moving = true; }

        int ts = GameWindow.TILE_SIZE;
        int margin = 4;
        boolean blocked = false;

        int[] checkX = { nextX + margin, nextX + ts - margin - 1, nextX + margin, nextX + ts - margin - 1 };
        int[] checkY = { nextY + margin, nextY + margin, nextY + ts - margin - 1, nextY + ts - margin - 1 };

        for (int i = 0; i < 4; i++) {
            if (map.tileAt(checkX[i], checkY[i]).solid) { blocked = true; break; }
        }

        if (!blocked && (nextX != worldX || nextY != worldY)) {
            worldX = nextX;
            worldY = nextY;

            animTimer++;
            if (animTimer >= ANIM_SPEED) {
                animTimer = 0;
                animFrame = (animFrame + 1) % 3;
            }
            return true;
        }

        animFrame = 0;
        animTimer = 0;
        return false;
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int ts = GameWindow.TILE_SIZE;
        int screenX = worldX - camX;
        int screenY = worldY - camY;

        BufferedImage sheet = SpriteLoader.getTrainer();
        if (sheet != null) {
            int fw = sheet.getWidth()  / 3;
            int fh = sheet.getHeight() / 2;

            // row 0 = front-facing, row 1 = back-facing
            int row = (direction == 1) ? 1 : 0;
            int col = moving ? animFrame : 0;

            BufferedImage frame = sheet.getSubimage(col * fw, row * fh, fw, fh);

            if (direction == 2) {
                // mirror horizontally for left
                g2.drawImage(frame, screenX + ts, screenY, -ts, ts, null);
            } else {
                g2.drawImage(frame, screenX, screenY, ts, ts, null);
            }
        } else {
            // fallback shapes if sprite fails to load
            g2.setColor(new Color(220, 180, 120));
            g2.fillOval(screenX + ts / 4, screenY, ts / 2, ts / 2);
            g2.setColor(new Color(60, 100, 200));
            g2.fillRect(screenX + ts / 4, screenY + ts / 2, ts / 2, ts / 2);
        }
    }

    // returns the front-idle frame for use in the battle screen
    public BufferedImage getBattleSprite() {
        BufferedImage sheet = SpriteLoader.getTrainer();
        if (sheet == null) return null;
        int fw = sheet.getWidth()  / 3;
        int fh = sheet.getHeight() / 2;
        return sheet.getSubimage(0, fh, fw, fh); // back-facing idle (row 1, col 0)
    }
}

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// The player character — handles movement, collision, and sprite animation
public class Player {

    public int worldX, worldY; // position in world pixels (not tile coordinates)
    private final int speed = 4; // pixels moved per frame
    private final KeyHandler keys;

    // facing direction: 0=down  1=up  2=left  3=right
    private int     direction = 0;
    private boolean moving    = false;

    // sprite sheet animation — cycles through 3 frames while walking
    private int animFrame = 0;
    private int animTimer = 0;
    private static final int ANIM_SPEED = 10; // frames of game time per animation step

    public Player(KeyHandler keys) {
        this.keys = keys;
        // spawn at the center of the world map
        worldX = (TileMap.MAP_COLS / 2) * GameWindow.TILE_SIZE;
        worldY = (TileMap.MAP_ROWS / 2) * GameWindow.TILE_SIZE;
    }

    // Moves the player and returns true if they actually moved (used to trigger encounter checks)
    public boolean update(TileMap map) {
        int nextX = worldX;
        int nextY = worldY;
        moving = false;

        if (keys.up)    { nextY -= speed; direction = 1; moving = true; }
        if (keys.down)  { nextY += speed; direction = 0; moving = true; }
        if (keys.left)  { nextX -= speed; direction = 2; moving = true; }
        if (keys.right) { nextX += speed; direction = 3; moving = true; }

        // check all four corners of the player's hitbox before allowing the move
        int ts     = GameWindow.TILE_SIZE;
        int margin = 4; // shrinks hitbox slightly so player doesn't get stuck on corners
        boolean blocked = false;

        int[] checkX = { nextX + margin, nextX + ts - margin - 1, nextX + margin, nextX + ts - margin - 1 };
        int[] checkY = { nextY + margin, nextY + margin, nextY + ts - margin - 1, nextY + ts - margin - 1 };

        for (int i = 0; i < 4; i++) {
            if (map.tileAt(checkX[i], checkY[i]).solid) { blocked = true; break; }
        }

        if (!blocked && (nextX != worldX || nextY != worldY)) {
            worldX = nextX;
            worldY = nextY;

            // advance animation frame every ANIM_SPEED game frames
            animTimer++;
            if (animTimer >= ANIM_SPEED) {
                animTimer = 0;
                animFrame = (animFrame + 1) % 3;
            }
            return true;
        }

        // reset to idle frame when not moving
        animFrame = 0;
        animTimer = 0;
        return false;
    }

    public void draw(Graphics2D g2, int camX, int camY) {
        int ts      = GameWindow.TILE_SIZE;
        int screenX = worldX - camX;
        int screenY = worldY - camY;

        BufferedImage sheet = SpriteLoader.getTrainer();
        if (sheet != null) {
            // sprite sheet is 3 cols × 2 rows
            int fw = sheet.getWidth()  / 3;
            int fh = sheet.getHeight() / 2;

            // row 0 = front-facing (down/left/right), row 1 = back-facing (up)
            int row = (direction == 1) ? 1 : 0;
            int col = moving ? animFrame : 0;

            BufferedImage frame = sheet.getSubimage(col * fw, row * fh, fw, fh);

            if (direction == 2) {
                // flip the right-facing sprite horizontally to get the left-facing version
                g2.drawImage(frame, screenX + ts, screenY, -ts, ts, null);
            } else {
                g2.drawImage(frame, screenX, screenY, ts, ts, null);
            }
        } else {
            // fallback: draw simple shapes if the sprite file failed to load
            g2.setColor(new Color(220, 180, 120));
            g2.fillOval(screenX + ts / 4, screenY, ts / 2, ts / 2);
            g2.setColor(new Color(60, 100, 200));
            g2.fillRect(screenX + ts / 4, screenY + ts / 2, ts / 2, ts / 2);
        }
    }

    // Returns the back-idle frame used on the battle screen (player faces the enemy)
    public BufferedImage getBattleSprite() {
        BufferedImage sheet = SpriteLoader.getTrainer();
        if (sheet == null) return null;
        int fw = sheet.getWidth()  / 3;
        int fh = sheet.getHeight() / 2;
        return sheet.getSubimage(0, fh, fw, fh); // row 1, col 0
    }
}

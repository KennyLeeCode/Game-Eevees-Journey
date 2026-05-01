import java.awt.Color;
import java.awt.Graphics2D;

public class Player {

    public int x, y;
    private final int speed = 4;
    private final KeyHandler keys;

    // facing direction for drawing: 0=down 1=up 2=left 3=right
    private int direction = 0;

    public Player(KeyHandler keys) {
        this.keys = keys;
        // start near center of screen
        x = GameWindow.SCREEN_WIDTH  / 2 - GameWindow.TILE_SIZE / 2;
        y = GameWindow.SCREEN_HEIGHT / 2 - GameWindow.TILE_SIZE / 2;
    }

    public void update() {
        if (keys.up)    { y -= speed; direction = 1; }
        if (keys.down)  { y += speed; direction = 0; }
        if (keys.left)  { x -= speed; direction = 2; }
        if (keys.right) { x += speed; direction = 3; }

        // clamp to screen bounds
        x = Math.max(0, Math.min(x, GameWindow.SCREEN_WIDTH  - GameWindow.TILE_SIZE));
        y = Math.max(0, Math.min(y, GameWindow.SCREEN_HEIGHT - GameWindow.TILE_SIZE));
    }

    public void draw(Graphics2D g2) {
        int ts = GameWindow.TILE_SIZE;

        // body
        g2.setColor(new Color(220, 180, 120));
        g2.fillOval(x + ts / 4, y, ts / 2, ts / 2);  // head

        g2.setColor(new Color(60, 100, 200));
        g2.fillRect(x + ts / 4, y + ts / 2, ts / 2, ts / 2);  // torso

        // direction indicator (small white dot shows which way player faces)
        g2.setColor(Color.WHITE);
        int dotSize = 6;
        int cx = x + ts / 2;
        int cy = y + ts / 4;
        switch (direction) {
            case 0 -> g2.fillOval(cx - dotSize / 2, cy + dotSize,     dotSize, dotSize); // down
            case 1 -> g2.fillOval(cx - dotSize / 2, cy - dotSize,     dotSize, dotSize); // up
            case 2 -> g2.fillOval(cx - dotSize - 2, cy - dotSize / 2, dotSize, dotSize); // left
            case 3 -> g2.fillOval(cx + 2,           cy - dotSize / 2, dotSize, dotSize); // right
        }
    }
}

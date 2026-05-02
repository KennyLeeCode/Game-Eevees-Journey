import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// Shown when the player has caught all 9 Eeveelutions
public class WinScreen {

    private int timer = 0; // counts up each frame for animations

    public void update() {
        timer++;
    }

    public void draw(Graphics2D g2, Collection collection) {
        // deep purple background
        g2.setColor(new Color(10, 5, 30));
        g2.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

        drawStars(g2);
        drawTitle(g2);
        drawSprites(g2, collection);
        drawSubtitle(g2);
    }

    // simple twinkling star field using the timer as a seed offset
    private void drawStars(Graphics2D g2) {
        int[][] stars = {
            {80,40},{200,90},{350,30},{500,70},{650,50},{720,110},
            {130,180},{400,160},{580,140},{740,200},{60,250},{310,240}
        };
        for (int[] s : stars) {
            int alpha = 120 + (int)(100 * Math.abs(Math.sin(timer * 0.03 + s[0])));
            g2.setColor(new Color(255, 255, 220, Math.min(alpha, 255)));
            g2.fillOval(s[0], s[1], 3, 3);
        }
    }

    private void drawTitle(Graphics2D g2) {
        // gentle floating effect using a slow sine wave
        int floatY = (int)(6 * Math.sin(timer * 0.04));

        g2.setFont(new Font("Arial", Font.BOLD, 42));
        String line1 = "You caught them all!";
        int x1 = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(line1)) / 2;
        g2.setColor(new Color(255, 230, 80));
        g2.drawString(line1, x1, 100 + floatY);

        g2.setFont(new Font("Arial", Font.BOLD, 22));
        String line2 = "Your journey through all the eevee regions is complete!";
        int x2 = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(line2)) / 2;
        g2.setColor(new Color(200, 180, 255));
        g2.drawString(line2, x2, 140 + floatY);
    }

    // show all 9 Eeveelution sprites in a row across the center of the screen
    private void drawSprites(Graphics2D g2, Collection collection) {
        Eeveelution[] all = Eeveelution.values();
        int spriteSize = 64;
        int totalW     = all.length * (spriteSize + 8) - 8;
        int startX     = (GameWindow.SCREEN_WIDTH - totalW) / 2;
        int y          = 200;

        for (int i = 0; i < all.length; i++) {
            int sx = startX + i * (spriteSize + 8);

            // each sprite bobs at a slightly different phase
            int bobY = y + (int)(8 * Math.sin(timer * 0.05 + i * 0.7));

            // glow circle behind each sprite
            g2.setColor(new Color(
                all[i].color.getRed(),
                all[i].color.getGreen(),
                all[i].color.getBlue(), 80));
            g2.fillOval(sx - 4, bobY - 4, spriteSize + 8, spriteSize + 8);

            BufferedImage sprite = SpriteLoader.getEeveelution(all[i]);
            if (sprite != null) {
                g2.drawImage(sprite, sx, bobY, spriteSize, spriteSize, null);
            } else {
                g2.setColor(all[i].color);
                g2.fillOval(sx + 8, bobY + 8, spriteSize - 16, spriteSize - 16);
            }

            // name under each sprite
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            int nameX = sx + (spriteSize - g2.getFontMetrics().stringWidth(all[i].name)) / 2;
            g2.drawString(all[i].name, nameX, bobY + spriteSize + 12);
        }
    }

    private void drawSubtitle(Graphics2D g2) {
        // flash "Press ENTER" on and off
        if ((timer / 40) % 2 == 0) {
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            g2.setColor(new Color(200, 200, 200));
            String msg = "Press ENTER to play again";
            int x = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(msg)) / 2;
            g2.drawString(msg, x, GameWindow.SCREEN_HEIGHT - 40);
        }
    }
}

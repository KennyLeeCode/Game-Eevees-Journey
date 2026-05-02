import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// Draws the full-screen collection overlay showing all Eeveelutions and their caught status
public class CollectionScreen {

    // Number of columns to lay the Eeveelution cards out in
    private static final int COLS       = 3;
    private static final int CARD_W     = 200;
    private static final int CARD_H     = 80;
    private static final int CARD_PAD   = 20;

    public void draw(Graphics2D g2, Collection collection) {
        // dark semi-transparent background over the map
        g2.setColor(new Color(10, 5, 25, 230));
        g2.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

        // title
        g2.setFont(new Font("Arial", Font.BOLD, 22));
        g2.setColor(Color.WHITE);
        String title = "Collection  (" + collection.count() + "/" + collection.total() + ")";
        int titleX = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, 44);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(new Color(160, 160, 160));
        String hint = "Press C to close";
        g2.drawString(hint, (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(hint)) / 2, 64);

        Eeveelution[] all = Eeveelution.values();

        // total grid width so cards sit centered on screen
        int totalW = COLS * CARD_W + (COLS - 1) * CARD_PAD;
        int startX = (GameWindow.SCREEN_WIDTH - totalW) / 2;
        int startY = 84;

        for (int i = 0; i < all.length; i++) {
            Eeveelution e    = all[i];
            boolean     have = collection.has(e);

            int col   = i % COLS;
            int row   = i / COLS;
            int cardX = startX + col * (CARD_W + CARD_PAD);
            int cardY = startY + row * (CARD_H + CARD_PAD);

            drawCard(g2, e, have, cardX, cardY);
        }
    }

    private void drawCard(Graphics2D g2, Eeveelution e, boolean caught, int x, int y) {
        // card background - bright if caught, dim if not
        Color bgColor = caught
            ? new Color(e.color.getRed() / 4, e.color.getGreen() / 4, e.color.getBlue() / 4, 220)
            : new Color(30, 25, 45, 200);
        g2.setColor(bgColor);
        g2.fillRoundRect(x, y, CARD_W, CARD_H, 10, 10);

        // border - colored if caught, grey if not
        g2.setColor(caught ? e.color : new Color(70, 60, 90));
        g2.drawRoundRect(x, y, CARD_W, CARD_H, 10, 10);

        // sprite or placeholder circle on the left of the card
        int spriteSize = 56;
        int spriteX    = x + 10;
        int spriteY    = y + (CARD_H - spriteSize) / 2;

        if (caught) {
            BufferedImage sprite = SpriteLoader.getEeveelution(e);
            if (sprite != null) {
                g2.drawImage(sprite, spriteX, spriteY, spriteSize, spriteSize, null);
            } else {
                g2.setColor(e.color);
                g2.fillOval(spriteX, spriteY, spriteSize, spriteSize);
            }
        } else {
            // show a silhouette (dark circle) for uncaught Eeveelutions
            g2.setColor(new Color(50, 45, 65));
            g2.fillOval(spriteX, spriteY, spriteSize, spriteSize);
            g2.setColor(new Color(80, 70, 100));
            g2.drawString("?", spriteX + spriteSize / 2 - 4, spriteY + spriteSize / 2 + 5);
        }

        // name and status text
        int textX = spriteX + spriteSize + 10;
        int textY = y + 28;

        g2.setFont(new Font("Arial", Font.BOLD, 15));
        g2.setColor(caught ? Color.WHITE : new Color(100, 90, 120));
        g2.drawString(caught ? e.name : "???", textX, textY);

        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(caught ? new Color(160, 220, 160) : new Color(140, 100, 100));
        g2.drawString(caught ? "Caught" : "Not caught", textX, textY + 18);

        // zone label
        g2.setColor(caught ? new Color(e.color.getRed(), e.color.getGreen(), e.color.getBlue(), 200)
                           : new Color(80, 70, 100));
        g2.drawString(e.zone.name().replace("_", " ").toLowerCase(), textX, textY + 34);
    }
}

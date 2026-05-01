import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.util.Random;

// Handles everything that happens during a battle:
// drawing the UI, processing player input, and running the catch/attack/run logic
public class BattleScreen {

    // The result of the battle — GamePanel reads this to decide what to do next
    public enum Outcome { NONE, FLED, CAUGHT, FAINTED }

    private static final int      MAX_PLAYER_HP = 50;
    private static final int      MENU_Y        = 360; // y position where the bottom panel starts
    private static final int[]    OPTION_X      = {120, 340, 560};
    private static final String[] OPTIONS       = {"ATTACK", "CATCH", "RUN"};

    private final Eeveelution eeveelution;
    private final Random      rng = new Random();

    private int    enemyHp;
    private int    playerHp;
    private int    selectedOption = 0;

    private String  logLine1     = "What will you do?";
    private String  logLine2     = "";
    private Outcome outcome      = Outcome.NONE;
    private boolean waitingInput = true;
    private int     outcomePause = 0; // frames to wait after battle ends before returning to map

    public BattleScreen(Eeveelution e, int currentPlayerHp) {
        this.eeveelution = e;
        this.enemyHp     = e.maxHp;
        this.playerHp    = currentPlayerHp;
    }

    // Called by GamePanel when a key is pressed during battle
    public void handleKey(int keyCode) {
        if (!waitingInput) return;
        if (keyCode == KeyEvent.VK_LEFT  && selectedOption > 0) selectedOption--;
        if (keyCode == KeyEvent.VK_RIGHT && selectedOption < 2) selectedOption++;
        if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_Z) confirm();
    }

    private void confirm() {
        waitingInput = false;
        switch (selectedOption) {
            case 0 -> doAttack();
            case 1 -> doCatch();
            case 2 -> doRun();
        }
    }

    private void doAttack() {
        int dmg = 8 + rng.nextInt(8); // deals 8–15 damage
        enemyHp  = Math.max(0, enemyHp - dmg);
        logLine1 = "You attacked! Dealt " + dmg + " damage.";

        if (enemyHp <= 0) {
            // enemy fainted but wasn't caught — treat as fled
            logLine2 = eeveelution.name + " fainted! It got away...";
            endBattle(Outcome.FLED);
            return;
        }

        // enemy attacks back after the player's turn
        int edamage = 5 + rng.nextInt(6); // deals 5–10 damage
        playerHp = Math.max(0, playerHp - edamage);
        logLine2 = eeveelution.name + " hit back! Took " + edamage + " damage.";

        if (playerHp <= 0) {
            logLine2 = "You fainted! Fleeing...";
            playerHp = MAX_PLAYER_HP; // restore HP so the player can keep exploring
            endBattle(Outcome.FAINTED);
            return;
        }
        waitingInput = true;
    }

    private void doCatch() {
        // catch chance scales with how much HP the enemy has lost
        // 10% at full HP → up to 80% at 1 HP remaining
        double catchChance = 0.10 + 0.70 * (1.0 - (double) enemyHp / eeveelution.maxHp);
        if (rng.nextDouble() < catchChance) {
            logLine1 = "Gotcha! " + eeveelution.name + " was caught!";
            logLine2 = "";
            endBattle(Outcome.CAUGHT);
        } else {
            logLine1 = "Oh no! " + eeveelution.name + " broke free!";
            int edamage = 5 + rng.nextInt(6);
            playerHp = Math.max(0, playerHp - edamage);
            logLine2 = eeveelution.name + " hit back! Took " + edamage + " damage.";
            if (playerHp <= 0) {
                playerHp = MAX_PLAYER_HP;
                endBattle(Outcome.FAINTED);
                return;
            }
            waitingInput = true;
        }
    }

    private void doRun() {
        logLine1 = "Got away safely!";
        logLine2 = "";
        endBattle(Outcome.FLED);
    }

    private void endBattle(Outcome o) {
        outcome      = o;
        outcomePause = 120; // wait 2 seconds (at 60fps) so the player can read the result
    }

    // Counts down the pause timer after battle ends
    public void update() {
        if (outcome != Outcome.NONE && outcomePause > 0) outcomePause--;
    }

    // GamePanel calls this to know when it's safe to leave the battle screen
    public boolean isReadyToTransition() {
        return outcome != Outcome.NONE && outcomePause <= 0;
    }

    public Outcome     getOutcome()     { return outcome; }
    public int         getPlayerHp()    { return playerHp; }
    public Eeveelution getEeveelution() { return eeveelution; }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(20, 10, 40));
        g2.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

        drawBattlefield(g2);
        drawMenuPanel(g2);
    }

    private void drawBattlefield(Graphics2D g2) {
        g2.setColor(new Color(40, 30, 70));
        g2.fillRect(0, 260, GameWindow.SCREEN_WIDTH, 100);

        // enemy sprite on the right
        BufferedImage enemySprite = SpriteLoader.getEeveelution(eeveelution);
        int eSize = 180;
        int eX    = GameWindow.SCREEN_WIDTH - eSize - 60;
        int eY    = 80;
        if (enemySprite != null) {
            g2.drawImage(enemySprite, eX, eY, eSize, eSize, null);
        } else {
            g2.setColor(eeveelution.color);
            g2.fillOval(eX + 20, eY + 20, eSize - 40, eSize - 40);
        }

        drawNameHpBox(g2, eeveelution.name, enemyHp, eeveelution.maxHp, 40, 60, eeveelution.color);

        // player sprite on the left (back-facing, as if looking at the enemy)
        BufferedImage playerSprite = getPlayerBackSprite();
        int pSize = 140, pX = 60, pY = 160;
        if (playerSprite != null) {
            g2.drawImage(playerSprite, pX, pY, pSize, pSize, null);
        } else {
            g2.setColor(new Color(60, 100, 200));
            g2.fillRect(pX + 20, pY + 20, pSize - 40, pSize - 40);
        }
    }

    private void drawMenuPanel(Graphics2D g2) {
        g2.setColor(new Color(15, 10, 30));
        g2.fillRect(0, MENU_Y, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT - MENU_Y);
        g2.setColor(new Color(80, 60, 120));
        g2.drawRect(0, MENU_Y, GameWindow.SCREEN_WIDTH - 1, GameWindow.SCREEN_HEIGHT - MENU_Y - 1);

        // battle log — two lines so both the player action and enemy response are visible
        g2.setFont(new Font("Arial", Font.PLAIN, 15));
        g2.setColor(Color.WHITE);
        g2.drawString(logLine1, 24, MENU_Y + 30);
        g2.setColor(new Color(200, 200, 200));
        g2.drawString(logLine2, 24, MENU_Y + 54);

        if (waitingInput) {
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            for (int i = 0; i < OPTIONS.length; i++) {
                boolean sel = (i == selectedOption);
                g2.setColor(sel ? Color.YELLOW : new Color(180, 180, 180));
                g2.drawString((sel ? "> " : "  ") + OPTIONS[i], OPTION_X[i], MENU_Y + 100);
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            g2.setColor(new Color(140, 140, 140));
            g2.drawString("LEFT / RIGHT to choose   ENTER to confirm", 24, MENU_Y + 125);
        }

        drawHpBar(g2, "You", playerHp, MAX_PLAYER_HP,
                  GameWindow.SCREEN_WIDTH - 260, MENU_Y + 155, new Color(100, 220, 100));
    }

    private void drawNameHpBox(Graphics2D g2, String name, int hp, int maxHp, int x, int y, Color barColor) {
        int boxW = 220, boxH = 56;
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(x, y, boxW, boxH, 8, 8);
        g2.setColor(new Color(100, 80, 140));
        g2.drawRoundRect(x, y, boxW, boxH, 8, 8);

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.WHITE);
        g2.drawString(name, x + 10, y + 20);

        drawHpBar(g2, "HP", hp, maxHp, x + 10, y + 30, barColor);
    }

    private void drawHpBar(Graphics2D g2, String label, int hp, int maxHp, int x, int y, Color fill) {
        int    barW  = 200, barH = 12;
        double ratio = (double) hp / maxHp;

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(new Color(180, 180, 180));
        g2.drawString(label + ": " + hp + "/" + maxHp, x, y + barH);

        int labelW = g2.getFontMetrics().stringWidth(label + ": " + hp + "/" + maxHp) + 8;
        int bx     = x + labelW;

        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(bx, y, barW, barH, 4, 4);

        // bar turns yellow below 50% and red below 20%
        Color barColor = ratio > 0.5 ? fill
                       : ratio > 0.2 ? new Color(240, 200, 40)
                                     : new Color(220, 60, 60);
        g2.setColor(barColor);
        g2.fillRoundRect(bx, y, (int)(barW * ratio), barH, 4, 4);

        g2.setColor(new Color(100, 100, 100));
        g2.drawRoundRect(bx, y, barW, barH, 4, 4);
    }

    private BufferedImage getPlayerBackSprite() {
        BufferedImage sheet = SpriteLoader.getTrainer();
        if (sheet == null) return null;
        int fw = sheet.getWidth()  / 3;
        int fh = sheet.getHeight() / 2;
        return sheet.getSubimage(0, fh, fw, fh); // row 1, col 0 = back-facing idle
    }
}

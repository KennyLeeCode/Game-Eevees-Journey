import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private static final int FPS = 60;
    private Thread gameThread;

    private final KeyHandler       keyHandler       = new KeyHandler();
    private final TileMap          tileMap          = new TileMap();
    private final Player           player           = new Player(keyHandler);
    private final EncounterManager encounterManager = new EncounterManager();
    private final Collection       collection       = new Collection();

    private GameState    state            = GameState.EXPLORE;
    private Eeveelution  currentEncounter = null;
    private BattleScreen battleScreen     = null;
    private int          playerHp         = 50;

    private int    encounterTimer            = 0;
    private static final int ENCOUNTER_FLASH_FRAMES = 90;

    // caught toast
    private String caughtToastMsg  = "";
    private int    caughtToastTimer = 0;
    private static final int TOAST_DURATION = 180; // 3 seconds

    public GamePanel() {
        setPreferredSize(new Dimension(GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyHandler);
        addKeyListener(this);

        startGameThread();
    }

    private void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        if (caughtToastTimer > 0) caughtToastTimer--;

        switch (state) {
            case EXPLORE -> {
                boolean moved = player.update(tileMap);
                tileMap.updateCamera(player.worldX, player.worldY);
                if (moved) {
                    TileType tile = tileMap.tileAt(
                        player.worldX + GameWindow.TILE_SIZE / 2,
                        player.worldY + GameWindow.TILE_SIZE / 2);
                    if (encounterManager.onStep(tile, collection)) {
                        currentEncounter = encounterManager.getPending();
                        encounterManager.clearPending();
                        state = GameState.ENCOUNTER;
                        encounterTimer = 0;
                    }
                }
            }
            case ENCOUNTER -> {
                encounterTimer++;
                if (encounterTimer >= ENCOUNTER_FLASH_FRAMES) {
                    battleScreen = new BattleScreen(currentEncounter, playerHp);
                    state = GameState.BATTLE;
                }
            }
            case BATTLE -> {
                battleScreen.update();
                if (battleScreen.isReadyToTransition()) {
                    playerHp = battleScreen.getPlayerHp();
                    if (battleScreen.getOutcome() == BattleScreen.Outcome.CAUGHT) {
                        collection.add(battleScreen.getEeveelution());
                        caughtToastMsg   = battleScreen.getEeveelution().name + " added to collection!  ("
                                         + collection.count() + "/" + collection.total() + ")";
                        caughtToastTimer = TOAST_DURATION;
                    }
                    state = GameState.EXPLORE;
                    battleScreen = null;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (state) {
            case EXPLORE -> {
                tileMap.draw(g2);
                player.draw(g2, tileMap.camX, tileMap.camY);
                drawZoneHint(g2);
                drawPlayerHp(g2);
                drawCollectionCounter(g2);
                if (caughtToastTimer > 0) drawCaughtToast(g2);
            }
            case ENCOUNTER -> {
                tileMap.draw(g2);
                player.draw(g2, tileMap.camX, tileMap.camY);
                drawEncounterFlash(g2);
            }
            case BATTLE -> {
                if (battleScreen != null) battleScreen.draw(g2);
            }
        }

        g2.dispose();
    }

    private void drawZoneHint(Graphics2D g2) {
        TileType tile = tileMap.tileAt(
            player.worldX + GameWindow.TILE_SIZE / 2,
            player.worldY + GameWindow.TILE_SIZE / 2);
        Eeveelution here = Eeveelution.forZone(tile);
        if (here == null) return;

        String hint = collection.has(here) ? here.name + " [caught]" : here.name + " may appear!";
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        int w = g2.getFontMetrics().stringWidth(hint) + 16;
        int x = (GameWindow.SCREEN_WIDTH - w) / 2;
        int y = GameWindow.SCREEN_HEIGHT - 36;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x, y, w, 24, 8, 8);
        g2.setColor(collection.has(here) ? new Color(160, 160, 160) : here.color);
        g2.drawString(hint, x + 8, y + 17);
    }

    private void drawPlayerHp(Graphics2D g2) {
        int x = GameWindow.SCREEN_WIDTH - 180;
        int y = GameWindow.SCREEN_HEIGHT - 36;
        int barW = 120, barH = 10;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x - 8, y - 14, 168, 28, 8, 8);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        g2.drawString("HP: " + playerHp + "/50", x, y);

        double ratio = playerHp / 50.0;
        g2.setColor(new Color(60, 60, 60));
        g2.fillRoundRect(x, y + 4, barW, barH, 4, 4);
        g2.setColor(ratio > 0.5 ? new Color(80, 200, 80)
                  : ratio > 0.2 ? new Color(220, 180, 40)
                                : new Color(200, 60, 60));
        g2.fillRoundRect(x, y + 4, (int)(barW * ratio), barH, 4, 4);
    }

    private void drawCollectionCounter(Graphics2D g2) {
        String text = "Caught: " + collection.count() + "/" + collection.total();
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        int w = g2.getFontMetrics().stringWidth(text) + 16;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(8, 8, w, 22, 6, 6);
        g2.setColor(Color.WHITE);
        g2.drawString(text, 16, 23);
    }

    private void drawCaughtToast(Graphics2D g2) {
        float alpha = Math.min(1f, caughtToastTimer / 30f); // fade in/out
        int a = (int)(200 * alpha);

        g2.setFont(new Font("Arial", Font.BOLD, 15));
        int w = g2.getFontMetrics().stringWidth(caughtToastMsg) + 24;
        int x = (GameWindow.SCREEN_WIDTH - w) / 2;
        int y = 60;

        g2.setColor(new Color(30, 160, 80, a));
        g2.fillRoundRect(x, y, w, 28, 10, 10);
        g2.setColor(new Color(255, 255, 255, a));
        g2.drawString(caughtToastMsg, x + 12, y + 20);
    }

    private void drawEncounterFlash(Graphics2D g2) {
        int alpha = (int)(160 * Math.abs(Math.sin(encounterTimer * 0.15)));
        g2.setColor(new Color(
            currentEncounter.color.getRed(),
            currentEncounter.color.getGreen(),
            currentEncounter.color.getBlue(), alpha));
        g2.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String msg = "A wild " + currentEncounter.name + " appeared!";
        int x = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(msg)) / 2;
        g2.drawString(msg, x, GameWindow.SCREEN_HEIGHT / 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (state == GameState.BATTLE && battleScreen != null) {
            battleScreen.handleKey(e.getKeyCode());
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

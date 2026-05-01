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

    private final KeyHandler      keyHandler       = new KeyHandler();
    private final TileMap         tileMap          = new TileMap();
    private final Player          player           = new Player(keyHandler);
    private final EncounterManager encounterManager = new EncounterManager();

    private GameState state = GameState.EXPLORE;
    private Eeveelution currentEncounter = null;

    // flicker timer for encounter flash effect
    private int encounterTimer = 0;
    private static final int ENCOUNTER_FLASH_FRAMES = 90;

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
        if (state == GameState.EXPLORE) {
            boolean moved = player.update(tileMap);
            tileMap.updateCamera(player.worldX, player.worldY);

            if (moved) {
                TileType standing = tileMap.tileAt(player.worldX + GameWindow.TILE_SIZE / 2,
                                                   player.worldY + GameWindow.TILE_SIZE / 2);
                if (encounterManager.onStep(standing)) {
                    currentEncounter = encounterManager.getPending();
                    encounterManager.clearPending();
                    state = GameState.ENCOUNTER;
                    encounterTimer = 0;
                }
            }
        } else if (state == GameState.ENCOUNTER) {
            encounterTimer++;
            // auto-advance to BATTLE after flash (Part 6 will handle BATTLE state)
            if (encounterTimer >= ENCOUNTER_FLASH_FRAMES) {
                state = GameState.BATTLE;
            }
        }
        // BATTLE state handled in Part 6
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (state == GameState.EXPLORE) {
            tileMap.draw(g2);
            player.draw(g2, tileMap.camX, tileMap.camY);
            drawZoneHint(g2);

        } else if (state == GameState.ENCOUNTER) {
            tileMap.draw(g2);
            player.draw(g2, tileMap.camX, tileMap.camY);
            drawEncounterFlash(g2);

        } else if (state == GameState.BATTLE) {
            // placeholder until Part 6
            drawBattlePlaceholder(g2);
        }

        g2.dispose();
    }

    private void drawZoneHint(Graphics2D g2) {
        TileType standing = tileMap.tileAt(player.worldX + GameWindow.TILE_SIZE / 2,
                                           player.worldY + GameWindow.TILE_SIZE / 2);
        Eeveelution here = Eeveelution.forZone(standing);
        if (here == null) return;

        String hint = here.name + " may appear!";
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        int w = g2.getFontMetrics().stringWidth(hint) + 16;
        int x = (GameWindow.SCREEN_WIDTH - w) / 2;
        int y = GameWindow.SCREEN_HEIGHT - 36;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(x, y, w, 24, 8, 8);
        g2.setColor(here.color);
        g2.drawString(hint, x + 8, y + 17);
    }

    private void drawEncounterFlash(Graphics2D g2) {
        // flicker overlay
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

    private void drawBattlePlaceholder(Graphics2D g2) {
        g2.setColor(new Color(20, 10, 40));
        g2.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

        g2.setColor(currentEncounter.color);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        String msg = "Battle: " + currentEncounter.name + " (coming in Part 6)";
        int x = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(msg)) / 2;
        g2.drawString(msg, x, GameWindow.SCREEN_HEIGHT / 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String sub = "Press R to flee back to map";
        int sx = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(sub)) / 2;
        g2.drawString(sub, sx, GameWindow.SCREEN_HEIGHT / 2 + 40);
    }

    // KeyListener for game-state transitions (not movement)
    @Override
    public void keyPressed(KeyEvent e) {
        if (state == GameState.BATTLE && e.getKeyCode() == KeyEvent.VK_R) {
            state = GameState.EXPLORE;
            currentEncounter = null;
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GamePanel extends JPanel implements Runnable {

    private static final int FPS = 60;
    private Thread gameThread;

    private final KeyHandler keyHandler = new KeyHandler();
    private final TileMap tileMap       = new TileMap();
    private final Player  player        = new Player(keyHandler);

    public GamePanel() {
        setPreferredSize(new Dimension(GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(keyHandler);

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
        player.update(tileMap);
        tileMap.updateCamera(player.worldX, player.worldY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        tileMap.draw(g2);
        player.draw(g2, tileMap.camX, tileMap.camY);

        g2.dispose();
    }
}

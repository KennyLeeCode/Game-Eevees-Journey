import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GamePanel extends JPanel implements Runnable {

    private static final int FPS = 60;
    private Thread gameThread;

    public GamePanel() {
        setPreferredSize(new Dimension(GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

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
        // game logic goes here in future parts
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // placeholder screen
        g2.setColor(new Color(10, 10, 30));
        g2.fillRect(0, 0, GameWindow.SCREEN_WIDTH, GameWindow.SCREEN_HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String title = "Eevee Moon Journey";
        int titleX = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(title)) / 2;
        g2.drawString(title, titleX, GameWindow.SCREEN_HEIGHT / 2 - 20);

        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        String subtitle = "Loading...";
        int subX = (GameWindow.SCREEN_WIDTH - g2.getFontMetrics().stringWidth(subtitle)) / 2;
        g2.drawString(subtitle, subX, GameWindow.SCREEN_HEIGHT / 2 + 20);

        g2.dispose();
    }
}

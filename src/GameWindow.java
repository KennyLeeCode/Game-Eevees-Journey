import javax.swing.JFrame;
import java.awt.Dimension;

// The game window - sets the size and holds the GamePanel where everything is drawn
public class GameWindow extends JFrame {

    // Each tile is 48x48 pixels; the screen shows 16 tiles wide and 12 tiles tall
    public static final int TILE_SIZE    = 48;
    public static final int SCREEN_COLS  = 16;
    public static final int SCREEN_ROWS  = 12;
    public static final int SCREEN_WIDTH  = TILE_SIZE * SCREEN_COLS;  // 768
    public static final int SCREEN_HEIGHT = TILE_SIZE * SCREEN_ROWS;  // 576

    public GameWindow() {
        setTitle("Eevee Moon Journey");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // fixed size so tile positions never shift

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack(); // resize window to fit the panel exactly
        setLocationRelativeTo(null); // center on screen
    }
}

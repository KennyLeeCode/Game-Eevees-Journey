import javax.swing.JFrame;
import java.awt.Dimension;

public class GameWindow extends JFrame {

    public static final int TILE_SIZE = 48;
    public static final int SCREEN_COLS = 16;
    public static final int SCREEN_ROWS = 12;
    public static final int SCREEN_WIDTH = TILE_SIZE * SCREEN_COLS;   // 768
    public static final int SCREEN_HEIGHT = TILE_SIZE * SCREEN_ROWS;  // 576

    public GameWindow() {
        setTitle("Eevee Moon Journey");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
    }
}

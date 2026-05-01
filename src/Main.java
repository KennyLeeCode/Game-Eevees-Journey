import javax.swing.JFrame;
import javax.swing.SwingUtilities;

// Entry point — launches the game window on the Swing UI thread
public class Main {
    public static void main(String[] args) {
        // invokeLater makes sure the window is created on the correct thread for Swing
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}

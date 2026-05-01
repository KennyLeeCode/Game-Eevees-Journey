import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage load(String path) {
        return cache.computeIfAbsent(path, p -> {
            try {
                return ImageIO.read(new File(p));
            } catch (Exception e) {
                System.err.println("Could not load sprite: " + p);
                return null;
            }
        });
    }

    public static BufferedImage getTrainer() {
        return load("img/myTrainerSprites.png");
    }

    public static BufferedImage getEeveelution(Eeveelution e) {
        return load("img/" + e.name.toLowerCase() + ".png");
    }
}

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

// Loads images from the img/ folder and keeps them in a cache
// so the same file is never read from disk more than once
public class SpriteLoader {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage load(String path) {
        // computeIfAbsent only reads the file if it isn't already cached
        return cache.computeIfAbsent(path, p -> {
            try {
                return ImageIO.read(new File(p));
            } catch (Exception e) {
                System.err.println("Could not load sprite: " + p);
                return null; // callers draw a fallback shape when null
            }
        });
    }

    public static BufferedImage getTrainer() {
        return load("img/myTrainerSprites.png");
    }

    public static BufferedImage getEeveelution(Eeveelution e) {
        // filename matches the enum name in lowercase, e.g. "vaporeon.png"
        return load("img/" + e.name.toLowerCase() + ".png");
    }
}

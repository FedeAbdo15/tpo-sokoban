package com.sokoban.view;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Flyweight: cada imagen se lee del disco una sola vez y se comparte entre todas las
 * celdas que la usan. Un nivel de 9x13 dibuja 117 celdas por cuadro con 21 imagenes.
 *
 * <p>Si falta un archivo devuelve un cuadrado de aviso en lugar de romper el dibujado,
 * asi se ve enseguida que sprite falta.
 */
public class SpriteCatalog {

    public static final int SIZE = 48;
    private static final String BASE_PATH = "/assets/images/";

    private final Map<SpriteKey, BufferedImage> cache = new HashMap<>();

    public BufferedImage get(SpriteKey key) {
        return cache.computeIfAbsent(key, this::load);
    }

    private BufferedImage load(SpriteKey key) {
        try (InputStream in = SpriteCatalog.class.getResourceAsStream(BASE_PATH + key.fileName())) {
            if (in != null) {
                BufferedImage image = ImageIO.read(in);
                if (image != null) {
                    return image;
                }
            }
        } catch (IOException e) {
            // cae en el sprite de aviso
        }
        return missing();
    }

    private BufferedImage missing() {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, SIZE, SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, SIZE - 1, SIZE - 1);
        g.dispose();
        return image;
    }
}

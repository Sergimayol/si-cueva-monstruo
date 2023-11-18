package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Helpers {
    private Helpers() {
        throw new IllegalStateException("Utility class");
    }

    public static BufferedImage readImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            throw new RuntimeException("Error reading image: " + path);
        }
    }

    public static ImageIcon escalateImageIcon(String iconPath, int width, int height) {
        Image image = new ImageIcon(iconPath).getImage();
        // Check if the image is already in the desired size
        if (image.getWidth(null) == width && image.getHeight(null) == height) {
            return new ImageIcon(iconPath);
        }
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    public static void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
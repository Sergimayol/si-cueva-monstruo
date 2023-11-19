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

    public static BufferedImage readImage(String path, int width, int height) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            // Check if the image is already in the desired size
            if (image.getWidth(null) == width && image.getHeight(null) == height) {
                return image;
            }
            Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            bufferedImage.getGraphics().drawImage(resizedImage, 0, 0, null);
            return bufferedImage;
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

    public static boolean createLogFileAndPathIfNotExists() {
        File file = new File(Config.PATH_TO_LOGS);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.LOG_FILE_PATH);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException("Error creating log file");
            }
        }
        return true;
    }

}
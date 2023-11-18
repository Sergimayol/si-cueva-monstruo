
package ui.entities;

import environment.TileData;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import utils.ImageLoader;

public class Hole extends Entity implements CaveEditor {

    private static BufferedImage image = null;
    private static BufferedImage imageCovered = null;
    public static final String IMAGEPATH = "./assets/images/hole.png";
    public static final String IMAGECOVEREDPATH = "./assets/images/planks.png";
    public static final String IMAGENAME = "hole";
    private boolean covered = false;

    public Hole(int x, int y) {
        super(x, y);
    }

    public Hole(Point position) {
        super(position);
    }

    public static void loadResizedImage(int width, int height) {
        image = ImageLoader.loadImageScaled(IMAGEPATH, width, height);
        imageCovered = ImageLoader.loadImageScaled(IMAGECOVEREDPATH, width, height);
    }

    @Override
    public void enterCave(TileData[][] map) {
        map[this.position.x][this.position.y].setHasHole(true);

        // Populate border elements
        for (int[] tile : CaveEditor.aroundTiles) {
            int i = this.position.x + tile[0];
            int j = this.position.y + tile[1];

            if (i < 0 || i >= map.length || j < 0 || j >= map.length)
                continue;

            map[i][j].addBreeze();
        }
    }

    @Override
    public void exitCave(TileData[][] map) {
        map[this.position.x][this.position.y].setHasHole(false);

        // Populate border elements
        for (int[] tile : CaveEditor.aroundTiles) {
            int i = this.position.x + tile[0];
            int j = this.position.y + tile[1];

            if (i < 0 || i >= map.length || j < 0 || j >= map.length)
                continue;

            map[i][j].removeBreeze();
        }
    }

    @Override
    public void paintComponent(Graphics2D g2, int x, int y, int size) {
        g2.drawImage(image, x + (int) Math.ceil(size * 0.05), y + (int) Math.ceil(size * 0.05),
                (int) Math.ceil(size * 0.90), (int) Math.ceil(size * 0.90), null);

        if (covered) {
            g2.drawImage(imageCovered, x + (int) Math.ceil(size * 0.05), y + (int) Math.ceil(size * 0.05),
                    (int) Math.ceil(size * 0.90), (int) Math.ceil(size * 0.90), null);
        }
    }

    public void putBridge() {
        covered = true;
    }

    @Override
    public void restoreEntity() {
        covered = false;
    }
}

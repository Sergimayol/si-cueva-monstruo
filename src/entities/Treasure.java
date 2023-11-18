
package entities;

import environment.TileData;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import utils.ImageLoader;

public class Treasure extends Entity implements CaveEditor {

    public static BufferedImage imageClosed = null;
    public static BufferedImage imageOpened = null;
    public static final String imagePathClosed = "./assets/images/treasure_closed.png";
    public static final String imagePathOpened = "./assets/images/treasure_opened.png";
    public static final String imageName = "treasure_closed";
    private boolean opened = false;

    public Treasure(Point position) {
        super(position);
    }

    public Treasure(int x, int y) {
        super(x, y);
    }

    public static void loadResizedImage(int width, int height) {
        imageClosed = ImageLoader.loadImageScaled(imagePathClosed, width, height);
        imageOpened = ImageLoader.loadImageScaled(imagePathOpened, width, height);
    }

    @Override
    public void enterCave(TileData[][] map) {
        map[this.position.x][this.position.y].setHasTreasure(true);
    }

    @Override
    public void exitCave(TileData[][] map) {
        map[this.position.x][this.position.y].setHasTreasure(false);
    }

    public void takeTreasure() {
        this.opened = true;
    }

    @Override
    public void restoreEntity() {
        this.opened = false;
    }

    @Override
    public void paintComponent(Graphics2D g2, int x, int y, int size) {
        if (opened) {
            g2.drawImage(imageOpened, x + (int) Math.ceil(size * 0.25), y + (int) Math.ceil(size * 0.25),
                    (int) Math.ceil(size * 0.50), (int) Math.ceil(size * 0.50), null);
        } else {
            g2.drawImage(imageClosed, x + (int) Math.ceil(size * 0.25), y + (int) Math.ceil(size * 0.25),
                    (int) Math.ceil(size * 0.50), (int) Math.ceil(size * 0.50), null);
        }
    }
}

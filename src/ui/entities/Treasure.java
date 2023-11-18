
package ui.entities;

import environment.TileData;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import utils.ImageLoader;

public class Treasure extends Entity implements CaveEditor {

    private static BufferedImage imageClosed = null;
    private static BufferedImage imageOpened = null;
    public static final String IMAGEPATHCLOSED = "./assets/images/treasure_closed.png";
    public static final String IMAGEPATHOPENED = "./assets/images/treasure_opened.png";
    public static final String IMAGENAME = "treasure_closed";
    private boolean opened = false;

    public Treasure(Point position) {
        super(position);
    }

    public Treasure(int x, int y) {
        super(x, y);
    }

    public static void loadResizedImage(int width, int height) {
        imageClosed = ImageLoader.loadImageScaled(IMAGEPATHCLOSED, width, height);
        imageOpened = ImageLoader.loadImageScaled(IMAGEPATHOPENED, width, height);
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
        final Image image = opened ? imageOpened : imageClosed;
        g2.drawImage(image, x + (int) Math.ceil(size * 0.25), y + (int) Math.ceil(size * 0.25),
                (int) Math.ceil(size * 0.50), (int) Math.ceil(size * 0.50), null);
    }
}

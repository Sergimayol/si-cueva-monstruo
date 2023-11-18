
package ui.entities;

import environment.TileData;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import utils.ImageLoader;

public class Monster extends Entity implements CaveEditor {

    public static BufferedImage imageAlive = null;
    public static BufferedImage imageDead = null;
    public static final String imagePathAlive = "./assets/images/monster.png";
    public static final String imagePathDead = "./assets/images/death-monster.png";
    public static final String imageName = "monster";
    private boolean dead = false;

    public Monster(int x, int y) {
        super(x, y);
    }

    public Monster(Point position) {
        super(position);
    }

    public static void loadResizedImage(int width, int height) {
        imageAlive = ImageLoader.loadImageScaled(imagePathAlive, width, height);
        imageDead = ImageLoader.loadImageScaled(imagePathDead, width, height);
    }

    @Override
    public void enterCave(TileData[][] map) {
        map[this.position.x][this.position.y].setHasMonster(true);

        // Populate border elements
        for (int[] tile : CaveEditor.aroundTiles) {
            int i = this.position.x + tile[0];
            int j = this.position.y + tile[1];

            if (i < 0 || i >= map.length || j < 0 || j >= map.length)
                continue;

            map[i][j].addHedor();
        }
    }

    @Override
    public void exitCave(TileData[][] map) {
        map[this.position.x][this.position.y].setHasMonster(false);

        // Populate border elements
        for (int[] tile : CaveEditor.aroundTiles) {
            int i = this.position.x + tile[0];
            int j = this.position.y + tile[1];

            if (i < 0 || i >= map.length || j < 0 || j >= map.length)
                continue;

            map[i][j].removeHedor();
        }
    }

    @Override
    public void paintComponent(Graphics2D g2, int x, int y, int size) {
        if (dead) {
            g2.drawImage(imageDead, x + (int) Math.ceil(size * 0.05), y + (int) Math.ceil(size * 0.05),
                    (int) Math.ceil(size * 0.90), (int) Math.ceil(size * 0.90), null);
        } else {
            g2.drawImage(imageAlive, x + (int) Math.ceil(size * 0.05), y + (int) Math.ceil(size * 0.05),
                    (int) Math.ceil(size * 0.90), (int) Math.ceil(size * 0.90), null);
        }
    }

    public void killMonster() {
        dead = true;
    }

    @Override
    public void restoreEntity() {
        dead = false;
    }
}

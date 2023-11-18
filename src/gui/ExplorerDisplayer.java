/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import agent.Explorer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import utils.ImageLoader;
import utils.MutableBoolean;

/**
 *
 * @author ccf20
 */
public class ExplorerDisplayer extends JComponent {

    class Timer {

        private static final ScheduledExecutorService scheduledThreadPoolExecutor = Executors
                .newScheduledThreadPool(10);

        private static void doPause(int ms) {
            try {
                scheduledThreadPoolExecutor.schedule(() -> {
                }, ms, TimeUnit.MILLISECONDS).get();
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

    private final int sleep_millis = 10;
    private final int spriteChangeIterations = 35;
    private int iterationCounter = 0;
    private boolean walking = false;
    // private final int animation_time = 1000;
    private int sizeFactor = 1;
    private final int sizeFactorDivider = 50;
    private double speedFactor = 1.0f;
    private final Explorer explorer;
    private final int baseIncMovement = 2;
    private final int baseIncAngle = 6;
    private int prevPosX;
    private int prevPosY;
    private final BufferedImage imageLeft;
    private final BufferedImage imageRight;
    private BufferedImage image;
    private Point position;
    private MutableBoolean active;
    private int width = 50;
    private int costat = 1;
    private int borde = 1;
    private int rotationAngle = 0;
    private boolean isInAnimation = false;
    private int initialI;
    private int initialJ;
    private final int id;
    private int totalTiles;

    public ExplorerDisplayer(Explorer explorer, int id) {
        this.explorer = explorer;
        this.id = id;
        this.position = new Point(-1, -1);
        this.prevPosX = this.position.x;
        this.prevPosY = this.position.y;
        this.active = new MutableBoolean(false);
        this.imageLeft = ImageLoader.loadImage("./assets/images/agents/explorer" + (id + 1) + "_left.png");
        this.imageRight = ImageLoader.loadImage("./assets/images/agents/explorer" + (id + 1) + "_right.png");
        this.image = imageLeft;
    }

    public boolean isActive() {
        return this.active.is();
    }

    public void setActive(boolean value) {
        this.active.setValue(value);
    }

    public MutableBoolean getIsActiveReference() {
        return this.active;
    }

    public BufferedImage getImageLeft() {
        return this.imageLeft;
    }

    public void setInitialTile(int i, int j, int costat, int borde, int totalTiles) {
        // System.out.println(i + ", " + j);
        // this.robot.setDisplacement(i, j);
        this.initialI = i;
        this.initialJ = j;
        this.width = (int) (costat * 0.9);
        this.position = this.calculatePositionFromTileIndices(i, j, costat, borde);
        this.prevPosX = i;
        this.prevPosY = j;
        this.costat = costat;
        this.borde = borde;
        this.rotationAngle = i == 0 ? 180 : 0;
        this.totalTiles = totalTiles;

        this.sizeFactor = (this.costat / sizeFactorDivider);
    }

    public Point getTileIndices() {
        return this.explorer.getDisplacement();
    }

    public Point calculatePositionFromTileIndices(int i, int j, int costat, int borde) {
        Point p = new Point();
        p.x = (j * costat + borde) + (int) (costat * 0.05);
        p.y = (i * costat + borde) + (int) (costat * 0.05);
        return p;
    }

    public boolean isOnTile(int i, int j) {
        Point robotTilesPosition = this.explorer.getDisplacement();
        return robotTilesPosition.x == i && robotTilesPosition.y == j;
    }

    public void manualMoveRobot(int i, int j, Cave kitchen) {

        if (isActive() && !isInAnimation) {
            isInAnimation = true;
            this.explorer.move(i, j);
            this.moveInternal(kitchen);
            isInAnimation = false;
        }
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }

    public void move(Cave kitchen) {
        if (!isInAnimation) {
            isInAnimation = true;
            moveInternal(kitchen);
            isInAnimation = false;
        }
    }

    private void moveInternal(Cave kitchen) {
        int explorerXPos = this.initialI + this.explorer.getDisplacement().x;
        int explorerYPos = this.initialJ + this.explorer.getDisplacement().y;

        Point explorerCoordinates = calculatePositionFromTileIndices(explorerXPos, explorerYPos, this.costat,
                this.borde);

        int multiplierX = (int) Math.signum(Integer.compare(explorerCoordinates.x, this.position.x));
        int multiplierY = (int) Math.signum(Integer.compare(explorerCoordinates.y, this.position.y));

        int currentIncX = (int) (this.speedFactor * Math.max(this.sizeFactor, 1) * baseIncMovement * multiplierX);
        int currentIncY = (int) (this.speedFactor * Math.max(this.sizeFactor, 1) * baseIncMovement * multiplierY);

        this.rotate(kitchen, explorerXPos, explorerYPos);

        this.prevPosX = explorerXPos;
        this.prevPosY = explorerYPos;

        if (explorerXPos < 0 || explorerXPos >= totalTiles || explorerYPos < 0 || explorerYPos >= totalTiles) {

            Point savedPos;
            for (int i = 0; i < 10; i++) {
                savedPos = new Point(this.position.x, this.position.y);
                this.position.x += (Math.random() * 2 - 1) * this.width / 4;
                this.position.y += (Math.random() * 2 - 1) * this.width / 4;
                kitchen.repaintExplorersAndTiles();
                Timer.doPause(sleep_millis);
                this.position = savedPos;
            }

            kitchen.repaintExplorersAndTiles();

            return;
        }

        this.walking = true;

        while (this.position.x != explorerCoordinates.x
                || this.position.y != explorerCoordinates.y) {
            this.position.x += currentIncX;
            this.position.y += currentIncY;

            // check if we have passed the target
            if (currentIncX > 0 && this.position.x > explorerCoordinates.x
                    || currentIncX < 0 && this.position.x < explorerCoordinates.x) {

                this.position.x = explorerCoordinates.x;
            }

            // check if we have passed the target
            if (currentIncY > 0 && this.position.y > explorerCoordinates.y
                    || currentIncY < 0 && this.position.y < explorerCoordinates.y) {

                this.position.y = explorerCoordinates.y;
            }

            Timer.doPause(sleep_millis);

            // if(this.position.x < 0 || this.position.x >= (this.totalWidth -
            // (int)(this.width*0.75)) || this.position.y < 0 || this.position.y >=
            // (this.totalWidth - (int)(this.width*0.75))){
            // break;
            // }

            kitchen.repaintExplorersAndTiles();

        }
        this.walking = false;
    }

    private void rotate(Cave kitchen, int targetPosX, int targetPosY) {

        int finalAngle = getFinalAngle(targetPosX, targetPosY);

        int negFinalAngle = finalAngle - 360;

        finalAngle = Math.abs(finalAngle - this.rotationAngle) <= Math.abs(negFinalAngle - this.rotationAngle)
                ? finalAngle
                : negFinalAngle;

        if (finalAngle == 0 && this.rotationAngle > 180) {
            finalAngle = 360;
        }

        int multiplier = this.rotationAngle <= finalAngle ? 1 : -1;

        int currentIncAngle = (int) (this.speedFactor * multiplier * this.baseIncAngle);

        while (this.rotationAngle != finalAngle) {
            this.rotationAngle += currentIncAngle;

            // check if we have passed the target
            if (currentIncAngle > 0 && this.rotationAngle > finalAngle
                    || currentIncAngle < 0 && this.rotationAngle < finalAngle) {

                this.rotationAngle = finalAngle;
            }

            Timer.doPause(sleep_millis);

            kitchen.repaintExplorersAndTiles();
        }

        if (this.rotationAngle < 0) {
            this.rotationAngle += 360;
        }
        if (this.rotationAngle >= 360) {
            this.rotationAngle -= 360;
        }
    }

    private int getFinalAngle(int targetPosX, int targetPosY) {

        if (targetPosX < this.prevPosX) {
            return 0;
        }

        if (targetPosX > this.prevPosX) {
            return 180;
        }

        if (targetPosY < this.prevPosY) {
            return 270;
        }

        if (targetPosY > this.prevPosY) {
            return 90;
        }

        return this.rotationAngle;

    }

    private void toggleSprite() {
        if (this.image == this.imageLeft) {
            this.image = this.imageRight;
        } else {
            this.image = this.imageLeft;
        }
    }

    @Override
    public void paintComponent(Graphics g1) {
        // super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.red);

        if (this.walking) {
            if (iterationCounter++ % spriteChangeIterations == 0) {
                this.toggleSprite();
            }
        } else {
            this.iterationCounter = 0;
        }

        // Draw rotated image
        AffineTransform backup = g.getTransform();

        AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(rotationAngle),
                position.x + this.width / 2, position.y + this.width / 2);

        g.setTransform(a);

        g.drawImage(this.image, position.x, position.y, this.width, this.width, null);

        g.setTransform(backup);
    }

}

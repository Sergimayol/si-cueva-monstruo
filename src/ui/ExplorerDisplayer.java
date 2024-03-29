package ui;

import agent.Explorer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;

import utils.Helpers;
import utils.MutableBoolean;

public class ExplorerDisplayer extends JComponent {

    private final int sleep_millis = 10;
    private int sizeFactor = 1;
    private final int sizeFactorDivider = 50;
    private double speedFactor = 1.0f;
    private final transient Explorer explorer;
    private final int baseIncMovement = 2;
    private final int baseIncAngle = 6;
    private int prevPosX;
    private int prevPosY;
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
    private int totalTiles;
    private boolean applyRotation = false;

    public ExplorerDisplayer(Explorer explorer, int id) {
        this.explorer = explorer;
        this.position = new Point(-1, -1);
        this.prevPosX = this.position.x;
        this.prevPosY = this.position.y;
        this.active = new MutableBoolean(false);
        this.image = Helpers.readImage("./assets/explorer/" + (id + 1) + ".png");
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

    public BufferedImage getImage() {
        return this.image;
    }

    public void setInitialTile(int i, int j, int costat, int borde, int totalTiles) {
        this.initialI = i;
        this.initialJ = j;
        this.width = (int) (costat * 0.9);
        this.position = this.calculatePositionFromTileIndices(i, j, costat, borde);
        this.prevPosX = i;
        this.prevPosY = j;
        this.costat = costat;
        this.borde = borde;
        this.rotationAngle = 0;
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

        if (this.applyRotation) {
            this.rotate(kitchen, explorerXPos, explorerYPos);
        }

        this.prevPosX = explorerXPos;
        this.prevPosY = explorerYPos;

        if (explorerXPos < 0 || explorerXPos >= totalTiles || explorerYPos < 0 || explorerYPos >= totalTiles) {
            kitchen.repaintExplorersAndTiles();
            return;
        }

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

            kitchen.repaintExplorersAndTiles();

        }
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

    @Override
    public void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.red);

        // Draw rotated image
        AffineTransform backup = g.getTransform();

        AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(rotationAngle),
                position.x + this.width / 2, position.y + this.width / 2);

        g.setTransform(a);

        g.drawImage(this.image, position.x, position.y, this.width, this.width, null);

        g.setTransform(backup);
    }

    private class Timer {

        private Timer() {
            throw new IllegalStateException("Utility class");
        }

        private static final ScheduledExecutorService scheduledThreadPoolExecutor = Executors
                .newScheduledThreadPool(10);

        private static void doPause(int ms) {
            try {
                scheduledThreadPoolExecutor.schedule(() -> {
                }, ms, TimeUnit.MILLISECONDS).get();
            } catch (ExecutionException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

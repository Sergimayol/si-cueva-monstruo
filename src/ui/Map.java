package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import agent.Robot;
import env.Environment;
import utils.Config;
import utils.Helpers;
import utils.FileLogger;

public class Map extends JPanel {

    private Tile[][] tiles;
    private transient Environment<Robot> environment;

    public Map(int size) {
        super();
        this.tiles = new Tile[size][size];
        this.setLayout(new BorderLayout());
    }

    public Map(int size, Environment<Robot> environment) {
        super();
        this.tiles = new Tile[size][size];
        this.environment = environment;
        this.setLayout(new BorderLayout());
    }

    public void initMapTiles() {
        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles[0].length; j++) {
                Color tileColor = (i + j) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY;
                BufferedImage image = Helpers.readImage(Config.OBSTACLE_IMG_PATH);
                BufferedImage robotImage = Helpers.readImage(Config.ROBOT_IMG_PATH);
                this.tiles[i][j] = new Tile(i, j, image, robotImage, tileColor, this.environment);
            }
        }
    }

    public void setEnvironment(Environment<Robot> environment) {
        this.environment = environment;
        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles[0].length; j++) {
                this.tiles[i][j].setEnvironment(environment);
            }
        }
    }

    public Point getRobotPosition() {
        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles[0].length; j++) {
                Tile tile = this.tiles[i][j];
                if (tile.isRobot()) {
                    return new Point(i, j);
                }
            }
        }
        return null; // Devuelve null si no se encuentra un robot
    }

    public List<Point> getObstaclePositions() {
        List<Point> obstaclePositions = new ArrayList<>();
        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles[0].length; j++) {
                Tile tile = this.tiles[i][j];
                if (tile.isObstacle()) {
                    obstaclePositions.add(new Point(i, j));
                }
            }
        }
        FileLogger.info("[MAP] Obstacle positions: " + obstaclePositions);
        return obstaclePositions;
    }

    public void resetMap() {
        this.removeAll();
        this.initMapTiles();
        this.paintComponent(this.getGraphics());
        this.revalidate();
    }

    public void changeMapUIsize(int size, boolean initMap) {
        this.removeAll();
        this.changeMapSize(size, initMap);
        this.paintComponent(this.getGraphics());
        this.revalidate();
    }

    public void changeMapSize(int size, boolean initMap) {
        this.tiles = new Tile[size][size];
        if (initMap) {
            this.initMapTiles();
        }
    }

    public void setTile(Tile tile) {
        final int x = tile.getPosition().x;
        final int y = tile.getPosition().y;
        this.tiles[x][y] = tile;
    }

    public Tile getTile(int x, int y) {
        return this.tiles[x][y];
    }

    @Override
    protected void paintComponent(Graphics g) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(this.tiles.length, this.tiles[0].length));
        for (int i = 0; i < this.tiles.length; i++) {
            for (int j = 0; j < this.tiles[0].length; j++) {
                Tile tile = this.tiles[i][j];
                if (tile == null) {
                    throw new RuntimeException("Tile is null");
                }
                panel.add(tile);
            }
        }

        this.add(panel, BorderLayout.CENTER);
    }

}

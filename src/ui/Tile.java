package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import agent.Robot;
import env.Environment;
import utils.FileLogger;

public class Tile extends JPanel {

    private transient BufferedImage image;
    private transient BufferedImage robotImage;
    private Color tileColor;
    private Point position;
    private boolean isObstacle;
    private boolean isRobot;
    private transient Environment<Robot> environment;

    public Tile(int x, int y, BufferedImage image, BufferedImage robotImage, Color tileColor,
            Environment<Robot> environment) {
        this.position = new Point(x, y);
        this.image = image;
        this.robotImage = robotImage;
        this.tileColor = tileColor;
        this.isObstacle = false;
        this.isRobot = false;
        this.environment = environment;
        this.addMouseListener(createMouseListner());
    }

    public void setEnvironment(Environment<Robot> environment) {
        this.environment = environment;
    }

    public void setObstacle(boolean isObstacle) {
        this.isObstacle = isObstacle;
    }

    public void setRobot(boolean isRobot) {
        this.isRobot = isRobot;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Point getPosition() {
        return this.position;
    }

    public boolean isObstacle() {
        return this.isObstacle;
    }

    public boolean isRobot() {
        return this.isRobot;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        final int width = this.getWidth();
        final int height = this.getHeight();
        this.setBackground(this.tileColor);
        g2d.setColor(this.tileColor);
        g2d.fillRect(0, 0, width, height);
        if (this.isObstacle || this.isRobot) {
            g2d.drawImage(this.isRobot ? this.robotImage : this.image, 0, 0, width, height, null);
        }
    }

    private MouseListener createMouseListner() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                FileLogger.info("[TILE] Clicked on tile: " + Tile.this.position);
                if (evt.getButton() == MouseEvent.BUTTON1) {
                    Tile.this.isObstacle = !Tile.this.isObstacle;
                    Tile.this.environment.setObstacleIn(Tile.this.position.x, Tile.this.position.y,
                            Tile.this.isObstacle);
                }
                final Robot robot = Tile.this.environment.getAgent();
                if (evt.getButton() == MouseEvent.BUTTON3) {
                    if (Tile.this.isRobot) {
                        Tile.this.isRobot = false;
                        robot.setDefaultPosition();
                    } else if (robot.isDefaultPosition()) { // Only one robot
                        Tile.this.isRobot = true;
                        robot.setPosition(Tile.this.position.x, Tile.this.position.y);
                    }
                    Tile.this.environment.setAgent(robot);
                }
                Tile.this.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // Do nothing
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Do nothing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Do nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Do nothing
            }
        };
    }

    @Override
    public String toString() {
        return "Tile [position=" + position + ", isObstacle=" + isObstacle + ", isRobot=" + isRobot + "]";
    }

}

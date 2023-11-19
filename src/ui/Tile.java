package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import javax.swing.JComponent;

import ui.entities.CaveEditor;

/*
Classe que controla el funcionament d'una casella
 */
public class Tile extends JComponent implements Serializable {

    // Variables estaticas
    public static final String backgroundImagePath = "./assets/images/caveTile.png";

    // Constants de la casella

    // Atributs de la casella
    private int x;
    private int y;
    private int costat;
    private CaveEditor entity = null;
    private boolean needsToBePainted = true;
    private transient BufferedImage backgroundImage;

    // Constructor de la casella
    public Tile(int i, int j, int costat, int borde, BufferedImage backgroundImage) {
        this.costat = costat;
        this.x = j * this.costat + borde;
        this.y = i * this.costat + borde;
        this.backgroundImage = backgroundImage;

    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setEntity(CaveEditor entity) {
        this.entity = entity;

    }

    public CaveEditor getEntity() {
        return this.entity;

    }

    public boolean hasEntity() {
        return this.entity != null;
    }

    public void notifyChange() {
        this.needsToBePainted = true;
    }

    // Mètode que pinta una casella
    @Override
    public void paintComponent(Graphics g) {
        if (needsToBePainted) {
            Graphics2D g2 = (Graphics2D) g;

            g2.drawImage(backgroundImage, x, y, costat, costat, null);

            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, costat, costat);

            if (entity != null) {
                entity.paintComponent(g2, x, y, costat);
            }
            needsToBePainted = false;
        }
    }

}

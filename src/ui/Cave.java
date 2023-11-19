package ui;

import agent.Explorer;
import environment.Environment;
import ui.entities.CaveEditor;
import ui.entities.Hole;
import ui.entities.Monster;
import ui.entities.Treasure;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

import utils.Helpers;

public class Cave extends JPanel implements MouseListener, MouseMotionListener {

    // Atributs propis del tauler
    private Tile[][] tiles;
    private int costat;
    private int costatCasella;
    private static final int dimsBordeBase = 40;
    private int dimsBorde = dimsBordeBase;
    private int pixelsCostat = 800 - (4 * dimsBorde);
    private Dimension dimensions = new Dimension(pixelsCostat + (2 * dimsBorde) + 1,
            pixelsCostat + (2 * dimsBorde) + 1);
    private int buttonPressed = -1;
    private ExplorerDisplayer[] explorerDisplayers;
    private transient BufferedImage starterImage = null;
    private transient Graphics2D gAux = null;
    private View gui;
    private Environment<Explorer> env;
    private boolean explorersActive = false;
    private boolean caveBlocked = false;
    private int treasuresRemaining = 0;

    // Constructor del tauler
    public Cave(int n, View gui, Environment<Explorer> env, ExplorerDisplayer[] explorerDisplayers) {
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        this.gui = gui;
        this.env = env;
        this.costat = n;
        this.costatCasella = pixelsCostat / costat;
        this.dimsBorde += (int) (((((float) pixelsCostat) / costat) - costatCasella) * costat / 2);
        this.tiles = new Tile[costat][costat];
        this.explorerDisplayers = explorerDisplayers;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        // Set initial position of each explorer
        setExplorerInitialTiles();

        for (Class cl : gui.getEntityClasses()) {
            try {
                cl.getMethod("loadResizedImage", int.class, int.class)
                        .invoke(null, this.costatCasella, this.costatCasella);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                    | InvocationTargetException ex) {
                Logger.getLogger(Cave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        BufferedImage backgroundImage = Helpers.readImage(Tile.backgroundImagePath, costatCasella, costatCasella);
        for (int i = 0; i < costat; i++) {
            for (int j = 0; j < costat; j++) {
                tiles[i][j] = new Tile(i, j, costatCasella, dimsBorde, backgroundImage);
            }
        }
    }

    // Constructor del tauler
    public Cave(Tile[][] tiles, View gui, Environment<Explorer> env, ExplorerDisplayer[] explorerDisplayers) {
        this.setLayout(null);
        this.setBackground(Color.WHITE);
        this.gui = gui;
        this.env = env;
        this.costat = tiles.length;
        this.costatCasella = pixelsCostat / costat;
        this.dimsBorde += (int) (((((float) pixelsCostat) / costat) - costatCasella) * costat / 2);
        this.tiles = tiles;
        this.explorerDisplayers = explorerDisplayers;

        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        // Set initial position of each explorer
        setExplorerInitialTiles();

        for (Class cl : gui.getEntityClasses()) {
            try {
                cl.getMethod("loadResizedImage", int.class, int.class)
                        .invoke(null, this.costatCasella, this.costatCasella);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                    | InvocationTargetException ex) {
                Logger.getLogger(Cave.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        BufferedImage backgroundImage = Helpers.readImage(Tile.backgroundImagePath, costatCasella, costatCasella);

        for (int i = 0; i < costat; i++) {
            for (int j = 0; j < costat; j++) {
                tiles[i][j].setBackgroundImage(backgroundImage);
                tiles[i][j].notifyChange();
                if (tiles[i][j].getEntity() instanceof Treasure) {
                    this.treasuresRemaining++;
                }
            }
        }
    }

    public Tile[][] getTiles() {
        return this.tiles;
    }

    public void setExplorerDisplayers(ExplorerDisplayer[] explorerDisplayers) {
        this.explorerDisplayers = explorerDisplayers;
        setExplorerInitialTiles();
        this.paintAll();
    }

    public void setExplorersActive(boolean active) {
        this.explorersActive = active;
    }

    private void setExplorerInitialTiles() {
        Point[] initialPos = this.env.getInitialPos();
        for (int i = 0; i < this.explorerDisplayers.length; i++) {
            Point pos = initialPos[i];
            this.explorerDisplayers[i].setInitialTile(pos.x, pos.y, costatCasella, dimsBorde, costat);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return dimensions;
    }

    // Pinta el tauler
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (starterImage == null) {
            starterImage = new BufferedImage(dimensions.width, dimensions.height, BufferedImage.TYPE_INT_ARGB);
            gAux = (Graphics2D) starterImage.getGraphics();
        }

        for (int i = 0; i < costat; i++) {
            for (int j = 0; j < costat; j++) {
                tiles[i][j].paintComponent(gAux);
            }
        }

        BufferedImage biAux = clone(starterImage);
        Graphics2D gbiAux = (Graphics2D) biAux.getGraphics();

        for (ExplorerDisplayer expDisplayer : this.explorerDisplayers) {
            if (expDisplayer != null) {
                expDisplayer.paintComponent(gbiAux);
            }
        }

        gbiAux.dispose();

        g.drawImage(biAux, 0, 0, this);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        String text = "Cofres restantes: " + this.treasuresRemaining;
        int x = (int) (this.getWidth() * 0.8);
        int y = (int) ((this.dimsBorde / 2) * 0.6);
        g2.setColor(Color.black);
        g2.setFont(new Font("Calibri", Font.BOLD, 24));

        Rectangle r = getStringBounds(g2, text);
        int posX = x - 24 / 10 + this.costat / 2 - (int) (r.getWidth() / 2);
        int posY = y + this.costat / 2 + (int) (r.getHeight() / 2);
        g2.drawChars(text.toCharArray(), 0, text.length(), posX, posY);

        if (this.caveBlocked) {

            text = "En ejecución...";
            x = this.getWidth() / 2;
            y = (int) ((this.dimsBorde / 2) * 0.6);
            g2.setColor(Color.red);
            g2.setFont(new Font("Calibri", Font.BOLD, 24));

            r = getStringBounds(g2, text);
            posX = x - 24 / 10 + this.costat / 2 - (int) (r.getWidth() / 2);
            posY = y + this.costat / 2 + (int) (r.getHeight() / 2);

            g2.drawChars(text.toCharArray(), 0, text.length(), posX, posY);
        }

    }

    private Rectangle getStringBounds(Graphics2D g2, String str) {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, 0, 0);
    }

    public void setExplorerDisplayerSpeedFactor(double speedFactor) {
        for (ExplorerDisplayer expDisplayer : this.explorerDisplayers) {
            expDisplayer.setSpeedFactor(speedFactor);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.requestFocus();
        this.buttonPressed = e.getButton();
        updateObstacles(e);
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void mouseReleased(MouseEvent e) {
        ;
        this.buttonPressed = -1;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private int getIndex(int x) {
        float val = (float) (x - dimsBorde) / this.costatCasella;
        return val < 0 ? -1 : (int) val;
    }

    private boolean isValid(int n) {
        return (n >= 0 && n < (this.pixelsCostat / this.costatCasella));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateObstacles(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private void updateObstacles(MouseEvent e) {

        if (caveBlocked)
            return;

        try {
            int x = e.getX();
            int y = e.getY();
            int i = getIndex(y);
            int j = getIndex(x);

            if (!isValid(i) || !isValid(j)) {
                return;
            }

            Tile tile = tiles[i][j];

            if (this.buttonPressed == MouseEvent.BUTTON1 && !tile.hasEntity()) {

                Constructor constructor = this.gui.getCurrentEntityConstructor();
                CaveEditor entity = (CaveEditor) constructor.newInstance(i, j);

                if (entity instanceof Treasure) {
                    this.treasuresRemaining++;
                }

                entity.enterCave(this.env.getMap());

                tile.setEntity(entity);

                tile.notifyChange();
            }

            if (this.buttonPressed == MouseEvent.BUTTON3 && tile.hasEntity()) {
                tile.getEntity().exitCave(this.env.getMap());

                if (tile.getEntity() instanceof Treasure) {
                    this.treasuresRemaining--;
                }

                tile.setEntity(null);

                tile.notifyChange();

            }

            this.repaint();

        } catch (InstantiationException | SecurityException | IllegalArgumentException | InvocationTargetException
                | IllegalAccessException ex) {
            Logger.getLogger(Cave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void repaintExplorersAndTiles() {

        for (ExplorerDisplayer expDisplayer : this.explorerDisplayers) {
            Point center = expDisplayer.getTileIndices();
            int[][] offset = {
                    { 0, 0 },
                    { 1, 0 },
                    { 0, 1 },
                    { -1, 0 },
                    { 0, -1 }
            };

            for (int i = 0; i < offset.length; i++) {
                int x = center.x + offset[i][0];
                int y = center.y + offset[i][1];
                if (!isValid(x) || !isValid(y)) {
                    continue;
                }
                this.tiles[x][y].notifyChange();
            }
        }

        this.repaint();
    }

    private BufferedImage clone(BufferedImage image) {
        BufferedImage clon = new BufferedImage(image.getWidth(),
                image.getHeight(), image.getType());
        Graphics2D g2d = clon.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return clon;
    }

    public void paintAll() {
        for (int i = 0; i < costat; i++) {
            for (int j = 0; j < costat; j++) {
                tiles[i][j].notifyChange();
            }
        }
        this.repaint();
    }

    public void resetAll() {
        if (caveBlocked)
            return;
        for (int i = 0; i < costat; i++) {
            for (int j = 0; j < costat; j++) {
                CaveEditor entity = tiles[i][j].getEntity();

                if (entity != null) {
                    entity.exitCave(this.env.getMap());
                }

                tiles[i][j].setEntity(null);
                tiles[i][j].notifyChange();

            }
        }
        this.repaint();
    }

    public Boolean moveExplorer(int i) {
        explorerDisplayers[i].move(this);
        return explorersActive;
    }

    void takeTreasure(Point position) {
        Tile tile = this.tiles[position.x][position.y];
        ((Treasure) tile.getEntity()).takeTreasure();
        this.treasuresRemaining--;
        tile.notifyChange();
        repaint();
    }

    void killMonster(Point monsterPos) {
        Tile tile = this.tiles[monsterPos.x][monsterPos.y];
        ((Monster) tile.getEntity()).killMonster();
        tile.notifyChange();
        repaint();
    }

    void lockCave() {
        this.caveBlocked = true;
    }

    void unlockCave() {
        this.caveBlocked = false;
    }

    boolean isLocked() {
        return this.caveBlocked;
    }

    void putBridge(Point holePos) {
        Tile tile = this.tiles[holePos.x][holePos.y];
        ((Hole) tile.getEntity()).putBridge();
        tile.notifyChange();
        repaint();
    }

    void resetEntities() {
        this.treasuresRemaining = 0;
        for (int i = 0; i < costat; i++) {
            for (int j = 0; j < costat; j++) {
                Tile tile = tiles[i][j];
                if (tile.hasEntity()) {
                    CaveEditor entity = tile.getEntity();
                    entity.restoreEntity();
                    if (entity instanceof Treasure) {
                        this.treasuresRemaining++;
                    }

                    tile.notifyChange();
                }
            }
        }
    }

    boolean thereAreTreasures() {
        return this.treasuresRemaining > 0;
    }

}

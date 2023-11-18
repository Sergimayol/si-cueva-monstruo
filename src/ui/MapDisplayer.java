
package ui;

import agent.ExplorerMap;
import environment.TileInfo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class MapDisplayer extends JComponent {

    public static Dimension dimension = new Dimension(400, 400);
    private BufferedImage cave;
    private BufferedImage img;
    private final ExplorerMap explorerMap;

    private final Point initialPos;
    private int initialX = 0;
    private int initialY = 0;
    private int tileSize = 0;
    private int nTiles = 0;
    private int wallWidth = 15;
    private boolean showTimesVisited;

    public MapDisplayer(ExplorerMap explorerMap, Point initialPos) {
        super();
        this.explorerMap = explorerMap;
        this.initialPos = initialPos;

        this.setBackground(Color.red);
    }

    @Override
    public Dimension getPreferredSize() {
        return dimension;
    }

    public void setCaveImage(BufferedImage image) {
        this.cave = image;
    }

    @Override
    public void paintComponent(Graphics gOriginal) {
        super.paintComponent(gOriginal);
        if (this.cave != null) {

            int w = cave.getWidth();
            int h = cave.getHeight();

            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) img.getGraphics();

            g.drawImage(cave, 0, 0, this);

            g.setColor(new Color(0, 0, 0, 0.5f));

            for (int i = -nTiles; i <= nTiles; i++) {
                for (int j = -nTiles; j <= nTiles; j++) {

                    TileInfo info = this.explorerMap.getTileNoAdd(new Point(i, j));
                    int x = j + this.initialPos.y;
                    int y = i + this.initialPos.x;

                    if (info == null || (info.getTimesVisited() == 0
                            && !info.hasMonster() && !info.hasHole()
                            && !(info.isOk()))) {

                        if (x >= 0 && x < nTiles && y >= 0 && y < nTiles) {
                            g.fillRect(initialX + (x * this.tileSize), initialY + (y * this.tileSize), this.tileSize,
                                    this.tileSize);
                        }
                    } else {

                        if (showTimesVisited && info != null) {

                            int xInit = initialX + (x * this.tileSize);
                            int yInit = initialY + (y * this.tileSize);

                            if (xInit >= 0 && xInit < (w - this.wallWidth * 2) && yInit >= 0
                                    && yInit < (h - this.wallWidth * 2)) {
                                String text = Integer.toString(info.getTimesVisited());
                                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                                int fontSize = this.tileSize;
                                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
                                Rectangle r = getStringBounds(g, text);
                                while (r.getWidth() > this.tileSize * 0.8) {
                                    fontSize *= 0.9;
                                    g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
                                    r = getStringBounds(g, text);
                                }
                                g.setColor(Color.red);

                                int posX = xInit - fontSize / 8 + this.tileSize / 2 - (int) (r.getWidth() / 2);
                                int posY = yInit + this.tileSize / 2 + (int) (r.getHeight() / 2);
                                g.drawChars(text.toCharArray(), 0, text.length(), posX, posY);

                                g.setColor(new Color(0, 0, 0, 0.5f));
                                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                            }
                        }

                        if (info.hasObstacle()) {

                            g.setColor(new Color(255, 70, 70, 128));
                            if (y == -1 && x >= 0 && x < nTiles) {
                                g.fillRect(initialX + (x * this.tileSize), 0, this.tileSize, wallWidth);
                            } else if (y == nTiles && x >= 0 && x < nTiles) {
                                g.fillRect(initialX + (x * this.tileSize), h - initialY, this.tileSize, wallWidth);
                            } else if (x == -1 && y >= 0 && y < nTiles) {
                                g.fillRect(0, initialY + (y * this.tileSize), wallWidth, this.tileSize);
                            } else if (x == nTiles && y >= 0 && y < nTiles) {
                                g.fillRect(w - initialX, initialY + (y * this.tileSize), wallWidth, this.tileSize);
                            }
                            g.setColor(new Color(0, 0, 0, 0.5f));

                        }
                    }

                }
            }

            // g.drawRect(0, 0, dimension.width - 1, dimension.height - 1);
            gOriginal.drawImage(resizeImage(img, dimension.width, dimension.height), 0, 0, this);
            g.dispose();
        }
    }

    private Rectangle getStringBounds(Graphics2D g2, String str) {
        FontRenderContext frc = g2.getFontRenderContext();
        GlyphVector gv = g2.getFont().createGlyphVector(frc, str);
        return gv.getPixelBounds(null, 0, 0);
    }

    void setSizeMaps(int total, int tile, int frame, int nTiles) {
        this.initialX = frame;
        this.initialY = frame;
        this.tileSize = tile;
        this.wallWidth = initialX;
        this.nTiles = nTiles;

    }

    public BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, 0, 0, original.getWidth(),
                original.getHeight(), null);
        g.dispose();
        return resized;
    }

    public void setShowTimesVisited(boolean show) {
        this.showTimesVisited = show;
    }

}

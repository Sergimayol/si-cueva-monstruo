package entities;

import environment.TileData;
import java.awt.Graphics2D;

/**
 *
 * @author ccf20
 */
public interface CaveEditor {
    
  public static int[][] aroundTiles = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    
  void enterCave(TileData[][] map);

  void exitCave(TileData[][] map);
  
  void restoreEntity();
  
  void paintComponent(Graphics2D g2, int x, int y, int size);
}

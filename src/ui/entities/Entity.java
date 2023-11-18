
package ui.entities;

import java.awt.Point;
import java.io.Serializable;

public abstract class Entity implements Serializable {

  protected Point position;

  protected Entity(int x, int y) {
    this.position = new Point(x, y);
  }

  protected Entity(Point position) {
    this.position = position;
  }

  public Point getPosition() {
    return position;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public void setPosition(int x, int y) {
    this.position = new Point(x, y);
  }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.awt.Point;
import java.io.Serializable;

/**
 *
 * @author ccf20
 */
public abstract class Entity implements Serializable {

  protected Point position;

  public Entity(int x, int y) {
    this.position = new Point(x, y);
  }

  public Entity(Point position) {
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

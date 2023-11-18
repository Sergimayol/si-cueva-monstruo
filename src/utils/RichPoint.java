/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.awt.Point;

/**
 *
 * @author ccf20
 */
public class RichPoint implements Comparable<RichPoint> {
        // x i y: coordenades fila, columna
    // previ : per guardar la referència des d'on s'ha arribat en aquest punt

    public int x;
    public int y;
    public RichPoint previous;

    public boolean visible = true; // variable auxiliar

    // Per calcular la f = g+h
    public int    distanceFromOrigin = Integer.MAX_VALUE;  // és la g de la funció d'avaluació
    public double distanceToEnd  = Integer.MAX_VALUE;  // és la h de la funció d'avaluació

    public RichPoint()
    {
        super();
    }

    public RichPoint(int x1, int y1, RichPoint previ)
    {
        super();
        this.x = x1;
        this.y = y1;
        this.previous = previ;
    }

    public RichPoint(RichPoint p1)
    {
        this.x = p1.x;
        this.y = p1.y;
    }
    
    public RichPoint(Point p1)
    {
        this.x = p1.x;
        this.y = p1.y;
    }

    public RichPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object other){   //per veure si dos punts són iguals
        if (other == null) return false;
        if (((RichPoint)other).x == this.x && ((RichPoint) other).y == this.y) return true;
        else return false;
    }

    public int compareTo(RichPoint punt2) {   // compara els valors de les heurístiques aplicades a dos punts
        double distanciaTotalDesdeLObjectiu = distanceToEnd + distanceFromOrigin;
        double nodeDistanciaTotalDesdeLObjectiu = punt2.distanceToEnd + punt2.distanceFromOrigin;

        if (distanciaTotalDesdeLObjectiu < nodeDistanciaTotalDesdeLObjectiu) {
            return -1;
        } else if (distanciaTotalDesdeLObjectiu > nodeDistanciaTotalDesdeLObjectiu) {
            return 1;
        } else {
            return 0;
        }
    }
}

package utils;

import java.awt.Point;

public class RichPoint implements Comparable<RichPoint> {

    public int x;
    public int y;
    public RichPoint previous;

    public boolean visible = true;

    public int distanceFromOrigin = Integer.MAX_VALUE;
    public double distanceToEnd = Integer.MAX_VALUE;

    public RichPoint() {
        super();
    }

    public RichPoint(int x1, int y1, RichPoint previ) {
        super();
        this.x = x1;
        this.y = y1;
        this.previous = previ;
    }

    public RichPoint(RichPoint p1) {
        this.x = p1.x;
        this.y = p1.y;
    }

    public RichPoint(Point p1) {
        this.x = p1.x;
        this.y = p1.y;
    }

    public RichPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof RichPoint))
            return false;
        return ((RichPoint) other).x == this.x && ((RichPoint) other).y == this.y;
    }

    @Override
    public int hashCode() {
        return this.x * 1000 + this.y;
    }

    public int compareTo(RichPoint punt2) {
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

package edu.hanyang.gridshift;

import edu.hanyang.kmbr.domain.Point;

import java.util.LinkedList;
import java.util.List;

public class Grid {

    private Centroid centroid;
    private double size;
    private List<Point> points;
    private boolean isValid;

    public Grid(final double size, final double x, final double y) {
        this.size = size;
        this.centroid = new Centroid(x, y);
        this.isValid = true;
        points = new LinkedList<>();
    }

    public Centroid getCentroid() {
        return centroid;
    }

    public int getNumOfPoints() {
        return this.points.size();
    }

    public void update(Point[] points) {
        this.points.clear();

        for (Point p: points) {
            if (p.getX() >= centroid.getX() - size/2 && p.getX() <= centroid.getX() + size/2 &&
                p.getY() >= centroid.getY() - size/2 && p.getY() <= centroid.getY() + size/2) {

                this.points.add(p);
            }
        }
    }

    public boolean isActive() {
        return points.size() > 0;
    }

    public boolean isValid() { return isValid; }

    public void setInvalid() {
        isValid = true;
    }

    public List<Point> getPoints() {
        return points;
    }
}

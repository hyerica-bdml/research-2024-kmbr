package edu.hanyang.kmbr.domain;

import edu.hanyang.kmbr.Config;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PointSet implements Externalizable {

    private Point[] xSortedPoints;
    private Point[] ySortedPoints;
    int size;

    public PointSet() {
        xSortedPoints = new Point[Config.POINTSET_SIZE + 1];
        ySortedPoints = new Point[Config.POINTSET_SIZE + 1];
        size = 0;
    }

    public PointSet(List<Point> points) {
        this();

        points.toArray(xSortedPoints);
        points.toArray(ySortedPoints);

        sort();
        size = points.size();
    }

    public PointSet(Point[] points) {
        this();

        int len = 0;
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                len = i;
            }
        }

        xSortedPoints = Arrays.copyOf(points, Config.POINTSET_SIZE + 1);
        ySortedPoints = Arrays.copyOf(points, Config.POINTSET_SIZE + 1);

        sort();

        size = -1;
        for (int i = 0; i < points.length; i += 1) {
            if (points[i] == null) {
                size = i;
                break;
            }
        }
        if (size == -1) size = points.length;
    }

    public int size() {
        return size;
    }

    public void add(Point p) {
        if (isFull())
            throw new RuntimeException("PointSet is full!");

        int xPos = -1, yPos = -1;

        for (int i = 0; i < size; i += 1) {
            if (xPos == -1 && p.getX() < xSortedPoints[i].getX())
                xPos = i;
            if (yPos == -1 && p.getY() < ySortedPoints[i].getY())
                yPos = i;

            if (xPos != -1 && yPos != -1) break;
        }

        if (xPos == -1) xPos = size;
        if (yPos == -1) yPos = size;

        for (int i = size; i > xPos; i -= 1)
            xSortedPoints[i] = xSortedPoints[i - 1];
        for (int i = size; i > yPos; i -= 1)
            ySortedPoints[i] = ySortedPoints[i - 1];

        xSortedPoints[xPos] = p;
        ySortedPoints[yPos] = p;

        size++;
    }

    public void remove(Point p) {
        if (isEmpty())
            throw new RuntimeException("PointSet is empty!");

        int xPos = -1, yPos = -1;

        for (int i = 0; i < size; i += 1) {
            if (xPos == -1 && p.equals(xSortedPoints[i]))
                xPos = i;
            if (yPos == -1 && p.equals(ySortedPoints[i]))
                yPos = i;

            if (xPos != -1 && yPos != -1) break;
        }

        if (xPos != -1 && yPos != -1) {
            for (int i = xPos + 1; i < size; i += 1) {
                xSortedPoints[i - 1] = xSortedPoints[i];
            }
            for (int i = yPos + 1; i < size; i += 1) {
                ySortedPoints[i - 1] = ySortedPoints[i];
            }

            xSortedPoints[size - 1] = null;
            ySortedPoints[size - 1] = null;
            size -= 1;
        }
    }

    public Point[] getXSortedPoints() {
        return xSortedPoints;
    }

    public Point[] getYSortedPoints() {
        return ySortedPoints;
    }

    public double getMinX() {
        return xSortedPoints[0].getX();
    }

    public double getMaxX() {
        return xSortedPoints[size - 1].getX();
    }

    public double getMinY() {
        return ySortedPoints[0].getY();
    }

    public double getMaxY() {
        return ySortedPoints[size - 1].getY();
    }

    public boolean isFull() {
        return size == xSortedPoints.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public PointSet split() {
        PointSet PointSet = new PointSet();
        int middlePoint = (size + 1) / 2;

        for (int i = middlePoint; i < xSortedPoints.length; i += 1) {
            Point p = xSortedPoints[middlePoint];
            PointSet.add(p);
            remove(p);
        }

        return PointSet;
    }

    public void merge(PointSet pointSet) {
        for (int i = 0; i < pointSet.size(); i += 1) {
            Point p = pointSet.xSortedPoints[i];
            add(p);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.size);

        for (int i = 0; i < this.size; i += 1)
            out.writeObject(this.xSortedPoints[i]);
        for (int i = 0; i < this.size; i += 1)
            out.writeObject(this.xSortedPoints[i]);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.size = in.readInt();

        for (int i = 0; i < this.size; i += 1)
            this.xSortedPoints[i] = (Point) in.readObject();
        for (int i = 0; i < this.size; i += 1)
            this.ySortedPoints[i] = (Point) in.readObject();
    }

    private void sort() {
        Arrays.sort(xSortedPoints, (p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            else if (p1 == null) return 1;
            else if (p2 == null) return -1;
            else if (p1.getX() > p2.getX()) return 1;
            else if (p1.getX() < p2.getX()) return -1;
            else return 0;
        });
        Arrays.sort(ySortedPoints, (p1, p2) -> {
            if (p1 == null && p2 == null) return 0;
            else if (p1 == null) return 1;
            else if (p2 == null) return -1;
            else if (p1.getY() > p2.getY()) return 1;
            else if (p1.getY() < p2.getY()) return -1;
            else return 0;
        });
    }

    @Override
    public String toString() {
        StringBuffer strBuf = new StringBuffer();

        strBuf.append("=== PointSet ===\n");

        for (int i = 0; i < size; i += 1) {
            Point p = xSortedPoints[i];
            strBuf.append("- ");
            strBuf.append(p.getId());
            strBuf.append(": ");
            strBuf.append(p.getX());
            strBuf.append(", ");
            strBuf.append(p.getY());
            strBuf.append("\n");
        }
        strBuf.append("================");

        return strBuf.toString();
    }
}

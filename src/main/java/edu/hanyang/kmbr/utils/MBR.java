package edu.hanyang.kmbr.utils;


import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class MBR implements Externalizable {

    private static final long serialVersionUID = -3415961007337923126L;
    public Point[] points;
    public int index;

    public int minXPointIndex;
    public int minYPointIndex;
    public int maxXPointIndex;
    public int maxYPointIndex;

    public MBR() {
        points = new Point[Config.K];
        index = 0;

        minXPointIndex = -1;
        minYPointIndex = -1;
        maxXPointIndex = -1;
        maxYPointIndex = -1;
    }

    public MBR(List<Point> Points) {
        this();

        for (Point p: Points) {
            if (index < this.points.length)
                this.points[index++] = p;
        }
        updateBoundaryPoints();
    }

    public MBR(Point[] Points) {
        this();

        for (Point p: Points) {
            if (p == null) break;

            if (index < this.points.length)
                this.points[index++] = p;
        }
        updateBoundaryPoints();
    }

    public double size() {
        double xLen = 0, yLen = 0;

        try {
            xLen = points[maxXPointIndex].getX() - points[minXPointIndex].getX();
            yLen = points[maxYPointIndex].getY() - points[minYPointIndex].getY();
        } catch (NullPointerException exc) {
            System.out.println(index);
            System.out.println(xLen + ", " + yLen);
            throw exc;
        }
        return (xLen + yLen)*2;
    }

    public double getArea() {
        double xLen = 0, yLen = 0;

        try {
            xLen = points[maxXPointIndex].getX() - points[minXPointIndex].getX();
            yLen = points[maxYPointIndex].getY() - points[minYPointIndex].getY();
        } catch (NullPointerException exc) {
            System.out.println(index);
            System.out.println(xLen + ", " + yLen);
            throw exc;
        }
        return xLen * yLen;
    }

    public long[] getPointIds() {
        long[] pointIds = new long[getPoints().length];
        for (int i = 0; i < getPoints().length; i += 1) {
            pointIds[i] = getPoints()[i].getId();
        }
        return pointIds;
    }

    public int length() {
        return index;
    }

    public Point[] getPoints() {
        return points;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.index = in.readInt();
        for (int i = 0; i < index; i += 1) {
            points[i] = (Point) in.readObject();
        }
        minXPointIndex = in.readInt();
        maxXPointIndex = in.readInt();
        minYPointIndex = in.readInt();
        maxYPointIndex = in.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(index);
        for (int i = 0; i < index; i += 1) {
            out.writeObject(points[i]);
        }
        out.writeInt(minXPointIndex);
        out.writeInt(maxXPointIndex);
        out.writeInt(minYPointIndex);
        out.writeInt(maxYPointIndex);
    }

    private void updateBoundaryPoints() {
        minXPointIndex = 0;
        minYPointIndex = 0;
        maxXPointIndex = 0;
        maxYPointIndex = 0;

        for (int i = 0; i < index; i += 1) {
            if (points[minXPointIndex].getX() > points[i].getX())
                minXPointIndex = i;
            if (points[minYPointIndex].getY() > points[i].getY())
                minYPointIndex = i;
            if (points[maxXPointIndex].getX() < points[i].getX())
                maxXPointIndex = i;
            if (points[maxYPointIndex].getY() < points[i].getY())
                maxYPointIndex = i;
        }
    }
}

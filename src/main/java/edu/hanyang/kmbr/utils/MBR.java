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
    public Point[] Points;
    public int index;

    public int minXPointIndex;
    public int minYPointIndex;
    public int maxXPointIndex;
    public int maxYPointIndex;

    public MBR() {
        Points = new Point[Config.K];
        index = 0;

        minXPointIndex = -1;
        minYPointIndex = -1;
        maxXPointIndex = -1;
        maxYPointIndex = -1;
    }

    public MBR(List<Point> Points) {
        this();

        for (Point p: Points) {
            if (index < this.Points.length)
                this.Points[index++] = p;
        }
        updateBoundaryPoints();
    }

    public MBR(Point[] Points) {
        this();

        for (Point p: Points) {
            if (p == null) break;

            if (index < this.Points.length)
                this.Points[index++] = p;
        }
        updateBoundaryPoints();
    }

    public double size() {
        double xLen = 0, yLen = 0;

        try {
            xLen = Points[maxXPointIndex].getX() - Points[minXPointIndex].getX();
            yLen = Points[maxYPointIndex].getY() - Points[minYPointIndex].getY();
        } catch (NullPointerException exc) {
            System.out.println(index);
            System.out.println(xLen + ", " + yLen);
            throw exc;
        }
        return (xLen + yLen)*2;
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
        return Points;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.index = in.readInt();
        for (int i = 0; i < index; i += 1) {
            Points[i] = (Point) in.readObject();
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
            out.writeObject(Points[i]);
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
            if (Points[minXPointIndex].getX() > Points[i].getX())
                minXPointIndex = i;
            if (Points[minYPointIndex].getY() > Points[i].getY())
                minYPointIndex = i;
            if (Points[maxXPointIndex].getX() < Points[i].getX())
                maxXPointIndex = i;
            if (Points[maxYPointIndex].getY() < Points[i].getY())
                maxYPointIndex = i;
        }
    }
}

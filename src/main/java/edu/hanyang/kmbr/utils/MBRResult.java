package edu.hanyang.kmbr.utils;

import edu.hanyang.kmbr.domain.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MBRResult implements Externalizable {

    protected MBR mbr;
    protected Point[] xSortedPoints;
    protected Point[] ySortedPoints;

    public MBRResult(final MBR mbr, final Point[] xSortedPoints, final Point[] ySortedPoints) {
        this.mbr = mbr;
        this.xSortedPoints = xSortedPoints;
        this.ySortedPoints = ySortedPoints;
    }

    public MBR getMBR() {
        return mbr;
    }

    public Point[] getXSortedPoints() {
        return xSortedPoints;
    }

    public Point[] getYSortedPoints() {
        return ySortedPoints;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        mbr = (MBR) in.readObject();

        final int xSortedPointsLength = in.readInt();
        xSortedPoints = new Point[xSortedPointsLength];
        for (int i = 0; i < xSortedPointsLength; i += 1) {
            xSortedPoints[i] = (Point) in.readObject();
        }

        final int ySortedPointsLength = in.readInt();
        ySortedPoints = new Point[ySortedPointsLength];
        for (int i = 0; i < ySortedPointsLength; i += 1) {
            ySortedPoints[i] = (Point) in.readObject();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(mbr);

        out.writeInt(xSortedPoints.length);
        for (int i = 0; i < xSortedPoints.length; i += 1) {
            out.writeObject(xSortedPoints[i]);
        }

        out.writeInt(ySortedPoints.length);
        for (int i = 0; i < xSortedPoints.length; i += 1) {
            out.writeObject(ySortedPoints[i]);
        }
    }
}

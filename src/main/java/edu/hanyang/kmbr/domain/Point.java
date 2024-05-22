package edu.hanyang.kmbr.domain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Point implements Externalizable {

    private static final long serialVersionUID = 8447430827315054366L;
    private double x, y;
    private long id;

    public Point(long id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
    public Point() {}

    public long getId() {
        return id;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    public void set(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point p = (Point) obj;
            return p.getId() == id;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + x + ", " + y + ")";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        out.writeDouble(x);
        out.writeDouble(y);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.id = in.readLong();
        this.x = in.readDouble();
        this.y = in.readDouble();
    }
}

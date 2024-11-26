package edu.hanyang.kmbr.domain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Point implements Externalizable {

    private static final long serialVersionUID = 8447430827315054366L;
    private Coordinate coordinate;
    private long id;

    public Point(final long id, final double x, final double y) {
        this.id = id;
        this.coordinate = new Coordinate(x, y);
    }
    public Point() {}

    public long getId() {
        return id;
    }

    public double getX() {
        return coordinate.getX();
    }
    public double getY() {
        return coordinate.getY();
    }

    public void set(final double x, final double y) {
        coordinate.set(x, y);
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
        return "(" + id + ", " + coordinate.getX() + ", " + coordinate.getY() + ")";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        out.writeObject(coordinate);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = in.readLong();
        this.coordinate = (Coordinate) in.readObject();
    }
}

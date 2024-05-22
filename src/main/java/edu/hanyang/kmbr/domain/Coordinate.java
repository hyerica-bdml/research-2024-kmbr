package edu.hanyang.kmbr.domain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Coordinate implements Externalizable {

    private static final long serialVersionUID = 582667153494550233L;

    private double x;
    private double y;

    public Coordinate(final double x, final double y) {
        set(x, y);
    }

    public Coordinate() {
    }

    public void set(final double x, final double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        x = in.readDouble();
        y = in.readDouble();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
    }
}

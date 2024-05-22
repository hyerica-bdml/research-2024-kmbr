package edu.hanyang.kmbr.domain;

import edu.hanyang.kmbr.domain.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ClusterAssignment implements Externalizable {

    private static final long serialVersionUID = -3864729274878161137L;
    private int clusterIndex;
    private Point point;

    public ClusterAssignment(final int clusterIndex, final Point point) {
        this.clusterIndex = clusterIndex;
        this.point = point;
    }

    public ClusterAssignment() {
    }

    public Point getPoint() {
        return point;
    }

    public int getClusterIndex() {
        return clusterIndex;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        clusterIndex = in.readInt();
        point = (Point) in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(clusterIndex);
        out.writeObject(point);
    }
}

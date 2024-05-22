package edu.hanyang.kmbr.domain;

import edu.hanyang.kmbr.domain.Point;

public class ClusterAssignment {

    private int clusterIndex;
    private Point point;

    public ClusterAssignment(final int clusterIndex, final Point point) {
        this.clusterIndex = clusterIndex;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public int getClusterIndex() {
        return clusterIndex;
    }
}

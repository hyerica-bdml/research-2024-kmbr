package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.List;

public class SmallestMBRResult {

    protected final List<ClusterAssignment> clusterAssignments;
    protected final ClusterAssignment top, bottom, left, right;

    public SmallestMBRResult(final List<ClusterAssignment> clusterAssignments) {
        this.clusterAssignments = clusterAssignments;

        ClusterAssignment top = clusterAssignments.get(0);
        ClusterAssignment bottom = clusterAssignments.get(0);
        ClusterAssignment left = clusterAssignments.get(0);
        ClusterAssignment right = clusterAssignments.get(0);

        for (int i = 1; i < clusterAssignments.size(); i += 1) {
            ClusterAssignment c = clusterAssignments.get(i);
            if (c.getPoint().getX() < left.getPoint().getX()) left = c;
            if (c.getPoint().getX() > right.getPoint().getX()) right = c;
            if (c.getPoint().getY() > top.getPoint().getY()) top = c;
            if (c.getPoint().getY() < bottom.getPoint().getY()) bottom = c;
        }

        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public double size() {
        return 2*(top.getPoint().getY() - bottom.getPoint().getY() + right.getPoint().getX() - left.getPoint().getX());
    }

    public double getArea() {
        return (top.getPoint().getY() - bottom.getPoint().getY()) * (right.getPoint().getX() - left.getPoint().getX());
    }

    public List<ClusterAssignment> getPoints() {
        return clusterAssignments;
    }
}

package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Rectangle {

    private List<ClusterAssignment> clusterAssignments;

    public Rectangle() {
        clusterAssignments = new LinkedList<>();
    }

    public Rectangle(final List<ClusterAssignment> clusterAssignments) {
        this.clusterAssignments = new LinkedList<>(clusterAssignments);
        this.clusterAssignments.sort(Comparator.comparingDouble(c -> c.getPoint().getX()));
    }

    public void add(final ClusterAssignment clusterAssignment) {
        if (clusterAssignments.size() == 0) {
            clusterAssignments.add(clusterAssignment);
            return;
        }
        for (int i = 0; i < clusterAssignments.size(); i += 1) {
            if (clusterAssignment.getPoint().getX() < clusterAssignments.get(i).getPoint().getX()) {
                clusterAssignments.add(i, clusterAssignment);
                break;
            }
            else if (i == clusterAssignments.size() - 1) {
                clusterAssignments.add(clusterAssignment);
                break;
            }
        }
    }

    public Rectangle split() {
        List<ClusterAssignment> xSortedList = new LinkedList<>(clusterAssignments);
        xSortedList.sort(Comparator.comparingDouble(c -> c.getPoint().getX()));
        int midIndex = (xSortedList.size() + 1)/2;

        Rectangle right = new Rectangle();
        right.clusterAssignments = new LinkedList<>(xSortedList.subList(midIndex, xSortedList.size()));
        clusterAssignments = new LinkedList<>(xSortedList.subList(0, midIndex));

        return right;
    }

    public int size() {
        return clusterAssignments.size();
    }

    public Rectangle duplicate() {
        return new Rectangle(clusterAssignments);
    }

    public Rectangle merge(final Rectangle other) {
        Rectangle rect = new Rectangle(clusterAssignments);
        for (ClusterAssignment clusterAssignment: other.clusterAssignments)
            rect.add(clusterAssignment);
        return rect;
    }

    public List<ClusterAssignment> getClusterAssignments() {
        return clusterAssignments;
    }

    public double getMinX() { return clusterAssignments.get(0).getPoint().getX(); }
    public double getMaxX() { return clusterAssignments.get(clusterAssignments.size() - 1).getPoint().getX(); }
}

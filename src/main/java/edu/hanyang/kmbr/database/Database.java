package edu.hanyang.kmbr.database;

import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Coordinate;
import edu.hanyang.kmbr.domain.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.stream.Collectors;

public class Database implements Externalizable {

    private Map<Long, ClusterAssignment> assignments;
    private Map<Integer, Coordinate> clusterMean;
    private Map<Integer, Double> clusterStd;
    private long maxPointId;

    public Database() {
        assignments = new TreeMap<>();
        clusterMean = new HashMap<>();
        clusterStd = new HashMap<>();
        maxPointId = 0;
    }

    public void addPoint(final ClusterAssignment assignment) {
        assignments.put(assignment.getPoint().getId(), assignment);
        if (assignment.getPoint().getId() > maxPointId) maxPointId = assignment.getPoint().getId();
    }

    public ClusterAssignment getPointById(final long id) {
        return assignments.getOrDefault(id, null);
    }

    public void removePoint(final ClusterAssignment p) {
        assignments.remove(p.getPoint().getId());
    }

    public void removePoint(final long id) {
        assignments.remove(id);
    }

    public void clearPoints() {
        assignments.clear();
        clusterMean.clear();
        clusterStd.clear();
    }

    public ClusterAssignment[] getPoints() {
        ClusterAssignment[] pointArr = new ClusterAssignment[assignments.size()];
        assignments.values().toArray(pointArr);
        return pointArr;
    }

    public ClusterAssignment[] getPointsByClusterIndex(final int clusterIndex) {
        List<ClusterAssignment> filteredPoints = assignments.values().stream().filter(a -> a.getClusterIndex() == clusterIndex).collect(Collectors.toList());
        ClusterAssignment[] points = new ClusterAssignment[filteredPoints.size()];
        return filteredPoints.toArray(points);
    }

//    public void updateClusterMean() {
//        Map<Integer, Integer> numOfPointsInCluster = new HashMap<>();
//        for (ClusterAssignment assignment: clusterAssignments) {
//            int clusterIndex = assignment.getClusterIndex();
//            int numOfPoints = numOfPointsInCluster.getOrDefault(clusterIndex, 0);
//            numOfPointsInCluster.put(clusterIndex, numOfPoints + 1);
//
//            Point p = assignment.getPoint();
//            double x = p.getX();
//            double y = p.getY();
//
//            Coordinate mean = clusterMean.getOrDefault(clusterIndex, new Coordinate(0, 0));
//            mean.set(mean.getX() + x, mean.getY() + y);
//        }
//
//        for (int c = 0; c < numOfPointsInCluster.size(); i += 1) {
//            if (clusterMean.containsKey(c)) {
//                Coordinate mean = clusterMean.get(c);
//                mean.set(mean.getX() / numOfPointsInCluster.get(c), mean.getY() / numOfPointsInCluster.get(c));
//            }
//        }
//    }


    public Set<Integer> getClusterIndices() {
        return clusterMean.keySet();
    }

    public void updateClusterStats(final Map<Integer, Coordinate> clusterMean,
                                   final Map<Integer, Double> clusterStd) {
        for (int c: clusterMean.keySet()) {
            this.clusterMean.put(c, clusterMean.get(c));
            this.clusterStd.put(c, clusterStd.get(c));
        }
    }

    public Coordinate getClusterMeanByIndex(final int clusterIndex) {
        return clusterMean.get(clusterIndex);
    }
    public double getClusterStdByIndex(final int clusterIndex) {
        return clusterStd.get(clusterIndex);
    }

    public Map<Integer, Coordinate> getClusterMean() {
        return clusterMean;
    }

    public Map<Integer, Double> getClusterStd() {
        return clusterStd;
    }

    public double getClusterStd(final int clusterIndex) {
        return clusterStd.get(clusterIndex);
    }

    public long getMaxPointId() {
        return maxPointId;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            ClusterAssignment p = (ClusterAssignment) in.readObject();
            assignments.put(p.getPoint().getId(), p);

            if (p.getPoint().getId() > maxPointId) maxPointId = p.getPoint().getId();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(assignments.size());
        assignments.forEach((id, p) -> {
            try {
                out.writeObject(p);
            } catch (IOException ignored) {}
        });
    }
}

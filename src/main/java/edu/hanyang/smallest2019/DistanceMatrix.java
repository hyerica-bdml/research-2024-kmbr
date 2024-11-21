package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DistanceMatrix {

    private List<ClusterAssignment> sortedPoints;
    private SortType sortType;
    private List<List<Double>> distanceMat;

    public DistanceMatrix(final List<ClusterAssignment> points, final SortType sortType) {
//        System.out.println(points.size());
        this.sortedPoints = new LinkedList<>(points);
        this.sortType = sortType;

        if (sortType == SortType.X)
            this.sortedPoints.sort(Comparator.comparingDouble(c -> c.getPoint().getX()));
        else
            this.sortedPoints.sort(Comparator.comparingDouble(c -> c.getPoint().getY()));

        computeDistanceMatrix();
    }

    public DistanceMatrix() {}

    public void removePoint(final ClusterAssignment point) {
//        long startTime = System.currentTimeMillis();

        int index = sortedPoints.indexOf(point);
        if (index == -1) return;
        distanceMat.remove(index);

        for (List<Double> doubles : distanceMat) {
            doubles.remove(index);
        }

        sortedPoints.remove(point);

//        System.out.println("REMOVAL TIME: " + (System.currentTimeMillis() - startTime)/1000.0);
    }

    public DistanceMatrix duplicate() {
//        long startTime = System.currentTimeMillis();

        DistanceMatrix newMat = new DistanceMatrix();
        newMat.sortedPoints = new LinkedList<>(sortedPoints);
        newMat.sortType = sortType;
        newMat.distanceMat = new LinkedList<>(distanceMat);
        for (int i = 0; i < distanceMat.size(); i += 1)
            newMat.distanceMat.set(i, new LinkedList<>(distanceMat.get(i)));

//        System.out.println("DUPLIATED TIME: " + (System.currentTimeMillis() - startTime)/1000.0);

        return newMat;
    }

    public List<ClusterAssignment> get() {
        double minDist = Double.MAX_VALUE;
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < distanceMat.size() - (Config.K - 1); i += 1) {
            double tempDist = distanceMat.get(i).get(i + Config.K - 1);
            if (minDist > tempDist) {
                minDist = tempDist;
                startIndex = i;
                endIndex = startIndex + Config.K;
            }
        }

        return sortedPoints.subList(startIndex, endIndex);
    }

//    public List<ClusterAssignment> get() {
//        double minDist = Double.MAX_VALUE;
//        int startIndex = -1;
//        int endIndex = -1;
//
//        for (int i = 0; i < sortedPoints.size() - (Config.K - 1); i += 1) {
//            double tempDist;
//            if (sortType == SortType.X)
//                tempDist = sortedPoints.get(i + Config.K - 1).getPoint().getX() - sortedPoints.get(i).getPoint().getX();
//            else
//                tempDist = sortedPoints.get(i + Config.K - 1).getPoint().getY() - sortedPoints.get(i).getPoint().getY();
//
//            if (minDist > tempDist) {
//                minDist = tempDist;
//                startIndex = i;
//                endIndex = startIndex + Config.K;
//            }
//        }
//
//        return sortedPoints.subList(startIndex, endIndex);
//    }

    public void print() {
        for (int i = 0; i < distanceMat.size(); i += 1) {
            for (int j = 0; j < distanceMat.size(); j += 1)
                System.out.print(distanceMat.get(i).get(j) + " ");
            System.out.println();
        }
    }

    private void computeDistanceMatrix() {
//        long startTime = System.currentTimeMillis();

        double[][] distanceMat = new double[sortedPoints.size()][sortedPoints.size()];

        for (int i = 0; i < sortedPoints.size() - 1; i += 1) {
            distanceMat[i][i] = 0;

            for (int j = i + 1; j < sortedPoints.size(); j += 1) {
                if (this.sortType == SortType.X)
                    distanceMat[i][j] = sortedPoints.get(j).getPoint().getX() - sortedPoints.get(i).getPoint().getX();
                else
                    distanceMat[i][j] = sortedPoints.get(j).getPoint().getY() - sortedPoints.get(i).getPoint().getY();

                distanceMat[j][i] = distanceMat[i][j];
            }
        }

        distanceMat[sortedPoints.size() - 1][sortedPoints.size() - 1] = 0;

        this.distanceMat = new LinkedList<>();
        for (int i = 0; i < sortedPoints.size(); i += 1) {
            this.distanceMat.add(new LinkedList<>());

            for (int j = 0; j < sortedPoints.size(); j += 1)
                this.distanceMat.get(i).add(distanceMat[i][j]);
        }

//        System.out.println("DISTANCE COMPUTE TIME: " + (System.currentTimeMillis() - startTime)/1000.0);
    }

    enum SortType {
        X, Y;
    }
}

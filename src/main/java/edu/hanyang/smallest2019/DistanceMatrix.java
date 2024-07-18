package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class DistanceMatrix {

    private final List<ClusterAssignment> sortedPoints;
    private final SortType sortType;
    private List<List<Double>> distanceMat;

    public DistanceMatrix(final List<ClusterAssignment> points, final SortType sortType) {
        this.sortedPoints = new LinkedList<>(points);
        this.sortType = sortType;

        if (sortType == SortType.X)
            this.sortedPoints.sort(Comparator.comparingDouble(c -> c.getPoint().getX()));
        else
            this.sortedPoints.sort(Comparator.comparingDouble(c -> c.getPoint().getY()));

        computeDistanceMatrix();
    }

    public void removePoint(final ClusterAssignment point) {
        int index = sortedPoints.indexOf(point);
        if (index == -1) return;
        distanceMat.remove(index);

        for (List<Double> doubles : distanceMat) {
            doubles.remove(index);
        }

        sortedPoints.remove(point);
    }

    public List<ClusterAssignment> get() {
        double minDist = Double.MAX_VALUE;
        int startIndex = -1;
        int endIndex = -1;

        for (int i = 0; i < distanceMat.size() - (Config.K - 1); i += 1) {
            double tempDist = distanceMat.get(i).get(i + Config.K - 1);
//            System.out.println(tempDist);
            if (minDist > tempDist) {
                minDist = tempDist;
                startIndex = i;
                endIndex = startIndex + Config.K;
            }
        }

        return sortedPoints.subList(startIndex, endIndex);
    }

    public void print() {
        for (int i = 0; i < distanceMat.size(); i += 1) {
            for (int j = 0; j < distanceMat.size(); j += 1)
                System.out.print(distanceMat.get(i).get(j) + " ");
            System.out.println();
        }
    }

    private void computeDistanceMatrix() {
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
    }

    enum SortType {
        X, Y;
    }
}

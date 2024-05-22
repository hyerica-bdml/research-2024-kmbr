package edu.hanyang.kmbr.database;

import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

import java.util.List;
import java.util.Random;

public class DatabaseInteractor {

    private static DatabaseInteractor inst = null;

    public static DatabaseInteractor getInstance() {
        synchronized (DatabaseInteractor.class) {
            if (inst == null) {
                inst = new DatabaseInteractor();
            }
        }
        return inst;
    }

    public Database db;
    public PointFactory pointFactory;

    private DatabaseInteractor() {
        db = new Database();
        pointFactory = new PointFactory(db);
    }

    public double[] getRandomClusterProbs(final int numOfClusters) {
        return pointFactory.getRandomClusterProbabilities(numOfClusters);
    }

    public ClusterAssignment[] generateRandomPoints(final int numOfPoints,
                                                    final int numOfClusters,
                                                    final double[] xLimits,
                                                    final double[] yLimits,
                                                    final double[] stdLimits) {

        double[] clusterProbs = pointFactory.getRandomClusterProbabilities(numOfClusters);
        return pointFactory.generateRandomPoints(numOfPoints, numOfClusters, xLimits, yLimits, stdLimits, clusterProbs);
    }

    public ClusterAssignment[] generateRandomPoints(final int numOfPoints,
                                        final int numOfClusters,
                                        final double[] xLimits,
                                        final double[] yLimits,
                                        final double[] stdLimits,
                                        final double[] clusterProbs) {

        return pointFactory.generateRandomPoints(numOfPoints, numOfClusters, xLimits, yLimits, stdLimits, clusterProbs);
    }

    public double[] getRandomClusterCDF(final int numOfClusters) {
        double[] clusterProbs = pointFactory.getRandomClusterProbabilities(numOfClusters);
        return pointFactory.getClusterCDF(clusterProbs);
    }

    public Point newPoint(final double x, final double y) {
        return pointFactory.newPoint(x, y);
    }

    public Point[] getPoints() {
        return db.getPoints();
    }
}

package edu.hanyang.kmbr.database;

import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Coordinate;
import edu.hanyang.kmbr.domain.Point;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

    public ClusterAssignment[] generateRandomPoints(final int numOfPoints,
                                                    final Map<Integer, Coordinate> clusterMean,
                                                    final Map<Integer, Double> clusterStd,
                                                    final double[] clusterProbs) {

        return pointFactory.generateRandomPoints(numOfPoints, clusterMean, clusterStd, clusterProbs);
    }

    public ClusterAssignment[] generateRandomPoints(final int numOfPoints,
                                                    final Map<Integer, Coordinate> clusterMean,
                                                    final Map<Integer, Double> clusterStd) {

        double[] clusterProbs = pointFactory.getRandomClusterProbabilities(db.getClusterIndices().size());
        return pointFactory.generateRandomPoints(numOfPoints, clusterMean, clusterStd, clusterProbs);
    }

    public double[] getRandomClusterCDF(final int numOfClusters) {
        double[] clusterProbs = pointFactory.getRandomClusterProbabilities(numOfClusters);
        return pointFactory.getClusterCDF(clusterProbs);
    }

    public ClusterAssignment newPoint(final double x, final double y, final int clusterIndex) {
        return pointFactory.newPoint(x, y, clusterIndex);
    }

    public ClusterAssignment newPoint(final double x, final double y) {
        return pointFactory.newPoint(x, y);
    }

    public ClusterAssignment[] getPoints() {
        return db.getPoints();
    }

    public ClusterAssignment getPointById(final long id) {
        return db.getPointById(id);
    }

    public ClusterAssignment[] getPointsByClusterIndex(final int clusterIndex) {
        return db.getPointsByClusterIndex(clusterIndex);
    }

    public Set<Integer> getClusterIndices() {
        return db.getClusterIndices();
    }

    public Map<Integer, Coordinate> getClusterMean() {
        return db.getClusterMean();
    }

    public Map<Integer, Double> getClusterStd() {
        return db.getClusterStd();
    }

    public Coordinate getClusterMeanByIndex(final int clusterIndex) {
        return db.getClusterMeanByIndex(clusterIndex);
    }

    public double getClusterStdByIndex(final int clusterIndex) {
        return db.getClusterStdByIndex(clusterIndex);
    }

    public void clearPoints() {
        db.clearPoints();
    }
}

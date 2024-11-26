package edu.hanyang.kmbr.database;

import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Coordinate;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.domain.PointUtilities;

import java.io.*;
import java.util.*;

public class PointFactory {

    private Database db;

    public PointFactory(final Database db) {
        this.db = db;
    }

    public ClusterAssignment newPoint(final double x, final double y, final int clusterIndex) {
        long newId = db.getMaxPointId() + 1;
        Point p = new Point(newId, x, y);
        ClusterAssignment a = new ClusterAssignment(clusterIndex, p);
        db.addPoint(a);
        return a;
    }

    public ClusterAssignment newPoint(final long id, final double x, final double y, final int clusterIndex) {
        long newId = id;
        Point p = new Point(newId, x, y);
        ClusterAssignment a = new ClusterAssignment(clusterIndex, p);
        db.addPoint(a);
        return a;
    }

    public ClusterAssignment newPoint(final double x, final double y) {
        long newId = db.getMaxPointId() + 1;
        Point p = new Point(newId, x, y);

        int optimalClusterIndex = -1;
        double optimalDistance = 99999999;
        for (int clusterIndex: db.getClusterIndices()) {
            Coordinate mean = db.getClusterMeanByIndex(clusterIndex);
            double std = db.getClusterStd(clusterIndex);

            double dist = PointUtilities.computeDistance(p, mean, std);
            if (dist < optimalDistance) {
                optimalDistance = dist;
                optimalClusterIndex = clusterIndex;
            }
        }
        ClusterAssignment assignment = new ClusterAssignment(optimalClusterIndex, p);
        db.addPoint(assignment);
        return assignment;
    }

    public ClusterAssignment[] readPointsFromFile(final String dataPath) {
        try (FileReader freader = new FileReader(dataPath);
             BufferedReader reader = new BufferedReader(freader, 8192)) {

            final int numOfPoints = Integer.parseInt(reader.readLine().strip());

            ClusterAssignment[] points = new ClusterAssignment[numOfPoints];

            for (int i = 0; i < numOfPoints; i += 1) {
                String line = reader.readLine();
                String[] splited = line.split(" ");

                int clusterIndex = Integer.parseInt(splited[0]);
                double x = Double.parseDouble(splited[1]);
                double y = Double.parseDouble(splited[2]);

                points[i] = newPoint(x, y, clusterIndex);
            }

            return points;
        } catch (IOException ignored) {
            return null;
        }
    }

    public ClusterAssignment[] generateRandomPoints(final int numOfPoints,
                                                    final int numOfClusters,
                                                    final double[] xLimits,
                                                    final double[] yLimits,
                                                    final double[] stdLimits,
                                                    final double[] clusterProbs) {

//        ClusterAssignment[] points = new ClusterAssignment[numOfPoints];
        ClusterAssignment[] points = new ClusterAssignment[numOfPoints];
        Random random = new Random(System.currentTimeMillis());

        int[] numOfPointsPerCluster = new int[clusterProbs.length];
        for (int i = 0; i < clusterProbs.length; i += 1) {
            numOfPointsPerCluster[i] = (int) Math.ceil(numOfPoints * clusterProbs[i]);
        }

        Map<Integer, Coordinate> clusterMean = new HashMap<>();
        Map<Integer, Double> clusterStd = new HashMap<>();

        int startIndex = 0;
        outerLoop: for (int c = 0; c < clusterProbs.length; c++) {

            double clusterX = random.nextDouble() * (xLimits[1] - xLimits[0]) + xLimits[0];
            double clusterY = random.nextDouble() * (yLimits[1] - yLimits[0]) + yLimits[0];
            double std = random.nextDouble() * (stdLimits[1] - stdLimits[0]) + stdLimits[0];

            clusterMean.put(c, new Coordinate(clusterX, clusterY));
            clusterStd.put(c, std);

            for (int i = 0; i < numOfPointsPerCluster[c]; i++) {
                if (i + startIndex == numOfPoints) break outerLoop;

                double x = random.nextGaussian() * std + clusterX;
                double y = random.nextGaussian() * std + clusterY;

//                if (x > clusterX + 3*std) x = clusterX + 3*std;
//                else if (x < clusterX - 3*std) x = clusterX - 3*std;
//
//                if (y > clusterY + 3*std) y = clusterY + 3*std;
//                else if (y < clusterY - 3*std) y = clusterY - 3*std;

                points[startIndex + i] = newPoint(x, y, c);
            }

            startIndex += numOfPointsPerCluster[c];
        }
//        System.out.println();
        db.updateClusterStats(clusterMean, clusterStd);

        return points;
    }

    public ClusterAssignment[] generateRandomPoints(final int numOfPoints,
                                                    final Map<Integer, Coordinate> clusterMean,
                                                    final Map<Integer, Double> clusterStd,
                                                    final double[] clusterProbs) {

//        ClusterAssignment[] points = new ClusterAssignment[numOfPoints];
        ClusterAssignment[] points = new ClusterAssignment[numOfPoints];
        Random random = new Random(System.currentTimeMillis());

        int[] numOfPointsPerCluster = new int[clusterProbs.length];
        for (int i = 0; i < clusterProbs.length; i += 1) {
            numOfPointsPerCluster[i] = (int) Math.ceil(numOfPoints * clusterProbs[i]);
        }

        int startIndex = 0;
        outerLoop: for (int c: db.getClusterIndices()) {

            double clusterX = clusterMean.get(c).getX();
            double clusterY = clusterMean.get(c).getY();
            double std = clusterStd.get(c);

            for (int i = 0; i < numOfPointsPerCluster[c]; i++) {
                if (i + startIndex == numOfPoints) break outerLoop;

                double x = random.nextGaussian() * std + clusterX;
                double y = random.nextGaussian() * std + clusterY;

                points[startIndex + i] = newPoint(x, y, c);
            }

            startIndex += numOfPointsPerCluster[c];
        }
//        System.out.println();
        db.updateClusterStats(clusterMean, clusterStd);

        return points;
    }

    public double[] getRandomClusterProbabilities(final int numOfCluster, final double amp) {
        double[] clusterProbabilities = new double[numOfCluster];
        Random random = new Random(System.currentTimeMillis());

        int numOfMajorCluster = 1;
        if (numOfCluster > 10)
            numOfMajorCluster = random.nextInt((int) Math.ceil(numOfCluster*0.1) - 1) + 1;
//        System.out.println(numOfMajorCluster);

        int count = 0;
        for (int i = 0; i < numOfCluster; i += 1) {
            if (count < numOfMajorCluster && random.nextDouble() < 0.1) {
                clusterProbabilities[i] = random.nextDouble()*amp;
                count += 1;
            }
            else {
                clusterProbabilities[i] = random.nextDouble();
            }
        }

//        for (int i = 0; i < numOfMajorCluster; i += 1)
//            clusterProbabilities[i] = random.nextDouble()*100;
//        for (int i = numOfMajorCluster; i < numOfCluster; i += 1)
//            clusterProbabilities[i] = random.nextDouble();

        double sum = 0;
        for (int i = 0; i < numOfCluster; i += 1)
            sum += clusterProbabilities[i];

        for (int i = 0; i < numOfCluster; i += 1)
            clusterProbabilities[i] /= sum;

        return clusterProbabilities;
    }

    public double[] getRandomClusterProbabilities(final int numOfCluster) {
        double[] clusterProbabilities = new double[numOfCluster];
        Random random = new Random(System.currentTimeMillis());

        int numOfMajorCluster = 1;
        if (numOfCluster > 10)
            numOfMajorCluster = random.nextInt((int) Math.ceil(numOfCluster*0.1) - 1) + 1;
//        System.out.println(numOfMajorCluster);

        int count = 0;
        for (int i = 0; i < numOfCluster; i += 1) {
            if (count < numOfMajorCluster && random.nextDouble() < 0.1) {
                clusterProbabilities[i] = random.nextDouble()*50;
                count += 1;
            }
            else {
                clusterProbabilities[i] = random.nextDouble();
            }
        }

//        for (int i = 0; i < numOfMajorCluster; i += 1)
//            clusterProbabilities[i] = random.nextDouble()*100;
//        for (int i = numOfMajorCluster; i < numOfCluster; i += 1)
//            clusterProbabilities[i] = random.nextDouble();

        double sum = 0;
        for (int i = 0; i < numOfCluster; i += 1)
            sum += clusterProbabilities[i];

        for (int i = 0; i < numOfCluster; i += 1)
            clusterProbabilities[i] /= sum;

        return clusterProbabilities;
    }

    public double[] getClusterCDF(double[] clusterProbs) {
        double acc = 0.0;
        double[] cdf = new double[clusterProbs.length];

        for (int i = 0; i < clusterProbs.length; i++) {
            acc += clusterProbs[i];
            cdf[i] = acc;
        }

        return cdf;
    }
}

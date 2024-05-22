package edu.hanyang.kmbr.database;

import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PointFactory {

    private Database db;

    public PointFactory(final Database db) {
        this.db = db;
    }

    public Point newPoint(final double x, final double y) {
        long newId = db.getMaxPointId() + 1;
        Point p = new Point(newId, x, y);
        db.addPoint(p);
        return p;
    }

    public Point[] readPointsFromFile(final String dataPath) {
        try (FileReader freader = new FileReader(dataPath);
             BufferedReader reader = new BufferedReader(freader, 8192)) {

            final int numOfPoints = Integer.parseInt(reader.readLine().strip());

            Point[] points = new Point[numOfPoints];

            for (int i = 0; i < numOfPoints; i += 1) {
                String line = reader.readLine();
                String[] splited = line.split(" ");

                int cluster_index = Integer.parseInt(splited[0]);
                double x = Double.parseDouble(splited[1]);
                double y = Double.parseDouble(splited[2]);

                points[i] = newPoint(x, y);
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

        ClusterAssignment[] points = new ClusterAssignment[numOfPoints];
        Random random = new Random(System.currentTimeMillis());

        int[] numOfPointsPerCluster = new int[clusterProbs.length];
        for (int i = 0; i < clusterProbs.length; i += 1) {
            numOfPointsPerCluster[i] = (int) Math.ceil(numOfPoints * clusterProbs[i]);
        }

        int startIndex = 0;
        outerLoop: for (int c = 0; c < clusterProbs.length; c++) {

            double clusterX = random.nextDouble() * (xLimits[1] - xLimits[0]) + xLimits[0];
            double clusterY = random.nextDouble() * (yLimits[1] - yLimits[0]) + yLimits[0];
            double std = random.nextDouble() * (stdLimits[1] - stdLimits[0]) + stdLimits[0];

            for (int i = 0; i < numOfPointsPerCluster[c]; i++) {
                if (i + startIndex == numOfPoints) break outerLoop;

                double x = random.nextGaussian() * std + clusterX;
                double y = random.nextGaussian() * std + clusterY;

//                if (x > clusterX + 3*std) x = clusterX + 3*std;
//                else if (x < clusterX - 3*std) x = clusterX - 3*std;
//
//                if (y > clusterY + 3*std) y = clusterY + 3*std;
//                else if (y < clusterY - 3*std) y = clusterY - 3*std;

                Point p = newPoint(x, y);
                points[startIndex + i] = new ClusterAssignment(c, p);
            }

            startIndex += numOfPointsPerCluster[c];
        }
        System.out.println();

        return points;
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
                clusterProbabilities[i] = random.nextDouble()*100;
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

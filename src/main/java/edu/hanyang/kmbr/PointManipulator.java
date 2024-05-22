package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.DataWriter;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.Utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PointManipulator {

    private DatabaseInteractor db;
    private KMBRInteractor kmbr;
    private DataWriter writer;

    public PointManipulator(final DatabaseInteractor db,
                            final KMBRInteractor kmbr,
                            final DataWriter writer) {
        this.db = db;
        this.kmbr = kmbr;
        this.writer = writer;
    }

    public void moveRandomPoints(final ClusterAssignment[] points,
                                 final int minMove,
                                 final int maxMove,
                                 final double[] moveClusterProbs) {

        Random random = new Random(System.currentTimeMillis());
        int move = random.nextInt(maxMove - minMove) + minMove;

        for (int i = 0; i < move; i++) {
            ClusterAssignment assignment = null;
            while (assignment == null) {
                int clusterIndex = getRandomClusterIndex(moveClusterProbs, random);
                assignment = sampleByClusterIndex(points, clusterIndex, random);
            }

            Point p = assignment.getPoint();

            double x = p.getX() + random.nextGaussian()/points.length*50.0 + random.nextDouble()*1e-6;
            double y = p.getY() + random.nextGaussian()/points.length*50.0 + random.nextDouble()*1e-6;

//            System.out.println("POINT MOVE " + p.getId() + ": " + p.getX());
            movePoint(p, x, y);
        }
    }

//    public void moveRandomPoints(final Point[] points,
//                                 final int minMove,
//                                 final int maxMove,
//                                 final double[] clusterProbs) {
//
//        Random random = new Random(System.currentTimeMillis());
//        int move = random.nextInt(maxMove - minMove) + minMove;
//
//        int[] numOfPointsInCluster = new int[clusterProbs.length];
//        for (int i = 0; i < clusterProbs.length; i += 1) {
//            numOfPointsInCluster[i] = (int) Math.ceil(points.length * clusterProbs[i]);
////            System.out.println(i + ": " + clusterProbs[i]);
//        }
//
//        for (int i = 0; i < move; i++) {
//            int clusterIndex = getRandomClusterIndex(clusterProbs, random);
//            int startIndex = 0, endIndex = numOfPointsInCluster[0];
//
//            for (int j = 1; j < clusterIndex; j += 1) {
//                startIndex += numOfPointsInCluster[j - 1];
//                endIndex += numOfPointsInCluster[j];
//            }
//
//            if (startIndex >= points.length || endIndex - startIndex <= 0) continue;
//            if (endIndex >= points.length) endIndex = points.length;
//
////            System.out.println("CLUSTER:" + clusterIndex + ", START: " + startIndex + ", END: " + endIndex);
//            int index = random.nextInt(endIndex - startIndex) + startIndex;
//
//            Point p = points[index];
//
//            double x = p.getX() + random.nextGaussian()/points.length*10.0;
//            double y = p.getY() + random.nextGaussian()/points.length*10.0;
//
////            System.out.println("POINT MOVE " + p.getId() + ": " + p.getX());
//            movePoint(p, x, y);
//        }
//    }

//    public void moveRandomPoints(final Point[] points,
//                                 final int minMove,
//                                 final int maxMove,
//                                 final double[] clusterProbs,
//                                 final double[] movingClusterProbs) {
//
//        Random random = new Random(System.currentTimeMillis());
//        int move = random.nextInt(maxMove - minMove) + minMove;
//
//        int[] numOfPointsInCluster = new int[clusterProbs.length];
//        for (int i = 0; i < clusterProbs.length; i += 1) {
//            numOfPointsInCluster[i] = (int) Math.ceil(points.length * clusterProbs[i]);
//        }
//
//        for (int i = 0; i < move; i++) {
//            int clusterIndex = getRandomClusterIndex(movingClusterProbs);
//            int startIndex = 0, endIndex = numOfPointsInCluster[0];
//
//            for (int j = 1; j < clusterIndex; j += 1) {
//                startIndex += numOfPointsInCluster[j - 1];
//                endIndex += numOfPointsInCluster[j];
//            }
//
//            if (endIndex - startIndex < 1) continue;
//
//            int index = random.nextInt(endIndex - startIndex) + startIndex;
//
//            Point p = points[index];
//
//            double x = p.getX() + random.nextGaussian()/points.length*10.0;
//            double y = p.getY() + random.nextGaussian()/points.length*10.0;
//
////            System.out.println("POINT MOVE " + p.getId() + ": " + p.getX());
//            movePoint(p, x, y);
//        }
//    }

    public int getRandomClusterIndex(final double[] clusterProbabilities, Random random) {
        double[] clusterCDF = Utilities.computeCDF(clusterProbabilities);
        double num = random.nextDouble();
        for (int i = 0; i < clusterCDF.length; i += 1) {
            if (num < clusterCDF[i])
                return i;
        }
        return clusterCDF.length - 1;
    }

    public void movePoint(final Point p, final double x, final double y) {
        kmbr.movePoint(p, x, y);
        if (writer != null)
            writer.write(EventType.MOVE, p.getId(), x, y);
    }

    public ClusterAssignment sampleByClusterIndex(final ClusterAssignment[] points, final int clusterIndex, final Random random) {
        List<ClusterAssignment> filteredList = filterClusterAssignments(points, clusterIndex);
        if (filteredList.size() == 0) return null;

        int randomIndex = random.nextInt(filteredList.size());
        return filteredList.get(randomIndex);
    }

    public List<ClusterAssignment> filterClusterAssignments(final ClusterAssignment[] points, final int clusterIndex) {
        return Arrays.stream(points).filter(p -> p.getClusterIndex() == clusterIndex).collect(Collectors.toList());
    }
}
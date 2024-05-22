package edu.hanyang.gridshift;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.PointManipulator;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.MBR;
import edu.hanyang.kmbr.utils.Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GridShiftApp {

    public static void main(String[] args) {
        new GridShiftApp().run();
    }

    private DatabaseInteractor db;
    private static final double GRID_SIZE = Double.parseDouble(System.getenv().getOrDefault("GRID_SIZE", "0.01"));
    public static final int NUM_OF_POINTS = Integer.parseInt(System.getenv().getOrDefault("NUM_OF_POINTS", "100000"));
    public static final int NUM_OF_GROUPS = Integer.parseInt(System.getenv().getOrDefault("NUM_OF_GROUPS", "30"));

    public GridShiftApp() {
        db = DatabaseInteractor.getInstance();
    }

    public void run() {
        System.out.println("NUM_OF_POINTS: " + NUM_OF_POINTS);
        runOnce();
    }

    public void runOnce() {
        double[] xLimits = {20.0, 50.0};
        double[] yLimits = {20.0, 50.0};
        double[] stdLimits = {0.5, 1.5};

        double[] pointGenerationClusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

        ClusterAssignment[] points = db.generateRandomPoints(
                NUM_OF_POINTS,
                NUM_OF_GROUPS,
                xLimits,
                yLimits,
                stdLimits,
                pointGenerationClusterProbs
        );
        GridShift gs;

//        try (FileWriter fout = new FileWriter("logs/gridshift/test.log");
//             BufferedWriter bout = new BufferedWriter(fout)) {
//
//            for (ClusterAssignment c: clusterAssignments) {
//                Point p = c.getPoint();
//                bout.write(String.format("point,%f,%f\n", p.getX(), p.getY()));
//            }
//            gs = new GridShift(clusterAssignments, 1, 0, 70, 0, 70);
//            for (Grid g: gs.getGrids().values()) {
//                bout.write(String.format("cluster1,%f,%f\n", g.getCentroid().getX(), g.getCentroid().getY()));
//            }
//            gs.run();
//
//            for (Grid g: gs.getGrids().values()) {
//                bout.write(String.format("cluster2,%f,%f\n", g.getCentroid().getX(), g.getCentroid().getY()));
//            }
//        } catch (IOException exc) {
//            exc.printStackTrace();
//        }

        double[] moveClusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

        try (FileWriter fout = new FileWriter(String.format("logs/gridShift/N%d_GS%f.csv", NUM_OF_POINTS, GRID_SIZE));
             BufferedWriter bout = new BufferedWriter(fout)) {

            for (int i = 1; i < 11; i += 1) {
//                int minMove = 500;
//                int maxMove = 501;
//                moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
                gs = new GridShift(points, 1, 0, 70, 0, 70);

                long startTime = System.currentTimeMillis();
                gs.run();
                double runtime = (System.currentTimeMillis() - startTime)/1000.0;
                System.out.println("(Grid shift) Runtime: " + runtime);
                bout.write(runtime + "\n");
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
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
        p.set(x, y);
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

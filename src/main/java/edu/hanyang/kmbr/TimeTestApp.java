package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;

import java.io.*;

public class TimeTestApp {

    public static void main(String[] args) {
        new TimeTestApp().run();
    }

    private DatabaseInteractor db;
    private KMBRInteractor kmbr;
    private PointManipulator pointManipulator;

    public TimeTestApp() {
        db = DatabaseInteractor.getInstance();
        kmbr = KMBRInteractor.getInstance();
        pointManipulator = new PointManipulator(db, kmbr, null);
    }

    public void run() {
        System.out.println("K: " + Config.K);
        System.out.println("NUM_OF_POINTS: " + Config.NUM_OF_POINTS);

        runOnce();
    }

    public void runOnce() {
        double[] xLimits = {20.0, 50.0};
        double[] yLimits = {20.0, 50.0};
        double[] stdLimits = {0.5, 1.5};

        double[] pointGenerationClusterProbs = db.pointFactory.getRandomClusterProbabilities(Config.NUM_OF_GROUPS);

        ClusterAssignment[] clusterAssignments = db.generateRandomPoints(
                Config.NUM_OF_POINTS,
                Config.NUM_OF_GROUPS,
                xLimits,
                yLimits,
                stdLimits,
                pointGenerationClusterProbs
        );

        Point[] points = new Point[clusterAssignments.length];

        for (int i = 0; i < points.length; i += 1) {
            points[i] = clusterAssignments[i].getPoint();
        }

        kmbr.createFinder(points);
        kmbr.updateCacheBits();

//        for (int i = 0; i < clusterProbabilities.length; i += 1) {
//            System.out.println(clusterProbabilities[i]);
//        }

//        if (Config.useCache) {
//            kmbr.updateCacheBits();
//        }
//        kmbr.find();

        long startTime = System.currentTimeMillis();
        MBR mbr = kmbr.find();
        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("(Without cache) Runtime: " + runtime + ", MBR size: " + mbr.size());

        String filename = "K_" + Config.K + "_NUM_OF_POINTS_" + Config.NUM_OF_POINTS + ".log";
        try (FileWriter fout = new FileWriter("logs/timeTest/" + filename);
             BufferedWriter bout = new BufferedWriter(fout)) {

            bout.write( 0 + "," + runtime + "\n");

            double[] moveClusterProbs = db.pointFactory.getRandomClusterProbabilities(Config.NUM_OF_GROUPS);

            for (int t = 1; t < 101; t += 1) {
                if (t == 50)
                    moveClusterProbs = db.pointFactory.getRandomClusterProbabilities(Config.NUM_OF_GROUPS);

                int minMove = 50;
                int maxMove = 51;
                pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
                kmbr.updateCacheBits();

                startTime = System.currentTimeMillis();
                kmbr.find();
                runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                System.out.println("(With cache at time " + t + ") Runtime: " + runtime + ", MBR size: " + mbr.size());

                bout.write( t + "," + runtime + "\n");
            }
        } catch (IOException ignored) {}
    }
}

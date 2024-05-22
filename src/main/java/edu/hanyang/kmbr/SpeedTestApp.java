package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.DataWriter;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.MBR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SpeedTestApp {

    public static void main(String[] args) {
        new SpeedTestApp().run();
    }

    private DatabaseInteractor db;
    private KMBRInteractor kmbr;
    private PointManipulator pointManipulator;

    public SpeedTestApp() {
        db = DatabaseInteractor.getInstance();
        kmbr = KMBRInteractor.getInstance();
        pointManipulator = new PointManipulator(db, kmbr, null);
    }

    public void run() {
        System.out.println("K: " + Config.K);
        System.out.println("NUM_OF_POINTS: " + Config.NUM_OF_POINTS);

        for (int i = 0; i < 10; i += 1) {
            runOnce();
        }
    }

    public void runOnce() {
        double[] xLimits = {20.0, 50.0};
        double[] yLimits = {20.0, 50.0};
        double[] stdLimits = {0.5, 1.5};

        double[] pointGenerationClusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

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

//        for (int i = 0; i < clusterProbabilities.length; i += 1) {
//            System.out.println(clusterProbabilities[i]);
//        }

//        if (Config.useCache) {
//            kmbr.updateCacheBits();
//        }
//        kmbr.find();

        double[] moveClusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

        int minMove = 1000;
        int maxMove = 2000;
        pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
        kmbr.updateCacheBits();

//        kmbr.printCacheBits();
//        kmbr.printDirtyProbs();

        try (FileWriter fout = new FileWriter(String.format("logs/K%d_N%d_without_caching.csv", Config.K, Config.NUM_OF_POINTS));
             BufferedWriter bout = new BufferedWriter(fout)) {

            long startTime = System.currentTimeMillis();
            MBR mbr = kmbr.find();
            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
            bout.write("" + runtime);
            System.out.println("(Without cache) Runtime: " + runtime + ", MBR size: " + mbr.size());

        } catch (IOException exc) {
            exc.printStackTrace();
        }

        minMove = 50;
        maxMove = 100;
        pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
        kmbr.updateCacheBits();

        try (FileWriter fout = new FileWriter(String.format("logs/K%d_N%d_with_caching.csv", Config.K, Config.NUM_OF_POINTS));
             BufferedWriter bout = new BufferedWriter(fout)) {
            long startTime = System.currentTimeMillis();
            MBR mbr = kmbr.find();
            double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
            bout.write("" + runtime);
            System.out.println("(With cache) Runtime: " + runtime + ", MBR size: " + mbr.size());
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

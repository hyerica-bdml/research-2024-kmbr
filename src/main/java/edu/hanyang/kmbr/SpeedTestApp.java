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

        for (int i = 0; i < Config.NUM_OF_ITERATIONS; i += 1) {
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
        kmbr.updateCacheBits();

        double[] moveClusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

        try (FileWriter fout = new FileWriter("logs/ours/without_caching.csv", true);
             BufferedWriter bout = new BufferedWriter(fout)) {

            long startTime = System.currentTimeMillis();
            // This computation is "without cache" since it is the first run.
            MBR mbr = kmbr.find();
            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
            bout.write(Config.K + "," + Config.NUM_OF_POINTS + "," + runtime + "\n");
            System.out.println("(Without cache) Runtime: " + runtime + ", MBR size: " + mbr.size());

        } catch (IOException exc) {
            exc.printStackTrace();
        }

       int minMove = 50;
       int maxMove = 100;
       pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
       kmbr.updateCacheBits();

       try (FileWriter fout = new FileWriter("logs/ours/with_caching.csv", true);
            BufferedWriter bout = new BufferedWriter(fout)) {
           long startTime = System.currentTimeMillis();
           MBR mbr = kmbr.find();
           double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
           bout.write(Config.K + "," + Config.NUM_OF_POINTS + "," + runtime + "\n");
           System.out.println("(With cache) Runtime: " + runtime + ", MBR size: " + mbr.size());
       } catch (IOException exc) {
           exc.printStackTrace();
       }
    }
}

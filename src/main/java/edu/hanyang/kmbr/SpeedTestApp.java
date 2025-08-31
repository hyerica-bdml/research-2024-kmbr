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
        double[] xLimits = {10.0, 100.0};
        double[] yLimits = {10.0, 100.0};
        double[] stdLimits = {1.5, 4.5};

//        double[] clusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS, Config.NUM_OF_GROUPS / 10, 0.2);
        double[] clusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

        ClusterAssignment[] clusterAssignments = db.generateRandomPoints(
                Config.NUM_OF_POINTS,
                Config.NUM_OF_GROUPS,
                xLimits,
                yLimits,
                stdLimits,
                clusterProbs
        );

        Point[] points = new Point[clusterAssignments.length];

        for (int i = 0; i < points.length; i += 1) {
            points[i] = clusterAssignments[i].getPoint();
        }

        kmbr.createFinder(points);
        kmbr.updateCacheBits();

        String logFilePath = System.getenv().getOrDefault("LOG_FILE", "logs/ours/log.txt");
        int numDynamicIterations = Integer.parseInt(System.getenv().getOrDefault("NUM_DYNAMIC_ITERATIONS", "30"));
        int minMove = Integer.parseInt(System.getenv().getOrDefault("MIN_MOVE", "50"));
        int maxMove = Integer.parseInt(System.getenv().getOrDefault("MAX_MOVE", "100"));
        boolean runOnDynamic = Boolean.parseBoolean(System.getenv().getOrDefault("RUN_ON_DYNAMIC", "true"));

        if (!runOnDynamic) numDynamicIterations = 1;

        double[] moveProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS, Config.NUM_OF_GROUPS / 10, 0.2);

        try (FileWriter fout = new FileWriter(logFilePath, true);
             BufferedWriter bout = new BufferedWriter(fout)) {

            for (int i = 0; i < numDynamicIterations; i += 1) {
                long startTime = System.currentTimeMillis();
                MBR mbr = kmbr.find();
                double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                bout.write(i + "," + Config.K + "," + Config.NUM_OF_POINTS + "," + runtime + "\n");

                if (i == 0)
                    System.out.print("(Without caching) ");
                else
                    System.out.print("(With caching) ");
                System.out.println("Runtime: " + runtime + ", MBR size: " + mbr.size());

                pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveProbs);
                kmbr.updateCacheBits();
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

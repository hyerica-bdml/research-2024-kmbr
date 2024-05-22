package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.DataWriter;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.MBR;

import java.util.Arrays;
import java.util.List;

public class SimulationApp {

    public static void main(String[] args) {
        new SimulationApp().run();
    }

    private DatabaseInteractor db;
    private KMBRInteractor kmbr;
    private DataWriter writer;
    private PointManipulator pointManipulator;

    public SimulationApp() {
        db = DatabaseInteractor.getInstance();
        kmbr = KMBRInteractor.getInstance();
        writer = new DataWriter(Config.kmbrSimulationFile);
        pointManipulator = new PointManipulator(db, kmbr, writer);
    }

    public void run() {
        double[] xLimits = {20.0, 50.0};
        double[] yLimits = {20.0, 50.0};
        double[] stdLimits = {1.5, 2.5};
//        double[] stdLimits = {2.0, 2.5};

        double[] pointGenerationClusterProbs = db.pointFactory.getRandomClusterProbabilities(Config.NUM_OF_GROUPS);

        long startTime = System.currentTimeMillis();
        ClusterAssignment[] clusterAssignments = db.generateRandomPoints(
                Config.NUM_OF_POINTS,
                Config.NUM_OF_GROUPS,
                xLimits,
                yLimits,
                stdLimits,
                pointGenerationClusterProbs
        );
        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("Data creation time: " + runtime);

        Point[] points = new Point[clusterAssignments.length];

        for (int i = 0; i < points.length; i += 1) {
            points[i] = clusterAssignments[i].getPoint();
        }

        startTime = System.currentTimeMillis();
        kmbr.createFinder(points);
        runtime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("Tree construction time: " + runtime);

        for (Point p: points) {
            writer.write(EventType.CREATE, p.getId(), p.getX(), p.getY());
        }

//        for (int i = 0; i < clusterProbabilities.length; i += 1) {
//            System.out.println(clusterProbabilities[i]);
//        }

        if (Config.useCache) {
            System.out.println("Update cache bit...");
            kmbr.updateCacheBits();
        }

        MBR mbr = kmbr.find();

        startTime = System.currentTimeMillis();
        System.out.println("MBR size: " + mbr.size());
        runtime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("MBR computation time: " + runtime);
        writer.writeMBR(mbr.getPointIds());

        double[] moveClusterProbs = db.pointFactory.getRandomClusterProbabilities(Config.NUM_OF_GROUPS);

        for (int i = 0; i < 30; i += 1) {
//        for (int i = 0; i < 1; i += 1) {
            System.out.println("Random move iteration: " + (i + 1));
            startTime = System.currentTimeMillis();
            pointManipulator.moveRandomPoints(clusterAssignments, 50, 100, moveClusterProbs);
//            pointManipulator.moveRandomPoints(points, 100, 200, clusterProbs);
            runtime = (System.currentTimeMillis() - startTime)/1000.0;
            System.out.println("Data moving time: " + runtime);

            if (Config.useCache) {
                System.out.println("Update cache bit...");
                kmbr.updateCacheBits();
            }
            System.out.println("Computing MBR...");
            startTime = System.currentTimeMillis();
            mbr = kmbr.find();
            System.out.println("MBR size: " + mbr.size());
            runtime = (System.currentTimeMillis() - startTime)/1000.0;
            System.out.println("MBR computation time: " + runtime);
            writer.writeMBR(mbr.getPointIds());
        }

        close();
    }

    public void close() {
        writer.close();
    }
}

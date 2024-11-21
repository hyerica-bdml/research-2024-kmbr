package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class SpeedTest {

    @Test
    public void speedTest() {
        DatabaseInteractor db = DatabaseInteractor.getInstance();
        KMBRInteractor kmbr = KMBRInteractor.getInstance();
        PointManipulator pointManipulator = new PointManipulator(db, kmbr, null);

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

        double[] moveClusterProbs = db.getRandomClusterProbs(Config.NUM_OF_GROUPS);

        int minMove = 1000;
        int maxMove = 2000;
        pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
        kmbr.updateCacheBits();

//        kmbr.printCacheBits();
//        kmbr.printDirtyProbs();

        long startTime = System.currentTimeMillis();
        MBR mbr = kmbr.find();
        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("(Without cache) Runtime: " + runtime + ", MBR size: " + mbr.size());

        minMove = 50;
        maxMove = 100;
        pointManipulator.moveRandomPoints(clusterAssignments, minMove, maxMove, moveClusterProbs);
        kmbr.updateCacheBits();

        startTime = System.currentTimeMillis();
        mbr = kmbr.find();
        runtime = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.println("(With cache) Runtime: " + runtime + ", MBR size: " + mbr.size());
    }
}

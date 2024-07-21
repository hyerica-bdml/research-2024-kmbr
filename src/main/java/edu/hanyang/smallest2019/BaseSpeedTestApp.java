package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class BaseSpeedTestApp {

    public static void main(String[] args) {
        new BaseSpeedTestApp().run();
    }

    private DatabaseInteractor db;

    public BaseSpeedTestApp() {
        db = DatabaseInteractor.getInstance();
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

        try (FileWriter fout = new FileWriter(String.format("logs/related_works/smallest2019/base/K%d_N%d.csv", Config.K, Config.NUM_OF_POINTS), true);
             BufferedWriter bout = new BufferedWriter(fout)) {

            long startTime = System.currentTimeMillis();
            Smallest2019Base method = new Smallest2019Base(Arrays.asList(clusterAssignments));
            SmallestMBRResult mbr = method.find();
            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
            bout.write(Config.K + "," + Config.NUM_OF_POINTS + "," + runtime + "\n");
            System.out.println("Runtime: " + runtime + ", MBR size: " + mbr.size());

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

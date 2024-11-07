package edu.hanyang.voronoi1991;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SpeedTestApp {

    public static void main(String[] args) {
        new SpeedTestApp().run();
    }

    private DatabaseInteractor db;

    public SpeedTestApp() {
        db = DatabaseInteractor.getInstance();
    }

    public void run() {
        System.out.println("K: " + Config.K);
        System.out.println("NUM_OF_POINTS: " + Config.NUM_OF_POINTS);

        for (int i = 0; i < 1; i += 1) {
            runOnce(i);
        }
    }

    public void runOnce(int execIndex) {
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
        List<ClusterAssignment> clusterAssignmentsList = Arrays.asList(clusterAssignments);

        try (FileWriter fout = new FileWriter(String.format("logs/related_works/voronoi1991/K%d_N%d.csv", Config.K, Config.NUM_OF_POINTS), true);
             BufferedWriter bout = new BufferedWriter(fout)) {

            long startTime = System.currentTimeMillis();
            Voronoi1991 testApp = new Voronoi1991(clusterAssignmentsList, Config.K);
            double mbrSize = testApp.find().size();

            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
            bout.write(Config.K + "," + Config.NUM_OF_POINTS + "," + runtime + "\n");
            System.out.println("Runtime: " + runtime + ", MBR size: " + mbrSize);

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

package edu.hanyang.voronoi1991;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.PointManipulator;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;
import edu.hanyang.kmbr.utils.Utilities;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Voronoi1991Test {

    protected DatabaseInteractor db;
    protected KMBRInteractor kmbr;
    protected PointManipulator pointManipulator;
    protected int TRIALS = 10;

    @Test
    public void correctnessTest() {
        for (int i = 0; i < TRIALS; i += 1) {
            runOnce();
        }
    }

    public void runOnce() {
        double[] xLimits = {10.0, 5000.0};
        double[] yLimits = {10.0, 5000.0};
        double[] stdLimits = {50, 150};

        db = DatabaseInteractor.getInstance();
        kmbr = KMBRInteractor.getInstance();
        pointManipulator = new PointManipulator(db, kmbr, null);
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
        double voronoiResult = runVoronoi1991(clusterAssignmentsList, Config.K);
        double bruteforceResult = runBruteforceSolution(clusterAssignmentsList, Config.K);

        Assert.assertEquals(voronoiResult, bruteforceResult, 0.001);
    }

    public double runVoronoi1991(final List<ClusterAssignment> clusterAssignments, final int k) {
        Voronoi1991 testApp = new Voronoi1991(clusterAssignments, k);
        return testApp.find().size();
    }

    public double runBruteforceSolution(final List<ClusterAssignment> clusterAssignments, final int k) {
        Point[] xSortedPoints = new Point[clusterAssignments.size()];
        Point[] ySortedPoints = new Point[clusterAssignments.size()];

        for (int i = 0; i < clusterAssignments.size(); i += 1) {
            xSortedPoints[i] = clusterAssignments.get(i).getPoint();
            ySortedPoints[i] = clusterAssignments.get(i).getPoint();
        }

        Arrays.sort(xSortedPoints, Comparator.comparingDouble(Point::getX));
        Arrays.sort(ySortedPoints, Comparator.comparingDouble(Point::getY));

        MBR mbr = Utilities.computeMBR(xSortedPoints, ySortedPoints);

        if (mbr == null) return -1;
        else return mbr.size();
    }
}

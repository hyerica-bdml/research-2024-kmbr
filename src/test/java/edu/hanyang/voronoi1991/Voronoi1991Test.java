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

import java.util.*;


public class Voronoi1991Test {

    protected int TRIALS = 1;

    @Test
    public void correctnessTest() {
        for (int i = 0; i < TRIALS; i += 1) {
            runOnce();
        }
    }

    public void runOnce() {
        double[] xLimits = {10.0, 100.0};
        double[] yLimits = {10.0, 100.0};
        double[] stdLimits = {1, 5};

        DatabaseInteractor db = DatabaseInteractor.getInstance();
        double[] pointGenerationClusterProbs = db.getRandomClusterProbs(1);

        ClusterAssignment[] clusterAssignments = db.generateRandomPoints(
                300,
                1,
                xLimits,
                yLimits,
                stdLimits,
                pointGenerationClusterProbs
        );

        List<ClusterAssignment> clusterAssignmentsList = Arrays.asList(clusterAssignments);
        double voronoiResult = runVoronoi1991(clusterAssignmentsList, 5);
        double bruteforceResult = runBruteforceSolution(clusterAssignmentsList, 5);

        Assert.assertEquals(voronoiResult, bruteforceResult, 0.001);
    }

//    public void runOnce() {
////        Random random = new Random(40);
//        Random random = new Random(102);
//
//        List<ClusterAssignment> clusterAssignments = new LinkedList<>();
//        for (int i = 0; i < 500; i += 1) {
////            points.add(new ClusterAssignment(i, new Point(i, Math.pow(2, i), i+1)));
//            clusterAssignments.add(new ClusterAssignment(i, new Point(i, 10*random.nextDouble(), 10*random.nextDouble())));
//        }
//
//        double voronoiResult = runVoronoi1991(clusterAssignments, 5);
//        double bruteforceResult = runBruteforceSolution(clusterAssignments, 5);
//
//        Assert.assertEquals(voronoiResult, bruteforceResult, 0.001);
//    }

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

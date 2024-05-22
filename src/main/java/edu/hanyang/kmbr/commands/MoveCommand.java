package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRApp;
import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.Utilities;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MoveCommand implements Command {

    private final int minMove, maxMove, numOfClusters;
    private KMBRApp app;
    private final KMBRInteractor kmbr;
    private final DatabaseInteractor db;

    public MoveCommand(final KMBRApp app,
                       final KMBRInteractor kmbr,
                       final DatabaseInteractor db,
                       final int minMove,
                       final int maxMove,
                       final int numOfClusters) {

        this.app = app;
        this.kmbr = kmbr;
        this.db = db;
        this.minMove = minMove;
        this.maxMove = maxMove;
        this.numOfClusters = numOfClusters;
    }

    @Override
    public void execute() {
        moveRandomPoints();
    }

    public void moveRandomPoints() {

        Point[] points = db.getPoints();
        double[] clusterProbs = db.getRandomClusterProbs(this.numOfClusters);

        Random random = new Random(System.currentTimeMillis());
        int move = random.nextInt(maxMove - minMove) + minMove;

        for (int i = 0; i < move; i++) {
            int clusterIndex = getRandomClusterIndex(clusterProbs, random);
            Point p = sampleByClusterIndex(clusterIndex, random).getPoint();

            double x = p.getX() + random.nextGaussian() / points.length * 10.0;
            double y = p.getY() + random.nextGaussian() / points.length * 10.0;

//            System.out.println("POINT MOVE " + p.getId() + ": " + p.getX());
            movePoint(p, x, y);
        }
    }

    public int getRandomClusterIndex(final double[] clusterProbabilities, Random random) {
        double[] clusterCDF = Utilities.computeCDF(clusterProbabilities);
        double num = random.nextDouble();
        for (int i = 0; i < clusterCDF.length; i += 1) {
            if (num < clusterCDF[i])
                return i;
        }
        return clusterCDF.length - 1;
    }

    public void movePoint(final Point p, final double x, final double y) {
        kmbr.movePoint(p, x, y);
    }

    public ClusterAssignment sampleByClusterIndex(final int clusterIndex, final Random random) {
        List<ClusterAssignment> filteredList = filterClusterAssignments(clusterIndex);
        int randomIndex = random.nextInt(filteredList.size());
        return filteredList.get(randomIndex);
    }

    public List<ClusterAssignment> filterClusterAssignments(final int clusterIndex) {
        return app.getPoints().stream().filter(p -> p.getClusterIndex() == clusterIndex).collect(Collectors.toList());
    }
}

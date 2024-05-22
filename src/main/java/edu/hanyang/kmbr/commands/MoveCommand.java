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

    private final int minMove, maxMove;
    private final KMBRInteractor kmbr;
    private final DatabaseInteractor db;

    public MoveCommand(final KMBRInteractor kmbr,
                       final DatabaseInteractor db,
                       final int minMove,
                       final int maxMove) {

        this.kmbr = kmbr;
        this.db = db;
        this.minMove = minMove;
        this.maxMove = maxMove;
    }

    @Override
    public void execute() {
        moveRandomPoints();
    }

    public void moveRandomPoints() {

        ClusterAssignment[] points = db.getPoints();
        double[] clusterProbs = db.getRandomClusterProbs(db.getClusterIndices().size());

        Random random = new Random(System.currentTimeMillis());
        int move = random.nextInt(maxMove - minMove) + minMove;

        for (int i = 0; i < move; i++) {
            int clusterIndex = getRandomClusterIndex(clusterProbs, random);
            ClusterAssignment p = sampleByClusterIndex(clusterIndex, random);

            double x = p.getPoint().getX() + random.nextGaussian() / points.length * 10.0;
            double y = p.getPoint().getY() + random.nextGaussian() / points.length * 10.0;

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

    public void movePoint(final ClusterAssignment p, final double x, final double y) {
        kmbr.movePoint(p.getPoint(), x, y);
    }

    public ClusterAssignment sampleByClusterIndex(final int clusterIndex, final Random random) {
        ClusterAssignment[] filteredList = db.getPointsByClusterIndex(clusterIndex);
        int randomIndex = random.nextInt(filteredList.length);
        return filteredList[randomIndex];
    }
}

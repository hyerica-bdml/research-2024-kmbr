package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRApp;
import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

public class GenerateRandomPointsCommand implements Command {

    private final KMBRInteractor kmbr;
    private final DatabaseInteractor db;

    private final int numOfPoints, numOfClusters;
    private final double[] xLimits, yLimits, stdLimits;
    private final boolean clear;

    public GenerateRandomPointsCommand(final KMBRInteractor kmbr,
                                       final DatabaseInteractor db,
                                       final int numOfPoints,
                                       final int numOfClusters,
                                       final double[] xLimits,
                                       final double[] yLimits,
                                       final double[] stdLimits,
                                       final boolean clear) {
        this.db = db;
        this.kmbr = kmbr;

        this.numOfPoints = numOfPoints;
        this.numOfClusters = numOfClusters;
        this.xLimits = xLimits;
        this.yLimits = yLimits;
        this.stdLimits = stdLimits;
        this.clear = clear;
    }

    @Override
    public void execute() {
        ClusterAssignment[] assignments = db.generateRandomPoints(numOfPoints, numOfClusters, xLimits, yLimits, stdLimits);
        Point[] points = new Point[assignments.length];
        for (int i = 0; i < assignments.length; i += 1) {
            points[i] = assignments[i].getPoint();
        }

        if (clear) {
            kmbr.createFinder(points);
        }
        else {
            for (Point p : points)
                kmbr.addPoint(p);
        }
    }
}

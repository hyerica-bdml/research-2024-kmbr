package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRApp;
import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

public class GenerateRandomPointsCommand implements Command {

    private final KMBRApp app;
    private final DatabaseInteractor db;
    private final KMBRInteractor kmbr;

    private final int numOfPoints, numOfClusters;
    private double[] xLimits, yLimits, stdLimits;
    private final boolean clear;

    public GenerateRandomPointsCommand(final KMBRApp app,
                                       final DatabaseInteractor db,
                                       final KMBRInteractor kmbr,
                                       final int numOfPoints,
                                       final int numOfClusters,
                                       final double[] xLimits,
                                       final double[] yLimits,
                                       final double[] stdLimits,
                                       final boolean clear) {
        this.app = app;
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
        ClusterAssignment[] clusterAssignments = db.generateRandomPoints(numOfPoints, numOfClusters, xLimits, yLimits, stdLimits);
        app.addClusterAssignments(clusterAssignments);
        Point[] points = new Point[clusterAssignments.length];

        for (int i = 0; i < points.length; i += 1) {
            points[i] = clusterAssignments[i].getPoint();
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

package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRApp;
import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

public class AddRandomPointsCommand implements Command {

    private final KMBRInteractor kmbr;
    private final DatabaseInteractor db;
    private final int numOfPoints;

    public AddRandomPointsCommand(final KMBRInteractor kmbr,
                                  final DatabaseInteractor db,
                                  final int numOfPoints) {
        this.db = db;
        this.kmbr = kmbr;
        this.numOfPoints = numOfPoints;
    }

    @Override
    public void execute() {
        ClusterAssignment[] assignments = db.generateRandomPoints(numOfPoints, db.getClusterMean(), db.getClusterStd());
        Point[] points = new Point[assignments.length];
        for (int i = 0; i < assignments.length; i += 1) {
            points[i] = assignments[i].getPoint();
        }

        for (Point p : points)
            kmbr.addPoint(p);
    }
}

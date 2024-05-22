package edu.hanyang.kmbr.domain;

public class PointUtilities {

    public static double computeDistance(final Point p,
                                                    final Coordinate mean,
                                                    final double std) {

        // mahalanobis distance
        double distX = (p.getX() - mean.getX()) / std;
        double distY = (p.getY() - mean.getY()) / std;

        return Math.sqrt(distX * distX + distY * distY);
    }

    private PointUtilities() {}
}

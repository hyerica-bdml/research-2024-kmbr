package edu.hanyang.voronoi1991;

import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public class VoronoiKVarianceResult {

    private final List<Coordinate> coordinates;

    public VoronoiKVarianceResult(final List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }

    public double computeVariance() {
        Coordinate centroid = getCentroid(coordinates);

        double variance = 0.0;
        for (Coordinate c: coordinates)
            variance += Math.pow(centroid.getX() - c.getX(), 2) + Math.pow(centroid.getY() - c.getY(), 2);

        variance /= coordinates.size();
        return variance;
    }

    private Coordinate getCentroid(List<Coordinate> coordinates) {
        double meanX = 0.0, meanY = 0.0;

        for (Coordinate c: coordinates) {
            meanX += c.getX();
            meanY += c.getY();
        }

        meanX /= coordinates.size();
        meanY /= coordinates.size();

        return new Coordinate(meanX, meanY);
    }
}

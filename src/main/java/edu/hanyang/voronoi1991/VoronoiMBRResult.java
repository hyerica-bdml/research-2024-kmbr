package edu.hanyang.voronoi1991;

import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public class VoronoiMBRResult {

    protected final List<Coordinate> coordinates;
    protected final Coordinate top, bottom, left, right;

    public VoronoiMBRResult(final List<Coordinate> coordinates) {
        this.coordinates = coordinates;

        Coordinate top = coordinates.get(0);
        Coordinate bottom = coordinates.get(0);
        Coordinate left = coordinates.get(0);
        Coordinate right = coordinates.get(0);

        for (int i = 1; i < coordinates.size(); i += 1) {
            Coordinate c = coordinates.get(i);
            if (c.x < left.x) left = c;
            if (c.x > right.x) right = c;
            if (c.y > top.y) top = c;
            if (c.y < bottom.y) bottom = c;
        }

        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public double size() {
        return 2*(top.y - bottom.y + right.x - left.x);
    }
}

package edu.hanyang.voronoi;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class VoronoiSet {

    protected final Coordinate[] voronoiSet;
    protected final Geometry voronoiCell;

    public VoronoiSet(final List<Coordinate> voronoiSet, final Geometry cell) {
        this.voronoiSet = new Coordinate[voronoiSet.size()];
        for (int i = 0; i < voronoiSet.size(); i += 1) {
            this.voronoiSet[i] = voronoiSet.get(i);
        }

        this.voronoiCell = cell;
    }

    public List<Coordinate> getVoronoiSet() {
        return Arrays.asList(voronoiSet);
    }

    public Coordinate[] getVoronoiSetAsArray() {
        return this.voronoiSet;
    }

    public Geometry getVoronoiCell() {
        return voronoiCell;
    }

    public int getOrder() {
        return this.voronoiSet.length;
    }

    public boolean isEqual(VoronoiSet other) {
        for (int i = 0; i < voronoiSet.length; i += 1) {
            boolean foundMatch = false;
            for (int j = 0; j < other.voronoiSet.length; j += 1) {
                if (voronoiSet[i] == other.voronoiSet[j]) {
                    foundMatch = true;
                    break;
                }
            }

            if (!foundMatch) return false;
        }

        return true;
    }

    public boolean isEqual(List<Coordinate> other) {
        if (voronoiSet.length != other.size()) return false;

//        System.out.println("Comparison:");
//        System.out.print("\t");
//        for (Coordinate c: voronoiSet)
//            System.out.print(c);
//        System.out.println();
//        System.out.print("\t");
//        for (Coordinate c: other)
//            System.out.print(c);
//        System.out.println();

        for (int i = 0; i < voronoiSet.length; i += 1) {
            boolean foundMatch = false;

            for (int j = 0; j < other.size(); j += 1) {
                if (voronoiSet[i].equals(other.get(j))) {
                    foundMatch = true;
                    break;
                }
            }

//            System.out.println(foundMatch);

            if (!foundMatch) return false;
        }

        return true;
    }

    public VoronoiSet union(final VoronoiSet other) {
        List<Coordinate> coordinates = new LinkedList<>(Arrays.asList(this.voronoiSet));

        for (int i = 0; i < other.voronoiSet.length; i += 1)
            if (!coordinates.contains(other.voronoiSet[i]))
                coordinates.add(other.voronoiSet[i]);

        Geometry cell;
        if (this.voronoiCell == null)
            cell = other.voronoiCell;
        else if (other.voronoiCell == null)
            cell = this.voronoiCell;
        else
            cell = this.voronoiCell.union(other.voronoiCell);

        return new VoronoiSet(coordinates, cell);
    }

//    protected void refinePolygon() {
//        Polygon polygon = (Polygon) voronoiCell;
//        Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
//        double THRESHOLD = 0.0000001;
//
//        List<Coordinate> newCoords = new LinkedList<>();
//
//        for (int i = 0; i < coords.length - 2; i += 1) {
//            double x = coords[i].x;
//            double y = coords[i].y;
//
//            double x2 = coords[i + 2].x;
//            double y2 = coords[i + 2].y;
//
//            if (x2 - x < THRESHOLD && y2 - y < THRESHOLD)
//        }
//    }
}

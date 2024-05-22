package edu.hanyang.gridshift;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

import java.util.*;

public class GridShift {

    private ClusterAssignment[] assignments;
    private double gridSize;
    private Map<KeyTuple, Grid> grids;


    public GridShift(final ClusterAssignment[] assignments,
                     final double gridSize,
                     final double x_lower,
                     final double x_upper,
                     final double y_lower,
                     final double y_upper) {

        this.assignments = assignments;
        this.gridSize = gridSize;

        grids = new HashMap<>();

        for (double x = x_lower; x < x_upper; x += gridSize) {
            for (double y = y_lower; y < y_upper; y += gridSize) {
                double centroidX = x + gridSize/2;
                double centroidY = y + gridSize/2;

                Grid grid = new Grid(gridSize, centroidX, centroidY);
                int indexX = (int) (centroidX/gridSize);
                int indexY = (int) (centroidY/gridSize);

                updateMembership(grid);

                if (grid.getNumOfPoints() > 0) {
                    KeyTuple key = new KeyTuple(indexX, indexY);
                    grids.put(key, grid);
                }
            }
        }
    }

    public void run() {
        while (true) {
            updateCentroids();
//            break;

            if (stopCriterion()) break;
        }
    }

    public void updateCentroids() {
        Map<KeyTuple, Grid> newGrids = new HashMap<>();

        for (KeyTuple key: grids.keySet()) {
            Grid grid = grids.get(key);
            if (!grid.isActive()) continue;

//            System.out.println("======");

            double numeratorX = 0.0, numeratorY = 0.0;
            int denominator = 0;

            for (int y = key.y - 1; y < key.y + 2; y += 1) {
                for (int x = key.x - 1; x < key.x + 2; x += 1) {
                    KeyTuple target = new KeyTuple(x, y);
                    Grid neighbor = grids.getOrDefault(target, null);

                    if (neighbor != null) {
//                        System.out.println(neighbor.getCentroid() + ", " + neighbor.getNumOfPoints());
                        numeratorX += neighbor.getNumOfPoints() * neighbor.getCentroid().getX();
                        numeratorY += neighbor.getNumOfPoints() * neighbor.getCentroid().getY();
                        denominator += neighbor.getNumOfPoints();
                    }
                }
            }
//            System.out.println("======");


            double newX = numeratorX / denominator;
            double newY = numeratorY / denominator;

            Grid newGrid = new Grid(gridSize, newX, newY);
            newGrid.getPoints().addAll(grid.getPoints());

            int indexX = (int)(newX/gridSize);
            int indexY = (int)(newY/gridSize);

            KeyTuple newKey = new KeyTuple(indexX, indexY);
            Grid other = newGrids.getOrDefault(newKey, null);

            if (other != null) {
                newX = (grid.getNumOfPoints()*grid.getCentroid().getX() + other.getNumOfPoints()*other.getCentroid().getX())/(grid.getNumOfPoints() + other.getNumOfPoints());
                newY = (grid.getNumOfPoints()*grid.getCentroid().getY() + other.getNumOfPoints()*other.getCentroid().getY())/(grid.getNumOfPoints() + other.getNumOfPoints());

                newGrid.getCentroid().set(newX, newY);
                newGrid.getPoints().addAll(other.getPoints());
            }
            newGrids.put(newKey, newGrid);
        }

        grids = newGrids;
    }

    public void updateMembership() {
        for (KeyTuple key: grids.keySet()) {
            Grid grid = grids.get(key);
            if (!grid.isValid()) continue;

            double x = grid.getCentroid().getX();
            double y = grid.getCentroid().getY();

            grid.getPoints().clear();

            for (ClusterAssignment assignment: assignments) {
                Point p = assignment.getPoint();
//                System.out.println(p);

                if (p.getX() >= x - gridSize/2 && p.getX() <= x + gridSize/2 &&
                    p.getY() >= y - gridSize/2 && p.getY() <= y + gridSize/2) {

                    grid.getPoints().add(p);
                }
            }
        }
    }

    public void updateMembership(Grid grid) {
        Point[] points = new Point[assignments.length];
        for (int i = 0; i < assignments.length; i += 1) {
            points[i] = assignments[i].getPoint();
        }
        grid.update(points);
    }

    public boolean stopCriterion() {
        KeyTuple[] keys = new KeyTuple[grids.size()];
        grids.keySet().toArray(keys);

        for (KeyTuple key: keys) {
            Grid grid = grids.get(key);

            for (int y = key.y - 1; y < key.y + 2; y += 1) {
                for (int x = key.x - 1; x < key.x + 2; x += 1) {
                    KeyTuple target = new KeyTuple(x, y);
                    Grid neighbor = grids.getOrDefault(target, null);

                    if (neighbor != null && neighbor != grid) return false;
                }
            }
        }
        return true;
    }

    public Map<KeyTuple, Grid> getGrids() {
        return grids;
    }

    public static class KeyTuple  {
        public int x, y;

        public KeyTuple(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof KeyTuple) {
                KeyTuple key = (KeyTuple) other;

                return key.x == x && key.y == y;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Integer.valueOf(x + y).hashCode();
        }

        @Override
        public String toString() {
            return "[" + x + ", " + y + "]";
        }

    }
}

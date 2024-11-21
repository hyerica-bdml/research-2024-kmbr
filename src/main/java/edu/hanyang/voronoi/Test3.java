package edu.hanyang.voronoi;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test3 extends JPanel {
    private final List<Geometry> highOrderVoronoiCells;
    private final Point testPoint;
    private Geometry containingCell;

    public Test3(List<Geometry> highOrderVoronoiCells, Point testPoint) {
        this.highOrderVoronoiCells = highOrderVoronoiCells;
        this.testPoint = testPoint;
        this.containingCell = findContainingCell(testPoint);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 고차 Voronoi 셀 그리기
        g2d.setColor(Color.BLACK);
        for (Geometry cell : highOrderVoronoiCells) {
            drawGeometry(g2d, cell);
        }

        // 특정 점이 포함된 셀을 강조
        if (containingCell != null) {
            g2d.setColor(Color.RED);
            drawGeometry(g2d, containingCell);
        }

        // 특정 점 그리기
        g2d.setColor(Color.BLUE);
        g2d.fillOval((int) testPoint.getX() - 3, (int) testPoint.getY() - 3, 6, 6);
    }

    private void drawGeometry(Graphics2D g2d, Geometry geometry) {
        if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon) geometry;
            Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
            int[] xPoints = new int[coords.length];
            int[] yPoints = new int[coords.length];
            for (int i = 0; i < coords.length; i++) {
                xPoints[i] = (int) coords[i].x;
                yPoints[i] = (int) coords[i].y;
            }
            g2d.drawPolygon(xPoints, yPoints, coords.length);
        }
    }

    private Geometry findContainingCell(Point point) {
        for (Geometry cell : highOrderVoronoiCells) {
            if (cell.contains(point)) {
                return cell;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        // 좌표계 정의
        GeometryFactory geometryFactory = new GeometryFactory();

        // 점 목록 생성
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(100, 100));
        coordinates.add(new Coordinate(200, 200));
        coordinates.add(new Coordinate(300, 100));
        coordinates.add(new Coordinate(400, 300));
        coordinates.add(new Coordinate(500, 500));

        // 고차 Voronoi 셀 생성
        List<Geometry> highOrderVoronoiCells = generateHighOrderVoronoiCells(geometryFactory, coordinates);

        // 특정 점 정의
        Coordinate testPointCoord = new Coordinate(150, 150);
        Point testPoint = geometryFactory.createPoint(testPointCoord);

        // JFrame 설정
        JFrame frame = new JFrame("High-Order Voronoi Diagram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(new Test3(highOrderVoronoiCells, testPoint));
        frame.setVisible(true);
    }

    private static List<Geometry> generateHighOrderVoronoiCells(GeometryFactory geometryFactory, List<Coordinate> coordinates) {
        List<Geometry> highOrderVoronoiCells = new ArrayList<>();
        Set<Set<Coordinate>> pointPairs = generateCombinations(coordinates, 2);

        for (Set<Coordinate> pair : pointPairs) {
            Coordinate[] pairArray = pair.toArray(new Coordinate[0]);
            MultiPoint points = geometryFactory.createMultiPointFromCoords(pairArray);
            VoronoiDiagramBuilder voronoiDiagramBuilder = new VoronoiDiagramBuilder();
            voronoiDiagramBuilder.setSites(points);
            Geometry diagram = voronoiDiagramBuilder.getDiagram(geometryFactory);

            for (int i = 0; i < diagram.getNumGeometries(); i++) {
                Geometry cell = diagram.getGeometryN(i);
                highOrderVoronoiCells.add(cell);
            }
        }

        return highOrderVoronoiCells;
    }

    private static Set<Set<Coordinate>> generateCombinations(List<Coordinate> coordinates, int k) {
        Set<Set<Coordinate>> result = new HashSet<>();
        int n = coordinates.size();
        int[] indices = new int[k];
        if (k <= n) {
            for (int i = 0; i < k; i++) {
                indices[i] = i;
            }
            result.add(createCombination(coordinates, indices));
            while (true) {
                int i;
                for (i = k - 1; i >= 0 && indices[i] == i + n - k; i--);
                if (i < 0) {
                    break;
                }
                indices[i]++;
                for (int j = i + 1; j < k; j++) {
                    indices[j] = indices[j - 1] + 1;
                }
                result.add(createCombination(coordinates, indices));
            }
        }
        return result;
    }

    private static Set<Coordinate> createCombination(List<Coordinate> coordinates, int[] indices) {
        Set<Coordinate> combination = new HashSet<>();
        for (int index : indices) {
            combination.add(coordinates.get(index));
        }
        return combination;
    }
}

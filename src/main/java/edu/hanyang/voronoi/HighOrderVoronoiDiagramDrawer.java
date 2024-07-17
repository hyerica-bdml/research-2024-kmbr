package edu.hanyang.voronoi;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class HighOrderVoronoiDiagramDrawer extends JPanel {
    private final HighOrderVoronoiDiagram voronoiDiagram;
    private final List<Point> points;

    public HighOrderVoronoiDiagramDrawer(HighOrderVoronoiDiagram voronoiDiagram) {
        this.voronoiDiagram = voronoiDiagram;
        points = new LinkedList<>();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Voronoi 셀 그리기
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < voronoiDiagram.getNumOfVoronoiSets(); i++) {
            VoronoiSet voronoiSet = voronoiDiagram.getVoronoiSet(i);
            Geometry cell = voronoiSet.getVoronoiCell();
            Coordinate[] coords = (Coordinate[]) cell.getUserData();
//            System.out.println("Voronoi Set:");
//            System.out.print("\t");
//            for (Coordinate c: coords)
//                System.out.print(c + " ");
//            System.out.println();
            drawGeometry(g2d, cell, i);
        }
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    private void drawGeometry(Graphics2D g2d, Geometry geometry, int index) {
        if (geometry instanceof MultiPolygon) {
            for (int g = 0; g < geometry.getNumGeometries(); g += 1) {
                Polygon polygon = (Polygon) geometry.getGeometryN(g);
                Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
                int[] xPoints = new int[coords.length];
                int[] yPoints = new int[coords.length];

                double meanX = 0.0, meanY = 0.0;
                for (int i = 0; i < coords.length; i++) {
                    xPoints[i] = (int) (coords[i].x + 0.00001) + 200;
                    yPoints[i] = (int) (coords[i].y + 0.00001) + 200;

                    meanX += xPoints[i];
                    meanY += yPoints[i];

                }

                meanX /= coords.length;
                meanY /= coords.length;

                g2d.setColor(new Color(100, 100, 20, 80));
                g2d.drawPolygon(xPoints, yPoints, coords.length);

                g2d.setColor(new Color(100, 100, 20, 50));
                g2d.fillPolygon(xPoints, yPoints, coords.length);
                g2d.drawString(String.valueOf(index), (int) meanX, (int) meanY);

                System.out.println(index + ", " + polygon);
            }

            Polygon convexHull = (Polygon) geometry.convexHull();
            Coordinate[] coords = convexHull.getExteriorRing().getCoordinates();
            int[] xPoints = new int[coords.length];
            int[] yPoints = new int[coords.length];

            double meanX = 0.0, meanY = 0.0;
            for (int i = 0; i < coords.length; i++) {
                xPoints[i] = (int) (coords[i].x + 0.00001) + 200;
                yPoints[i] = (int) (coords[i].y + 0.00001) + 200;

                meanX += xPoints[i];
                meanY += yPoints[i];

//                if (index == 6) {
//                    System.out.println("(" + xPoints[i] + ", " + yPoints[i] + "), (" + coords[i].x + ", " + coords[i].y + ")");
//                    g2d.drawRect((int) xPoints[i] - 2, (int) yPoints[i] - 2, 4, 4);
//                }
            }

            meanX /= coords.length;
            meanY /= coords.length;
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, coords.length);
        }
        else if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon) geometry;
            Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
            int[] xPoints = new int[coords.length];
            int[] yPoints = new int[coords.length];

            double meanX = 0.0, meanY = 0.0;
            for (int i = 0; i < coords.length; i++) {
                xPoints[i] = (int) (coords[i].x + 0.00001) + 200;
                yPoints[i] = (int) (coords[i].y + 0.00001) + 200;

                meanX += xPoints[i];
                meanY += yPoints[i];

//                if (index == 6) {
//                    System.out.println("(" + xPoints[i] + ", " + yPoints[i] + "), (" + coords[i].x + ", " + coords[i].y + ")");
//                    g2d.drawRect((int) xPoints[i] - 2, (int) yPoints[i] - 2, 4, 4);
//                }
            }

            meanX /= coords.length;
            meanY /= coords.length;

            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(xPoints, yPoints, coords.length);
//            g2d.drawString(String.valueOf(index), (int) meanX, (int) meanY);
        }

        for (Point p: points) {
            g2d.drawRect((int) p.getX() + 200 - 2, (int) p.getY() + 200 - 2, 4, 4);
        }
    }
}

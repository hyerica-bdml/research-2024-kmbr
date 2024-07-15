package edu.hanyang.voronoi;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import java.util.List;

public class VoronoiDiagramDrawer extends JPanel {
    private final Geometry voronoiDiagram;
    private final List<Point> points;

    public VoronoiDiagramDrawer(Geometry voronoiDiagram) {
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
        for (int i = 0; i < voronoiDiagram.getNumGeometries(); i++) {
            Geometry cell = voronoiDiagram.getGeometryN(i);

//            if (i == 0) {
//                cell = voronoiDiagram.getGeometryN(1).union(cell);
//            }
//            else if (i == 1) {
//                continue;
//            }
            drawGeometry(g2d, cell, i);
        }
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    private void drawGeometry(Graphics2D g2d, Geometry geometry, int index) {
        if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon) geometry;
            Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
            int[] xPoints = new int[coords.length];
            int[] yPoints = new int[coords.length];

            double meanX = 0.0, meanY = 0.0;
            for (int i = 0; i < coords.length; i++) {
                xPoints[i] = (int) coords[i].x + 200;
                yPoints[i] = (int) coords[i].y + 200;

                meanX += xPoints[i];
                meanY += yPoints[i];
            }

            meanX /= coords.length;
            meanY /= coords.length;

            g2d.drawPolygon(xPoints, yPoints, coords.length);
            g2d.drawString(String.valueOf(index), (int) meanX, (int) meanY);
        }

        for (Point p: points) {
            g2d.drawRect((int) p.getX() + 200 - 2, (int) p.getY() + 200 - 2, 4, 4);
        }
    }
}

package edu.hanyang.voronoi;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import javax.swing.*;
import java.awt.*;

public class TestBoard extends JPanel {

    protected final Geometry[] rootGeometries;

    public TestBoard(final Geometry[] rootGeometries) {
        this.rootGeometries = rootGeometries;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Voronoi 셀 그리기
        g2d.setColor(Color.BLACK);

        for (Geometry geom: rootGeometries)
            drawGeometry(g2d, geom);
    }

    public void drawGeometry(final Graphics2D g2d, final Geometry geometry) {
        if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon) geometry;
            Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
            int[] xPoints = new int[coords.length];
            int[] yPoints = new int[coords.length];

            double meanX = 0.0, meanY = 0.0;
            for (int i = 0; i < coords.length; i++) {
                xPoints[i] = (int) (coords[i].x);
                yPoints[i] = (int) (coords[i].y);

                meanX += xPoints[i];
                meanY += yPoints[i];

//                if (index == 6) {
//                    System.out.println("(" + xPoints[i] + ", " + yPoints[i] + "), (" + coords[i].x + ", " + coords[i].y + ")");
//                    g2d.drawRect((int) xPoints[i] - 2, (int) yPoints[i] - 2, 4, 4);
//                }
            }

            meanX /= coords.length;
            meanY /= coords.length;

            g2d.setColor(new Color(100, 100, 20, 50));
            g2d.fillPolygon(xPoints, yPoints, coords.length);
//            g2d.drawString(String.valueOf(index), (int) meanX, (int) meanY);
        }
    }
}

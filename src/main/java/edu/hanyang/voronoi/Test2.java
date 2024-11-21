package edu.hanyang.voronoi;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test2 {

    public static void main(String[] args) {
        // 좌표계 정의
        GeometryFactory geometryFactory = new GeometryFactory();

        // 점 목록 생성
        List<Coordinate> coordinates = new ArrayList<>();
//        coordinates.add(new Coordinate(10, 10));
//        coordinates.add(new Coordinate(20, 20));
//        coordinates.add(new Coordinate(30, 10));
//        coordinates.add(new Coordinate(40, 30));
//        coordinates.add(new Coordinate(50, 50));

        Random random = new Random(22);
        for (int i = 0; i < 30; i += 1) {
            coordinates.add(new Coordinate(random.nextDouble() * 500, random.nextDouble() * 500));
//            coordinates.add(new Coordinate(random.nextDouble() * 100, random.nextDouble() * 100));
        }

        HighOrderVoronoiDiagram highOrderVoronoiDiagram = new HighOrderVoronoiDiagram(coordinates, 1);

        // 특정 점이 어떤 Voronoi 셀에 포함되는지 확인
//        Coordinate testPointCoord = new Coordinate(-50, -50);
//        Point testPoint = geometryFactory.createPoint(testPointCoord);

//        Geometry containingCell = findContainingCell(diagram, testPoint);

//        if (containingCell != null) {
//            System.out.println("Point " + testPoint + " is contained in cell: " + containingCell);
//        } else {
//            System.out.println("Point " + testPoint + " is not contained in any Voronoi cell.");
//        }

//        int containingCellIndex = findContainingCell(highOrderVoronoiDiagram, testPoint);
//
//        if (containingCellIndex > -1) {
//            System.out.println("Point " + testPoint + " is contained in cell: " + containingCellIndex);
//        } else {
//            System.out.println("Point " + testPoint + " is not contained in any Voronoi cell.");
//        }

        HighOrderVoronoiDiagramDrawer drawer = new HighOrderVoronoiDiagramDrawer(highOrderVoronoiDiagram);
//        drawer.addPoint(testPoint);
        for (Coordinate coordinate : coordinates) {
            Point testPoint = geometryFactory.createPoint(coordinate);
            drawer.addPoint(testPoint);
        }

        JFrame frame = new JFrame("Test");
        frame.setSize(1500, 1500);
        frame.setLocation(1000, 500);
        frame.add(drawer);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static int findContainingCell(HighOrderVoronoiDiagram diagram, Point point) {
        PreparedGeometryFactory pgFactory = new PreparedGeometryFactory();

        for (int i = 0; i < diagram.getNumOfVoronoiSets(); i++) {
            Geometry cell = diagram.getVoronoiSet(i).getVoronoiCell();
            PreparedGeometry preparedCell = pgFactory.create(cell);

            if (preparedCell.contains(point)) {
                return i;
            }
        }

        return -1;
    }

    private static int findContainingCell(Geometry diagram, Point point) {
        PreparedGeometryFactory pgFactory = new PreparedGeometryFactory();

        for (int i = 0; i < diagram.getNumGeometries(); i++) {
            Geometry cell = diagram.getGeometryN(i);
            PreparedGeometry preparedCell = pgFactory.create(cell);

            if (preparedCell.contains(point)) {
                return i;
            }
        }

        return -1;
    }

//    private static Geometry findContainingCell(Geometry diagram, Point point) {
//        PreparedGeometryFactory pgFactory = new PreparedGeometryFactory();
//
//        for (int i = 0; i < diagram.getNumGeometries(); i++) {
//            Geometry cell = diagram.getGeometryN(i);
//            PreparedGeometry preparedCell = pgFactory.create(cell);
//
//            if (preparedCell.contains(point)) {
//                return cell;
//            }
//        }
//
//        return null;
//    }
}

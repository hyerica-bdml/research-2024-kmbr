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

public class Test {

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
        for (int i = 0; i < 10; i += 1) {
            coordinates.add(new Coordinate(random.nextDouble() * 100, random.nextDouble() * 100));
        }

        // MultiPoint 객체 생성
        MultiPoint points = geometryFactory.createMultiPointFromCoords(coordinates.toArray(new Coordinate[0]));

        // Voronoi 다이어그램 빌더 생성
        VoronoiDiagramBuilder voronoiDiagramBuilder = new VoronoiDiagramBuilder();
        voronoiDiagramBuilder.setSites(points);

        Envelope envelope = new Envelope(-200, 200, -200, 200);
        voronoiDiagramBuilder.setClipEnvelope(envelope);

        // Voronoi 다이어그램 생성
        Geometry diagram = voronoiDiagramBuilder.getDiagram(geometryFactory);

        // 생성된 Voronoi 다이어그램의 각 셀을 출력
        for (int i = 0; i < diagram.getNumGeometries(); i++) {
            Geometry cell = diagram.getGeometryN(i);
            System.out.println("Cell " + i + ": " + cell);
//            System.out.println("Cell " + i + ": " + (Coordinate) cell.getUserData());
        }

        // 특정 점이 어떤 Voronoi 셀에 포함되는지 확인
//        Coordinate testPointCoord = new Coordinate(30, 10);
//        Point testPoint = geometryFactory.createPoint(testPointCoord);

//        Geometry containingCell = findContainingCell(diagram, testPoint);

//        if (containingCell != null) {
//            System.out.println("Point " + testPoint + " is contained in cell: " + containingCell);
//        } else {
//            System.out.println("Point " + testPoint + " is not contained in any Voronoi cell.");
//        }

//        int containingCellIndex = findContainingCell(diagram, testPoint);
//
//        if (containingCellIndex > -1) {
//            System.out.println("Point " + testPoint + " is contained in cell: " + containingCellIndex);
//        } else {
//            System.out.println("Point " + testPoint + " is not contained in any Voronoi cell.");
//        }

        VoronoiDiagramDrawer drawer = new VoronoiDiagramDrawer(diagram);
//        drawer.addPoint(testPoint);
        for (Coordinate coordinate : coordinates) {
            Point testPoint = geometryFactory.createPoint(coordinate);
            drawer.addPoint(testPoint);
        }

        JFrame frame = new JFrame("Test");
        frame.setSize(500, 500);
        frame.setLocation(500, 500);
        frame.add(drawer);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
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

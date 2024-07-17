package edu.hanyang.voronoi;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.locationtech.jts.operation.polygonize.Polygonizer;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import javax.swing.*;
import java.util.*;

public class HighOrderVoronoiDiagram {

    protected Coordinate[] coordinates;
    protected List<VoronoiSet> voronoiSets;
    protected int order;

    public HighOrderVoronoiDiagram(final List<Coordinate> coordinates, final int order) {
        this.coordinates = new Coordinate[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i += 1) {
            this.coordinates[i] = coordinates.get(i);
        }

        this.order = order;
        constructHighOrderVoronoiDiagram(Arrays.asList(this.coordinates), this.order);
    }

    public Coordinate[] getCoordinates() {
        return coordinates;
    }

    public List<VoronoiSet> getVoronoiSets() {
        return voronoiSets;
    }

    public int getNumOfVoronoiSets() {
        return this.voronoiSets.size();
    }

    public VoronoiSet getVoronoiSet(final int index) {
        return voronoiSets.get(index);
    }

    protected Geometry constructFirstOrderVoronoiDiagram(final List<Coordinate> coordinates) {
        GeometryFactory geometryFactory = new GeometryFactory();
        MultiPoint points = geometryFactory.createMultiPointFromCoords(coordinates.toArray(new Coordinate[0]));
        VoronoiDiagramBuilder voronoiDiagramBuilder = new VoronoiDiagramBuilder();
        voronoiDiagramBuilder.setSites(points);

//        Envelope envelope = new Envelope(-100, 150, -100, 150);
        Envelope envelope = new Envelope(-1000, 1000, -1000, 1000);
//        Envelope envelope = new Envelope(-200, 250, -200, 250);
        voronoiDiagramBuilder.setClipEnvelope(envelope);

        return voronoiDiagramBuilder.getDiagram(geometryFactory);
    }

    protected void constructHighOrderVoronoiDiagram(final List<Coordinate> coordinates, final int order) {
        voronoiSets = new LinkedList<>();

        Geometry diagram = constructFirstOrderVoronoiDiagram(coordinates);
        for (int i = 0; i < diagram.getNumGeometries(); i += 1) {
            Geometry cell = diagram.getGeometryN(i);
            cell = validate(cell);

            List<Coordinate> voronoiSetCoords = new LinkedList<>();
            voronoiSetCoords.add((Coordinate) cell.getUserData());
            VoronoiSet voronoiSet = new VoronoiSet(voronoiSetCoords, cell);
            cell.setUserData(voronoiSet.getVoronoiSetAsArray());
            voronoiSets.add(voronoiSet);
        }

        while (voronoiSets.get(0).getOrder() < order) {
            VoronoiSet voronoiSet = voronoiSets.get(0);
            Geometry voronoiCell = voronoiSet.getVoronoiCell();

            List<Coordinate> voronoiSetCoords = voronoiSet.getVoronoiSet();
            List<Coordinate> voronoiCoords = getHighOrderCoordinatesCombination(voronoiSetCoords, coordinates);
            Geometry partialVoronoiDiagram = constructFirstOrderVoronoiDiagram(voronoiCoords);

            for (int g = 0; g < partialVoronoiDiagram.getNumGeometries(); g += 1) {
                Geometry partialCell = partialVoronoiDiagram.getGeometryN(g);
                Geometry intersection = voronoiCell.intersection(partialCell);
                intersection = validate(intersection);
//                if (!intersection.isValid()) intersection = intersection.buffer(0);

                if (intersection.isValid() && intersection.getArea() > 0.0) {
                    List<Coordinate> newVoronoiSetCoords = new LinkedList<>(voronoiSetCoords);
                    newVoronoiSetCoords.add((Coordinate) partialCell.getUserData());

                    for (int j = 1; j < voronoiSets.size(); j += 1) {
                        VoronoiSet vset = voronoiSets.get(j);
                        Geometry targetVoronoiCell = vset.getVoronoiCell();

                        if (vset.isEqual(newVoronoiSetCoords)) {
                            try {
                                intersection = intersection.union(targetVoronoiCell).convexHull();
                                intersection = validate(intersection);
                            } catch (TopologyException ignored) {}
                            voronoiSets.remove(vset);
                        }
                    }

                    VoronoiSet newVoronoiSet = new VoronoiSet(newVoronoiSetCoords, intersection);
                    intersection.setUserData(newVoronoiSet.getVoronoiSetAsArray());
                    voronoiSets.add(newVoronoiSet);
                }
            }

            voronoiSets.remove(0);
        }

//
//        voronoiSets = new LinkedList<>();
//
//        Geometry diagram = constructFirstOrderVoronoiDiagram(coordinates);
//        for (int i = 0; i < diagram.getNumGeometries(); i += 1) {
//            Geometry cell = diagram.getGeometryN(i);
//            cell = validate(cell);
//
//            List<Coordinate> voronoiSetCoords = new LinkedList<>();
//            voronoiSetCoords.add((Coordinate) cell.getUserData());
//            VoronoiSet voronoiSet = new VoronoiSet(voronoiSetCoords, cell);
//            cell.setUserData(voronoiSet.getVoronoiSetAsArray());
//            voronoiSets.add(voronoiSet);
//        }
//
//        while (voronoiSets.get(0).getOrder() < order) {
//            VoronoiSet voronoiSet = voronoiSets.get(0);
//            Geometry voronoiCell = voronoiSet.getVoronoiCell();
//
//            List<Coordinate> voronoiSetCoords = voronoiSet.getVoronoiSet();
//            List<Coordinate> voronoiCoords = getHighOrderCoordinatesCombination(voronoiSetCoords, coordinates);
//            Geometry partialVoronoiDiagram = constructFirstOrderVoronoiDiagram(voronoiCoords);
//
//            for (int g = 0; g < partialVoronoiDiagram.getNumGeometries(); g += 1) {
//                Geometry partialCell = partialVoronoiDiagram.getGeometryN(g);
//                Geometry intersection = voronoiCell.intersection(partialCell);
//                intersection = validate(intersection);
////                if (!intersection.isValid()) intersection = intersection.buffer(0);
//
//                if (intersection.isValid() && intersection.getArea() > 0.0) {
//                    List<Coordinate> newVoronoiSetCoords = new LinkedList<>(voronoiSetCoords);
//                    newVoronoiSetCoords.add((Coordinate) partialCell.getUserData());
//
//                    for (int j = 1; j < voronoiSets.size(); j += 1) {
//                        VoronoiSet vset = voronoiSets.get(j);
//                        Geometry targetVoronoiCell = vset.getVoronoiCell();
//
//                        if (vset.isEqual(newVoronoiSetCoords)) {
//                            try {
//                                if (!intersection.covers(targetVoronoiCell) && ! targetVoronoiCell.covers(intersection)) {
//                                    intersection = intersection.union(targetVoronoiCell).convexHull();
//                                    intersection = validate(intersection);
//                                } else if (targetVoronoiCell.covers(intersection)) {
//                                    intersection = targetVoronoiCell;
//                                }
////                                if (!intersection.isValid()) intersection = intersection.buffer(0);
//                            } catch (TopologyException exc) {
//                                TestBoard tb = new TestBoard(new Geometry[] {intersection, targetVoronoiCell});
//                                JFrame frame = new JFrame();
//                                frame.add(tb);
//                                frame.setSize(1500, 1500);
//                                frame.setLocation(500, 500);
//                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                                frame.setVisible(true);
//                                exc.printStackTrace();
//                                System.out.println(intersection);
//                                System.out.println(targetVoronoiCell);
//                                System.out.println("AREA1: " + intersection.getArea());
//                                System.out.println("AREA2: " + targetVoronoiCell.getArea());
//                            }
//                            voronoiSets.remove(vset);
//                        }
//                    }
//
//                    VoronoiSet newVoronoiSet = new VoronoiSet(newVoronoiSetCoords, intersection);
//                    intersection.setUserData(newVoronoiSet.getVoronoiSetAsArray());
//                    voronoiSets.add(newVoronoiSet);
//                }
//            }
//
//            voronoiSets.remove(0);
//        }
//        for (int i = 0; i < diagram.getNumGeometries(); i += 1) {
//            Geometry cell = diagram.getGeometryN(i);
//            cell = validate(cell);
//
//            List<Coordinate> voronoiSetCoords = new LinkedList<>();
//            voronoiSetCoords.add((Coordinate) cell.getUserData());
//            VoronoiSet voronoiSet = new VoronoiSet(voronoiSetCoords, cell);
//            cell.setUserData(voronoiSet.getVoronoiSetAsArray());
//            voronoiSets.add(voronoiSet);
//        }
//
//        while (voronoiSets.get(0).getOrder() < order) {
//            VoronoiSet voronoiSet = voronoiSets.get(0);
//            Geometry voronoiCell = voronoiSet.getVoronoiCell();
//
//            List<Coordinate> voronoiSetCoords = voronoiSet.getVoronoiSet();
//            List<Coordinate> voronoiCoords = getHighOrderCoordinatesCombination(voronoiSetCoords, coordinates);
//            Geometry partialVoronoiDiagram = constructFirstOrderVoronoiDiagram(voronoiCoords);
//
//            for (int g = 0; g < partialVoronoiDiagram.getNumGeometries(); g += 1) {
//                Geometry partialCell = partialVoronoiDiagram.getGeometryN(g);
//                Geometry intersection = voronoiCell.intersection(partialCell);
//                intersection = validate(intersection);
////                if (!intersection.isValid()) intersection = intersection.buffer(0);
//
//                if (intersection.isValid() && intersection.getArea() > 0.0) {
//                    List<Coordinate> newVoronoiSetCoords = new LinkedList<>(voronoiSetCoords);
//                    newVoronoiSetCoords.add((Coordinate) partialCell.getUserData());
//
//                    for (int j = 1; j < voronoiSets.size(); j += 1) {
//                        VoronoiSet vset = voronoiSets.get(j);
//                        Geometry targetVoronoiCell = vset.getVoronoiCell();
//
//                        if (vset.isEqual(newVoronoiSetCoords)) {
//                            try {
//                                if (!intersection.covers(targetVoronoiCell) && ! targetVoronoiCell.covers(intersection)) {
//                                    intersection = intersection.union(targetVoronoiCell).convexHull();
//                                    intersection = validate(intersection);
//                                } else if (targetVoronoiCell.covers(intersection)) {
//                                    intersection = targetVoronoiCell;
//                                }
////                                if (!intersection.isValid()) intersection = intersection.buffer(0);
//                            } catch (TopologyException exc) {
//                                TestBoard tb = new TestBoard(new Geometry[] {intersection, targetVoronoiCell});
//                                JFrame frame = new JFrame();
//                                frame.add(tb);
//                                frame.setSize(1500, 1500);
//                                frame.setLocation(500, 500);
//                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                                frame.setVisible(true);
//                                exc.printStackTrace();
//                                System.out.println(intersection);
//                                System.out.println(targetVoronoiCell);
//                                System.out.println("AREA1: " + intersection.getArea());
//                                System.out.println("AREA2: " + targetVoronoiCell.getArea());
//                            }
//                            voronoiSets.remove(vset);
//                        }
//                    }
//
//                    VoronoiSet newVoronoiSet = new VoronoiSet(newVoronoiSetCoords, intersection);
//                    intersection.setUserData(newVoronoiSet.getVoronoiSetAsArray());
//                    voronoiSets.add(newVoronoiSet);
//                }
//            }
//
//            voronoiSets.remove(0);
//        }

//        for (int o = 0; o < order; o += 1) {
//            int voronoiSetSize = voronoiSets.size();
//
//            for (int i = 0; i < voronoiSetSize; i += 1) {
//                VoronoiSet voronoiSet = voronoiSets.get(i);
//                Geometry voronoiCell = voronoiSet.getVoronoiCell();
//                List<Coordinate> voronoiSetCoords = voronoiSet.getVoronoiSet();
//                List<Coordinate> voronoiCoords = getHighOrderCoordinatesCombination(voronoiSetCoords, coordinates);
//                Geometry partialVoronoiDiagram = constructFirstOrderVoronoiDiagram(voronoiCoords);
//
//                for (int g = 0; g < partialVoronoiDiagram.getNumGeometries(); g += 1) {
//                    Geometry partialCell = partialVoronoiDiagram.getGeometryN(g);
//                    Geometry intersection = voronoiCell.intersection(partialCell);
//
////                System.out.println("Area of intersection: " + intersection.getArea());
//                    if (intersection.getArea() > 0) {
//                        List<Coordinate> newVoronoiSetCoords = new LinkedList<>(voronoiSetCoords);
//                        newVoronoiSetCoords.add((Coordinate) partialCell.getUserData());
//
//                        VoronoiSet newVoronoiSet = new VoronoiSet(newVoronoiSetCoords, intersection);
//                        intersection.setUserData(newVoronoiSet.getVoronoiSetAsArray());
//                        voronoiSets.add(newVoronoiSet);
//                    }
//                }
//            }
//
//            voronoiSets = voronoiSets.subList(voronoiSetSize, voronoiSets.size());
//            List<Integer> indexToRemove = new LinkedList<>();
//
//            for (int i = 0; i < voronoiSets.size() - 1; i += 1) {
//                for (int j = 1; j < voronoiSets.size(); j += 1) {
//                    VoronoiSet vseti = voronoiSets.get(i);
//                    VoronoiSet vsetj = voronoiSets.get(j);
//
//
//                }
//            }
//        }

//        Geometry diagram = constructFirstOrderVoronoiDiagram(coordinates);
//        for (int i = 0; i < diagram.getNumGeometries(); i += 1) {
//            Geometry cell = diagram.getGeometryN(i);
//            Polygon[] polygon = new Polygon[] {(Polygon) cell};
//            MultiPolygon multiPolygon = new MultiPolygon(polygon, diagram.getFactory());
//
//            List<Coordinate> voronoiSetCoords = new LinkedList<>();
//            voronoiSetCoords.add((Coordinate) cell.getUserData());
//            VoronoiSet voronoiSet = new VoronoiSet(voronoiSetCoords, multiPolygon);
//            cell.setUserData(voronoiSet.getVoronoiSetAsArray());
//            voronoiSets.add(voronoiSet);
//        }
//
//        while (voronoiSets.get(0).getOrder() < order) {
//            VoronoiSet voronoiSet = voronoiSets.get(0);
//            Geometry voronoiCell = voronoiSet.getVoronoiCell();
//
//            List<Coordinate> voronoiSetCoords = voronoiSet.getVoronoiSet();
//            List<Coordinate> voronoiCoords = getHighOrderCoordinatesCombination(voronoiSetCoords, coordinates);
//            Geometry partialVoronoiDiagram = constructFirstOrderVoronoiDiagram(voronoiCoords);
//
//            for (int g = 0; g < partialVoronoiDiagram.getNumGeometries(); g += 1) {
//                Geometry partialCell = partialVoronoiDiagram.getGeometryN(g);
//                Geometry intersection = voronoiCell.intersection(partialCell);
////                intersection.normalize();
//
////                System.out.println("Area of intersection: " + intersection.getArea());
//                if (intersection.isValid() && intersection.getArea() > 0.00001) {
//                    List<Coordinate> newVoronoiSetCoords = new LinkedList<>(voronoiSetCoords);
//                    newVoronoiSetCoords.add((Coordinate) partialCell.getUserData());
//
//                    for (int j = 1; j < voronoiSets.size(); j += 1) {
//                        VoronoiSet vset = voronoiSets.get(j);
//                        Geometry targetVoronoiCell = vset.getVoronoiCell();
//
//                        if (vset.isEqual(newVoronoiSetCoords)) {
//                            Polygon[] newPolygons = new Polygon[intersection.getNumGeometries() + targetVoronoiCell.getNumGeometries()];
//
//                            int l = 0;
//                            for (; l < intersection.getNumGeometries(); l += 1)
//                                newPolygons[l] = (Polygon) intersection.getGeometryN(l);
//                            for (; l < newPolygons.length; l += 1)
//                                newPolygons[l] = (Polygon) targetVoronoiCell.getGeometryN(l);
//
//                            intersection = new MultiPolygon(newPolygons, partialVoronoiDiagram.getFactory());
//                            voronoiSets.remove(vset);
//                        }
//                    }
//
////                    intersection.normalize();
//                    VoronoiSet newVoronoiSet = new VoronoiSet(newVoronoiSetCoords, intersection);
//                    intersection.setUserData(newVoronoiSet.getVoronoiSetAsArray());
//                    voronoiSets.add(newVoronoiSet);
//                }
//            }
//
//            voronoiSets.remove(0);
//        }

//        while (voronoiSets.get(0).getOrder() < order) {
//            VoronoiSet voronoiSet = voronoiSets.get(0);
//            Geometry voronoiCell = voronoiSet.getVoronoiCell();
//
//            List<Coordinate> voronoiSetCoords = voronoiSet.getVoronoiSet();
//            List<Coordinate> voronoiCoords = getHighOrderCoordinatesCombination(voronoiSetCoords, coordinates);
//            Geometry partialVoronoiDiagram = constructFirstOrderVoronoiDiagram(voronoiCoords);
//
//            for (int g = 0; g < partialVoronoiDiagram.getNumGeometries(); g += 1) {
//                Geometry partialCell = partialVoronoiDiagram.getGeometryN(g);
//                Geometry intersection = voronoiCell.intersection(partialCell);
//
////                System.out.println("Area of intersection: " + intersection.getArea());
//                if (intersection.isValid() && intersection.getArea() > 0) {
//                    List<Coordinate> newVoronoiSetCoords = new LinkedList<>(voronoiSetCoords);
//                    newVoronoiSetCoords.add((Coordinate) partialCell.getUserData());
//
//                    boolean foundMatch = false;
//
//                    for (int j = 1; j < voronoiSets.size(); j += 1) {
//                        VoronoiSet vset = voronoiSets.get(j);
//
//                        if (vset.isEqual(newVoronoiSetCoords)) {
////                            System.out.println("FOUND MATCH:");
////                            System.out.print("\tVoronoiSet 1: ");
////                            for (Coordinate c: newVoronoiSetCoords)
////                                System.out.print(c + " ");
////                            System.out.print("\tVoronoiSet 2: ");
////                            for (Coordinate c: vset.getVoronoiSet())
////                                System.out.print(c + " ");
////                            System.out.println();
//
//                            Geometry newVoronoiCell = intersection.union(vset.getVoronoiCell());
//                            VoronoiSet newVoronoiSet = new VoronoiSet(vset.getVoronoiSet(), newVoronoiCell);
//                            newVoronoiCell.setUserData(newVoronoiSet.getVoronoiSetAsArray());
//                            voronoiSets.set(j, newVoronoiSet);
//                            foundMatch = true;
//                            break;
//                        }
//                    }
//
//                    if (!foundMatch) {
//                        VoronoiSet newVoronoiSet = new VoronoiSet(newVoronoiSetCoords, intersection);
//                        intersection.setUserData(newVoronoiSet.getVoronoiSetAsArray());
//                        voronoiSets.add(newVoronoiSet);
//                    }
//                }
//            }
//
//            voronoiSets.remove(0);
//        }
    }

    protected List<Coordinate> getHighOrderCoordinatesCombination(final List<Coordinate> voronoiSet, final List<Coordinate> coordinates) {
        List<Coordinate> highOrderCoordinates = new LinkedList<>();

        for (Coordinate coord : coordinates)
            if (!voronoiSet.contains(coord))
                highOrderCoordinates.add(coord);

        return highOrderCoordinates;
    }

    protected int findContainingCell(Geometry diagram, Point point) {
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

    /**
     * https://stackoverflow.com/questions/31473553/is-there-a-way-to-convert-a-self-intersecting-polygon-to-a-multipolygon-in-jts
     * @param geom
     * @return
     */
    protected Geometry validate(Geometry geom){
        if(geom instanceof Polygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the polygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            addPolygon((Polygon)geom, polygonizer);
            return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
        }else if(geom instanceof MultiPolygon){
            if(geom.isValid()){
                geom.normalize(); // validate does not pick up rings in the wrong order - this will fix that
                return geom; // If the multipolygon is valid just return it
            }
            Polygonizer polygonizer = new Polygonizer();
            for(int n = geom.getNumGeometries(); n-- > 0;){
                addPolygon((Polygon)geom.getGeometryN(n), polygonizer);
            }
            return toPolygonGeometry(polygonizer.getPolygons(), geom.getFactory());
        }else{
            return geom; // In my case, I only care about polygon / multipolygon geometries
        }
    }

    protected void addPolygon(Polygon polygon, Polygonizer polygonizer){
        addLineString(polygon.getExteriorRing(), polygonizer);
        for(int n = polygon.getNumInteriorRing(); n-- > 0;){
            addLineString(polygon.getInteriorRingN(n), polygonizer);
        }
    }

    protected void addLineString(LineString lineString, Polygonizer polygonizer){

        if(lineString instanceof LinearRing){ // LinearRings are treated differently to line strings : we need a LineString NOT a LinearRing
            lineString = lineString.getFactory().createLineString(lineString.getCoordinateSequence());
        }

        // unioning the linestring with the point makes any self intersections explicit.
        Point point = lineString.getFactory().createPoint(lineString.getCoordinateN(0));
        Geometry toAdd = lineString.union(point);

        //Add result to polygonizer
        polygonizer.add(toAdd);
    }

    protected Geometry toPolygonGeometry(Collection<Polygon> polygons, GeometryFactory factory){
        switch(polygons.size()){
            case 0:
                return null; // No valid polygons!
            case 1:
                return polygons.iterator().next(); // single polygon - no need to wrap
            default:
                //polygons may still overlap! Need to sym difference them
                Iterator<Polygon> iter = polygons.iterator();
                Geometry ret = iter.next();
                while(iter.hasNext()){
                    ret = ret.symDifference(iter.next());
                }
                return ret;
        }
    }
}

package edu.hanyang.voronoi1991;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;
import edu.hanyang.kmbr.utils.Utilities;
import edu.hanyang.utils.Tuple;
import edu.hanyang.voronoi.HighOrderVoronoiDiagram;
import edu.hanyang.voronoi.VoronoiSet;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

import java.util.*;
import java.util.stream.Collectors;

public class Voronoi1991 {

    protected final List<ClusterAssignment> clusterAssignments;
    protected final int k;

    public Voronoi1991(final List<ClusterAssignment> clusterAssignments, final int k) {
        this.clusterAssignments = clusterAssignments;
        this.k = k;
    }

    public VoronoiMBRResult find() {
        int order = 6*k - 6;
        List<Coordinate> coordinates = getCoordinates(clusterAssignments);
        HighOrderVoronoiDiagram voronoiDiagram = new HighOrderVoronoiDiagram(coordinates, order);
//        System.out.println("High order voronoi");
        List<VoronoiSet> voronoiSets = voronoiDiagram.getVoronoiSets();

        Map<Tuple<Coordinate>, VoronoiMBRResult> resultMap = new LinkedHashMap<>();
        for (VoronoiSet voronoiSet: voronoiSets) {
            List<Coordinate> voronoiSetCoordinates = voronoiSet.getVoronoiSet();

            for (int i = 0; i < voronoiSetCoordinates.size() - 1; i += 1) {
                for (int j = i + 1; j < voronoiSetCoordinates.size(); j += 1) {
                    Coordinate c1 = voronoiSetCoordinates.get(i);
                    Coordinate c2 = voronoiSetCoordinates.get(j);

                    Tuple<Coordinate> tuple = new Tuple<>(c1, c2);

                    VoronoiMBRResult result = findLocal(voronoiSet, c1, c2);
                    if (resultMap.containsKey(tuple) && result.size() > resultMap.get(tuple).size())
                        continue;

                    resultMap.put(tuple, result);
                }
            }
        }

        Optional<VoronoiMBRResult> finalResult = resultMap
                .values()
                .stream()
                .reduce((r1, r2) -> {
                    if (r2 == null) return r1;
                    else if (r1.size() < r2.size()) return r1;
                    else return r2;
                });

        return finalResult.orElse(null);
    }

    protected VoronoiMBRResult findLocal(final VoronoiSet voronoiSet, final Coordinate p1, final Coordinate p2) {
        List<Coordinate> coordinates = voronoiSet.getVoronoiSet()
                 .stream()
                 .filter(p -> p.x >= p1.x && p.y >= p2.y)
                 .sorted(Comparator.comparingDouble(c -> c.x))
                 .collect(Collectors.toList());

        VoronoiMBRResult result = null;

        while (coordinates.size() >= k) {
            Coordinate u = getTopCoordinate(coordinates);
            Coordinate v = coordinates.get(k - 1);

            if (u.y < p1.y) {
                return result;
            }
            else if (v.x < Math.max(u.x, p2.x)) {
                coordinates.remove(u);
            }
            else {
                List<Coordinate> mbrCoords = coordinates.stream()
                        .filter(c -> c.x <= v.x && c.y <= u.y)
                        .collect(Collectors.toList());

                result = new VoronoiMBRResult(mbrCoords);
                coordinates.remove(u);
            }
        }

        return result;
    }

    protected List<Coordinate> getCoordinates(final List<ClusterAssignment> clusterAssignments) {
        List<Coordinate> coordinates = new LinkedList<>();

        for (int i = 0; i < clusterAssignments.size(); i += 1) {
            ClusterAssignment clusterAssignment = clusterAssignments.get(i);
            coordinates.add(new Coordinate(
                    clusterAssignment.getPoint().getX(),
                    clusterAssignment.getPoint().getY()
            ));
        }

        return coordinates;
    }

    public VoronoiMBRResult computeMBR(Coordinate[] xSortedPoints, Coordinate[] ySortedPoints) {
        if (xSortedPoints.length < Config.K) return null;

        Coordinate[] mbrBuffer = new Coordinate[Config.K];
        VoronoiMBRResult minMBR = null;

        for (int yStart = 0; yStart < ySortedPoints.length - Config.K; yStart += 1) {
            mbrBuffer[0] = ySortedPoints[yStart];

            for (int yEnd = yStart + Config.K - 1; yEnd < ySortedPoints.length; yEnd += 1) {
                if (minMBR != null && ySortedPoints[yEnd].getY() - ySortedPoints[yStart].getY() >= minMBR.size()/2)
                    break;

                mbrBuffer[1] = ySortedPoints[yEnd];
                int index = 2;

                for (int xStart = 0; xStart < xSortedPoints.length; xStart += 1) {
                    for (int j = xStart; j < xSortedPoints.length && index < Config.K; j += 1) {
                        if ((xSortedPoints[j] != mbrBuffer[0] && xSortedPoints[j] != mbrBuffer[1]) &&
                                (xSortedPoints[j].getY() >= ySortedPoints[yStart].getY()) &&
                                (xSortedPoints[j].getY() < ySortedPoints[yEnd].getY())) {
                            mbrBuffer[index] = xSortedPoints[j];
                            index += 1;
                        }
                    }

                    if (index == Config.K)
                        if (minMBR == null || computeMBRSize(mbrBuffer) < minMBR.size())
                            minMBR = new VoronoiMBRResult(Arrays.asList(mbrBuffer));

                    index = 2;
                }
            }
        }
        return minMBR;
    }

    public double computeMBRSize(Coordinate[] Points) {
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE, minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Coordinate p: Points) {
            if (p == null) break;

            double x = p.getX();
            double y = p.getY();

            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
        }

        return ((maxX - minX) + (maxY - minY))*2;
    }

    protected Coordinate getTopCoordinate(final List<Coordinate> coordinates) {
        Coordinate top = coordinates.get(0);
        for (int i = 1; i < coordinates.size(); i += 1)
            if (top.y < coordinates.get(i).y)
                top = coordinates.get(i);
        return top;
    }

//    public VoronoiMBRResult find() {
//        int order = 6*k - 6;
//        List<Coordinate> coordinates = getCoordinates(clusterAssignments);
//        HighOrderVoronoiDiagram voronoiDiagram = new HighOrderVoronoiDiagram(coordinates, order);
//        System.out.println("High order voronoi");
//        List<VoronoiSet> voronoiSets = voronoiDiagram.getVoronoiSets();
//
//        VoronoiMBRResult result = null;
//        for (VoronoiSet voronoiSet: voronoiSets) {
//            VoronoiMBRResult temp = findLocal(voronoiSet);
//            if (result == null || result.size() > temp.size())
//                result = temp;
//        }
//
//        return result;
//    }
//
//    protected VoronoiMBRResult findLocal(final VoronoiSet voronoiSet) {
//        List<Coordinate> coordinates = voronoiSet.getVoronoiSet();
//        Coordinate[] xSortedCoords = coordinates.toArray(new Coordinate[0]);
//        Coordinate[] ySortedCoords = coordinates.toArray(new Coordinate[0]);
//
//        Arrays.sort(xSortedCoords, Comparator.comparingDouble(Coordinate::getX));
//        Arrays.sort(ySortedCoords, Comparator.comparingDouble(Coordinate::getY));
//
//        return computeMBR(xSortedCoords, ySortedCoords);
//    }
}

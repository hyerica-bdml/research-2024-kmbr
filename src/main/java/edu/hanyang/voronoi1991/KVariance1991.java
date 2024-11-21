package edu.hanyang.voronoi1991;

import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.voronoi.HighOrderVoronoiDiagram;
import edu.hanyang.voronoi.VoronoiSet;
import org.locationtech.jts.geom.Coordinate;

import java.util.*;

public class KVariance1991 {

    protected final List<ClusterAssignment> clusterAssignments;
    protected final int k;

    public KVariance1991(final List<ClusterAssignment> clusterAssignments, final int k) {
        this.clusterAssignments = clusterAssignments;
        this.k = k;
    }

    public VoronoiKVarianceResult find() {
        List<Coordinate> coordinates = getCoordinates(clusterAssignments);
        HighOrderVoronoiDiagram voronoiDiagram = new HighOrderVoronoiDiagram(coordinates, k);
//        System.out.println("High order voronoi");
        List<VoronoiSet> voronoiSets = voronoiDiagram.getVoronoiSets();

        double minVariance = Double.MAX_VALUE;
        VoronoiKVarianceResult result = null;
        for (VoronoiSet voronoiSet: voronoiSets) {
            List<Coordinate> voronoiSetCoordinates = voronoiSet.getVoronoiSet();
            VoronoiKVarianceResult temp = new VoronoiKVarianceResult(voronoiSetCoordinates);
            double variance = temp.computeVariance();

            if (minVariance > variance) {
                minVariance = variance;
                result = temp;
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
}

package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class DistanceMatTest {

//    @Test
    public void multipleTest() {
        for (int i = 5000; i < 10000; i += 1) {
//            System.out.println("SEED: " + i);
            test(i);
        }
//        test(1000);
    }

//    @Test
    public void test(int seed) {
        DatabaseInteractor db = DatabaseInteractor.getInstance();
        Random random = new Random(seed);

        List<ClusterAssignment> points = new LinkedList<>();
        for (int i = 0; i < 20; i += 1) {
//            points.add(new ClusterAssignment(i, new Point(i, Math.pow(2, i), i+1)));
            points.add(new ClusterAssignment(i, new Point(i, random.nextDouble(), i+1)));
        }

        DistanceMatrix distanceMatrix = new DistanceMatrix(points, DistanceMatrix.SortType.X);

//        System.out.println("=== Distance Matrix ===");
//        distanceMatrix.print();
//        System.out.println();
//
        distanceMatrix.removePoint(points.get(2));
        distanceMatrix.removePoint(points.get(5));
//        System.out.println("=== Distance Matrix (after removing) ===");
//        distanceMatrix.print();

        List<ClusterAssignment> sublist = distanceMatrix.get();
        double finalDist = sublist.get(sublist.size() - 1).getPoint().getX() - sublist.get(0).getPoint().getX();


        List<ClusterAssignment> newPoints = new LinkedList<>(points);
        newPoints.remove(points.get(2));
        newPoints.remove(points.get(5));
        newPoints.sort(Comparator.comparingDouble(c -> c.getPoint().getX()));

        double minDist = Double.MAX_VALUE;
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < newPoints.size() - (Config.K - 1); i += 1) {
            double dist = newPoints.get(i + Config.K - 1).getPoint().getX() - newPoints.get(i).getPoint().getX();
            if (dist < minDist) {
                minDist = dist;
                startIndex = i;
                endIndex = startIndex + Config.K;
            }
        }

//        System.out.println("===");
//        for (int i = 0; i < sublist.size(); i += 1) {
//            System.out.print(sublist.get(i).getPoint().getId() + " ");
//        }
//        System.out.println();
//
//        System.out.println("===");
//        for (int i = 0; i < newPoints.size(); i += 1) {
//            System.out.print(newPoints.get(i).getPoint().getId() + " ");
//        }
//        System.out.println();
//        System.out.println("START: " + startIndex + ", END: " + endIndex);


        Assert.assertEquals(sublist.size(), Config.K);
        Assert.assertEquals(endIndex - startIndex, Config.K);
        Assert.assertEquals(minDist, finalDist, 0.0001);
        Assert.assertEquals(newPoints.get(startIndex).getPoint().getId(), sublist.get(0).getPoint().getId());
        Assert.assertEquals(newPoints.get(endIndex - 1).getPoint().getId(), sublist.get(sublist.size() - 1).getPoint().getId());
    }

    @Test
    public void multipleRandomTest() {
        for (int i = 0; i < 100; i += 1) {
            System.out.println("SEED: " + i);
            randomTest(i);
        }
//        randomTest(1);
    }

//    @Test
    public void randomTest(int seed) {
        DatabaseInteractor db = DatabaseInteractor.getInstance();
        Random random = new Random(seed);

        List<ClusterAssignment> points = new LinkedList<>();
        for (int i = 0; i < 1000; i += 1) {
//            points.add(new ClusterAssignment(i, new Point(i, Math.pow(2, i), i+1)));
            points.add(new ClusterAssignment(i, new Point(i, random.nextDouble(), i+1)));
        }

        DistanceMatrix distanceMatrix = new DistanceMatrix(points, DistanceMatrix.SortType.X);

        List<ClusterAssignment> newPoints = new LinkedList<>(points);

        for (int i = 0; i < 100; i += 1) {
            int randomIndex = random.nextInt(points.size());
            distanceMatrix.removePoint(points.get(randomIndex));

            int ind = newPoints.indexOf(points.get(randomIndex));
            if (ind > 0)
                newPoints.remove(ind);
        }

        List<ClusterAssignment> sublist = distanceMatrix.get();
        double finalDist = sublist.get(sublist.size() - 1).getPoint().getX() - sublist.get(0).getPoint().getX();

        newPoints.sort(Comparator.comparingDouble(c -> c.getPoint().getX()));

        double minDist = Double.MAX_VALUE;
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < newPoints.size() - (Config.K - 1); i += 1) {
            double dist = newPoints.get(i + Config.K - 1).getPoint().getX() - newPoints.get(i).getPoint().getX();
            if (dist < minDist) {
                minDist = dist;
                startIndex = i;
                endIndex = startIndex + Config.K;
            }
        }

//        System.out.println("===");
//        for (int i = 0; i < sublist.size(); i += 1) {
//            System.out.print(sublist.get(i).getPoint().getId() + " ");
//        }
//        System.out.println();
//
//        System.out.println("===");
//        for (int i = 0; i < newPoints.size(); i += 1) {
//            System.out.print(newPoints.get(i).getPoint().getId() + " ");
//        }
//        System.out.println();
//        System.out.println("START: " + startIndex + ", END: " + endIndex);


        Assert.assertEquals(sublist.size(), Config.K);
        Assert.assertEquals(endIndex - startIndex, Config.K);
        Assert.assertEquals(minDist, finalDist, 0.0001);
        Assert.assertEquals(newPoints.get(startIndex).getPoint().getId(), sublist.get(0).getPoint().getId());
        Assert.assertEquals(newPoints.get(endIndex - 1).getPoint().getId(), sublist.get(sublist.size() - 1).getPoint().getId());
    }
}

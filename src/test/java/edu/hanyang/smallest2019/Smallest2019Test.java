package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;
import edu.hanyang.kmbr.utils.Utilities;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Smallest2019Test {

    @Test
    public void algorithmTest() {
        DatabaseInteractor db = DatabaseInteractor.getInstance();
//        Random random = new Random(40);
        Random random = new Random(102);

        List<ClusterAssignment> points = new LinkedList<>();
        for (int i = 0; i < 5000; i += 1) {
//            points.add(new ClusterAssignment(i, new Point(i, Math.pow(2, i), i+1)));
            points.add(new ClusterAssignment(i, new Point(i, random.nextDouble(), random.nextDouble())));
        }

        Smallest2019 method = new Smallest2019(points);

        long startTime = System.currentTimeMillis();
        SmallestMBRResult estimated = method.find();
        System.out.println("Algorithm runtime: " + (System.currentTimeMillis() - startTime) / 1000.0);
//        for (int i = 0; i < estimated.getPoints().size(); i += 1)
//            System.out.print(estimated.getPoints().get(i).getPoint().getId() + " ");
//        System.out.println();
        double estimatedSize = estimated.size();

        Point[] xSortedPoints = new Point[points.size()];
        Point[] ySortedPoints = new Point[points.size()];

        for (int i = 0; i < points.size(); i += 1) {
            xSortedPoints[i] = points.get(i).getPoint();
            ySortedPoints[i] = points.get(i).getPoint();
        }

        Arrays.sort(xSortedPoints, Comparator.comparingDouble(Point::getX));
        Arrays.sort(ySortedPoints, Comparator.comparingDouble(Point::getY));

        startTime = System.currentTimeMillis();
        MBR gt = Utilities.computeMBR(xSortedPoints, ySortedPoints);
        System.out.println("Algorithm runtime: " + (System.currentTimeMillis() - startTime) / 1000.0);
//        for (int i = 0; i < gt.getPoints().length; i += 1)
//            System.out.print(gt.getPoints()[i].getId() + " ");
//        System.out.println();
        double trueSize = gt.size();
//        savePoints(points, estimated.getPoints(), Arrays.asList(gt.getPoints()));

//        Assert.assertEquals(gt.getArea(), estimated.getArea(), 0.0001);
        System.out.println("Estimated: " + estimatedSize);
        System.out.println("True: " + trueSize);
        Assert.assertEquals(trueSize, estimatedSize, 0.0001);
    }

    private void savePoints(final List<ClusterAssignment> allPoints,
                            final List<ClusterAssignment> estimatedMBRPoints,
                            final List<Point> trueMBRPoints) {

        try (FileWriter fwriter = new FileWriter("test.txt");
             BufferedWriter writer = new BufferedWriter(fwriter)) {

            for (int i = 0; i < allPoints.size(); i += 1) {
                Point point = allPoints.get(i).getPoint();
                writer.write(String.format("%d %d %f %f\n", 0, point.getId(), point.getX(), point.getY()));
            }
            for (int i = 0; i < estimatedMBRPoints.size(); i += 1) {
                Point point = estimatedMBRPoints.get(i).getPoint();
                writer.write(String.format("%d %d %f %f\n", 1, point.getId(), point.getX(), point.getY()));
            }
            for (int i = 0; i < trueMBRPoints.size(); i += 1) {
                Point point = trueMBRPoints.get(i);
                writer.write(String.format("%d %d %f %f\n", 2, point.getId(), point.getX(), point.getY()));
            }
        } catch (IOException ignored) {}
    }
}

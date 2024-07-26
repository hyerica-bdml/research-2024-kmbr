package edu.hanyang.enclosing1998;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;
import edu.hanyang.kmbr.utils.Utilities;
import edu.hanyang.smallest2019.Smallest2019;
import edu.hanyang.smallest2019.SmallestMBRResult;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Enclosing1998Test {

    @Test
    public void algorithmTest() {
        DatabaseInteractor db = DatabaseInteractor.getInstance();
//        Random random = new Random(40);
        Random random = new Random(938);

        List<ClusterAssignment> points = new LinkedList<>();
        for (int i = 0; i < 500; i += 1) {
//            points.add(new ClusterAssignment(i, new Point(i, Math.pow(2, i), i+1)));
            points.add(new ClusterAssignment(i, new Point(i, random.nextDouble(), random.nextDouble())));
        }

        Enclosing1998 method = new Enclosing1998(points, Config.K);

        long startTime = System.currentTimeMillis();
        double estimatedSize = method.find();
        System.out.println("Algorithm runtime: " + (System.currentTimeMillis() - startTime) / 1000.0);
//        for (int i = 0; i < estimated.getPoints().size(); i += 1)
//            System.out.print(estimated.getPoints().get(i).getPoint().getId() + " ");
//        System.out.println();

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
//            System.out.println(gt.getPoints()[i]);
//        System.out.println();
        double trueSize = gt.size();
//        savePoints(points, Arrays.asList(gt.getPoints()));

//        Assert.assertEquals(gt.getArea(), estimated.getArea(), 0.0001);
        System.out.println("Estimated: " + estimatedSize);
        System.out.println("True: " + trueSize);
        Assert.assertEquals(trueSize, estimatedSize, 0.0001);
    }

    private void savePoints(final List<ClusterAssignment> allPoints,
                            final List<Point> trueMBRPoints) {

        try (FileWriter fwriter = new FileWriter("test.txt");
             BufferedWriter writer = new BufferedWriter(fwriter)) {

            for (int i = 0; i < allPoints.size(); i += 1) {
                Point point = allPoints.get(i).getPoint();
                writer.write(String.format("%d %d %f %f\n", 0, point.getId(), point.getX(), point.getY()));
            }
            for (int i = 0; i < trueMBRPoints.size(); i += 1) {
                Point point = trueMBRPoints.get(i);
                writer.write(String.format("%d %d %f %f\n", 2, point.getId(), point.getX(), point.getY()));
            }
        } catch (IOException ignored) {}
    }
}

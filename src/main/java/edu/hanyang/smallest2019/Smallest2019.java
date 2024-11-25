package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Smallest2019 {

    private List<ClusterAssignment> ySortedClusterAssignments;

    public Smallest2019(final List<ClusterAssignment> clusterAssignments) {
        this.ySortedClusterAssignments = new LinkedList<>(clusterAssignments);
        this.ySortedClusterAssignments.sort(Comparator.comparingDouble(c -> c.getPoint().getY()));
    }

    public SmallestMBRResult find() {
        return findRecursive(ySortedClusterAssignments, Config.K);
    }

    public SmallestMBRResult findRecursive(final List<ClusterAssignment> ySortedList, final int l) {
        if (ySortedList.size() > 12*l) {
            List<Rectangle> rectangles = computeRectangles(ySortedList, l);

            SmallestMBRResult result = null;

            for (Rectangle rect: rectangles) {
//                long startTime = System.currentTimeMillis();
                Smallest2019Base basicAlgorithm = new Smallest2019Base(rect.getClusterAssignments());
                SmallestMBRResult temp = basicAlgorithm.find();
//                System.out.println("Base algorithm time (rect): " + (System.currentTimeMillis() - startTime)/1000.0);
                if (result == null || result.size() > temp.size())
                    result = temp;
            }

            int midIndex = (ySortedList.size() + 1)/2;
            SmallestMBRResult bottom = findRecursive(ySortedList.subList(0, midIndex), l);
            SmallestMBRResult top = findRecursive(ySortedList.subList(midIndex, ySortedList.size()), l);

            if (bottom != null)
                result = bottom.size() < result.size() ? bottom : result;
            if (top != null)
                result = top.size() < result.size() ? top : result;

            return result;
        } else {
//            long startTime = System.currentTimeMillis();
            Smallest2019Base basicAlgorithm = new Smallest2019Base(ySortedList);
            SmallestMBRResult result = basicAlgorithm.find();
//            System.out.println("Base algorithm time (leaf): " + (System.currentTimeMillis() - startTime)/1000.0);
            return result;
        }
    }

    private List<Rectangle> computeRectangles(final List<ClusterAssignment> ySortedList, final int l) {
        int midIndex = (ySortedList.size() + 1)/2;
        double medianY = (ySortedList.get(midIndex - 1).getPoint().getY() + ySortedList.get(midIndex).getPoint().getY())/2;

//        long startTime = System.currentTimeMillis();
        List<ClusterAssignment> yAbsoluteSortedList = new LinkedList<>(ySortedList);
        yAbsoluteSortedList.sort(Comparator.comparingDouble(c -> Math.abs(c.getPoint().getY() - medianY)));
        List<Rectangle> finalRectangles = new LinkedList<>();
        List<Rectangle> rectangles = new LinkedList<>();
        rectangles.add(new Rectangle());

        for (int i = 0; i < yAbsoluteSortedList.size(); i += 1) {
            ClusterAssignment point = yAbsoluteSortedList.get(i);
            if (rectangles.size() == 1) {
                Rectangle rect = rectangles.get(0);
                rect.add(point);
                if (rect.size() == 6 * l) {
                    rectangles.add(rect.split());
                }
                continue;
            }

            for (int j = 0; j < rectangles.size() - 1; j += 1) {
                Rectangle left = rectangles.get(j);
                Rectangle right = rectangles.get(j + 1);
                double splitPoint = (left.getMaxX() + right.getMinX()) / 2;

                if (point.getPoint().getX() < splitPoint) {
                    left.add(point);
                    if (left.size() == 6 * l) {
                        if (j > 1)
                            finalRectangles.add(left.merge(rectangles.get(j - 1)));
                        finalRectangles.add(left.merge(right));
                        rectangles.add(j + 1, left.split());
                    }
                    break;
                } else if (j == rectangles.size() - 2) {
                    right.add(point);
                    if (right.size() == 6 * l) {
                        finalRectangles.add(left.merge(right));
                        rectangles.add(right.split());
                    }
                    break;
                }
            }
        }

//        System.out.println("Rectangle computation time: " + (System.currentTimeMillis() - startTime)/1000.0);

//        System.out.println("List size: " + yAbsoluteSortedList.size());
//        System.out.println("Num of rects: " + rectangles.size());
//        for (Rectangle rect: rectangles) {
//            System.out.println(rect.size());
//        }
//        System.out.println("Num of final rects: " + finalRectangles.size());
//        for (Rectangle rect: finalRectangles) {
//            System.out.println(rect.size());
//        }
//        System.out.println("runtime of computeRectangles: " + (System.currentTimeMillis() - startTime) / 1000.0);
        return finalRectangles;
    }
}

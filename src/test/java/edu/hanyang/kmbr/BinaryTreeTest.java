package edu.hanyang.kmbr;

import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.domain.PointSet;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

public class BinaryTreeTest {

//    @Test
    public void testInsertionWithSmallData() {
        BinaryTree tree = new BinaryTree();

        Random rand = new Random();

        for (int i = 0; i < 1000; i += 1) {
            Point p = new Point(i, rand.nextDouble(), rand.nextDouble());
            tree.add(p);

//            if ((i+1)%10 == 0) {
//                System.out.println("i: " + (i+1));
//                System.out.println("Tree:");
//                tree.printTree();
//                System.out.println("\nHeights:");
//                tree.printHeights();
//
//                System.out.println();
//                System.out.println();
//            }
        }

        tree.printTree();

        List<Integer> allPointSetIndex = tree.getDescentPointSetIndices(0);
        System.out.println(allPointSetIndex);
        for (int i: allPointSetIndex) {
            System.out.println("PointSet " + i);
            System.out.println("Sorted by x");
            System.out.println("Min x: " + tree.getPointSet(i).getMinX() + ", Max x: " + tree.getPointSet(i).getMaxX());
            for (Point p: tree.getPointSet(i).getXSortedPoints()) {
                System.out.println(p);
            }
//            System.out.println("Sorted by y");
//            for (Point p: PointSet.getYSortedPoints(i)) {
//                System.out.println(p);
//            }
            System.out.println();
        }
        double max = -1.0;
        for (int i = 0; i < allPointSetIndex.size(); i += 1) {
            double min = tree.getPointSet(allPointSetIndex.get(i)).getMinX();
            try {
                assertTrue("MAX: " + max + ", min value: " + min, max <= min);
            } catch (AssertionError exc) {
                System.out.println("Sorted by x (previous)");
                for (Point p: tree.getPointSet(allPointSetIndex.get(i - 1)).getXSortedPoints()) {
                    System.out.println(p);
                }
                System.out.println("Sorted by x (next)");
                for (Point p: tree.getPointSet(allPointSetIndex.get(i)).getXSortedPoints()) {
                    System.out.println(p);
                }
                tree.printTree();
                throw exc;
            }
            max = tree.getPointSet(allPointSetIndex.get(i)).getMaxX();
        }
    }

//     @Test
    public void testInsertionWithLargeData() {
        BinaryTree tree = new BinaryTree();

        Random rand = new Random();

        for (int i = 0; i < 30000; i += 1) {
            Point p = new Point(i, rand.nextInt(100000), rand.nextInt(100000));
            tree.add(p);
        }

        List<Integer> allPointSetIndex = tree.getDescentPointSetIndices(0);

//        System.out.println(allPointSetIndex);

        double max = -1;
        for (int i = 0; i < allPointSetIndex.size(); i += 1) {
            PointSet pointSet = tree.getPointSet(allPointSetIndex.get(i));
            double min = pointSet.getMinX();
            try {
                assertTrue("MAX: " + max + ", min value: " + min, max <= min);
            } catch (AssertionError exc) {
                System.out.println("Sorted by x (previous)");
                PointSet prevPointSet = tree.getPointSet(allPointSetIndex.get(i - 1));
                for (Point p: prevPointSet.getXSortedPoints()) {
                    System.out.println(p);
                }
                System.out.println("Sorted by x (next)");
                for (Point p: pointSet.getXSortedPoints()) {
                    System.out.println(p);
                }
                tree.printTree();
                throw exc;
            }
            max = pointSet.getMaxX();
        }
    }

    @Test
    public void testDeletion() {
        BinaryTree tree = new BinaryTree();
        Random rand = new Random();

        final int N = 300;

        Point[] Points = new Point[N];

        for (int i = 0; i < N; i += 1) {
            Point p = new Point(i, rand.nextDouble(), rand.nextDouble());
            Points[i] = p;
            tree.add(p);
        }

        tree.printTree();
        tree.printHeights();

        for (int i = 0; i < N; i += 1) {
            tree.remove(Points[i]);

            try {
                tree.printTree();
                tree.printHeights();
            } catch (IndexOutOfBoundsException exc) {
                System.out.println(tree.treeSize());
                System.out.println(tree.heightSize());
                throw exc;
            }
        }
    }
}

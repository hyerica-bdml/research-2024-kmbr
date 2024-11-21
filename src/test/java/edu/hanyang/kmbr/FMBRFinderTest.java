//package edu.hanyang.kmbr;
//
//import edu.hanyang.kmbr.domain.Point;
//import edu.hanyang.kmbr.utils.MBR;
//import org.junit.Test;
//
//import java.io.*;
//import java.util.*;
//
//public class FMBRFinderTest {
//
//    @Test
//    public void addPointTest() {
//        int NUM_OF_POINT = 300;
//
//        KMBRFinder finder = new KMBRFinder();
//        Random random = new Random(System.currentTimeMillis());
//
//        for (int i = 0; i < NUM_OF_POINT; i += 1) {
//            System.out.println("\n\n\ni: " + i);
//            double x = random.nextDouble();
//            double y = random.nextDouble();
//
//            Point p = new Point(i, x, y);
//            finder.addPoint(p);
//            finder.printTree();
////            printAlphaMap(finder);
//        }
//    }
//
//    @Test
//    public void addPointTest2() {
//        int NUM_OF_POINT = 300;
//
//        KMBRFinder finder = new KMBRFinder();
//        Random random = new Random(System.currentTimeMillis());
//
//        for (int i = 0; i < NUM_OF_POINT; i += 1) {
////            System.out.println("\n\n\ni: " + i);
//            int shift = i/(NUM_OF_POINT/10);
//
//            double x = shift + random.nextDouble();
//            double y = shift + random.nextDouble();
//
//            Point p = new Point(i, x, y);
//            finder.addPoint(p);
//
//            finder.printTree();
//            finder.printHeights();
////            printAlphaMap(finder);
//        }
//    }
//
//    @Test
//    public void deletePointTest() {
//        int NUM_OF_POINT = 300;
//
//        KMBRFinder finder = new KMBRFinder();
//        Random random = new Random(System.currentTimeMillis());
//
//        Point[] Points = new Point[NUM_OF_POINT];
//
//        for (int i = 0; i < NUM_OF_POINT; i += 1) {
////            System.out.println("\n\n\ni: " + i);
//            int shift = i/(NUM_OF_POINT/10);
//
//            double x = shift + random.nextDouble();
//            double y = shift + random.nextDouble();
//
//            Point p = new Point(i, x, y);
//            finder.addPoint(p);
//            Points[i] = p;
//
//            finder.printTree();
//            finder.printHeights();
////            tree.printTree();
////            printAlphaMap(finder);
//        }
//        finder.printTree();
//        finder.printHeights();
//
//        for (int i = 0; i < NUM_OF_POINT; i += 1) {
//            finder.removePoint(Points[i]);
//
////            tree.printTree();
////            tree.printHeights();
////            printAlphaMap(finder);
//        }
//        System.out.println("RESULT:");
//        finder.printTree();
//        finder.printHeights();
//    }
//
//    @Test
//    public void movePointTest() {
//        int NUM_OF_POINT = 300;
//
//        KMBRFinder finder = new KMBRFinder();
//        Random random = new Random(System.currentTimeMillis());
//
//        List<Point> Points = new LinkedList<>();
//
//        for (int i = 0; i < NUM_OF_POINT; i += 1) {
//            int shift = i/(NUM_OF_POINT/10);
//
//            double x = shift*3 + random.nextDouble();
//            double y = shift*3 + random.nextDouble();
//
//            Point p = new Point(i, x, y);
//            finder.addPoint(p);
//            Points.add(p);
//        }
//
//        finder.printTree();
//        finder.printHeights();
//
//        random.setSeed(System.currentTimeMillis());
//
//        MBR mbr = null;
//
//        for (int j = 0; j < 30; j += 1) {
//            Point originalPoint = Points.get(random.nextInt(NUM_OF_POINT));
//
//            long pid = originalPoint.getId();
//            double x = originalPoint.getX();
//            double y = originalPoint.getY();
//
//            double xOffset = (random.nextDouble() - 0.5) * 0.5;
//            double yOffset = (random.nextDouble() - 0.5) * 0.5;
//
////            Point newPoint = new Point(pid, x + xOffset, y + yOffset);
////
////            finder.movePoint(originalPoint, newPoint);
//
//            finder.movePoint(originalPoint, x + xOffset, y + yOffset);
//
//            finder.printTree();
//            finder.printHeights();
//        }
//
//        long currentTime = System.currentTimeMillis();
//        mbr = finder.find();
//        double time = ((double) (System.currentTimeMillis() - currentTime))/1000;
//        System.out.println("Computing time: " + time + " secs.");
//    }
//
//    @Test
//    public void globalPointMoveTest() {
//        Random random = new Random(System.currentTimeMillis());
//
//        List<Point> Points = new LinkedList<>();
//        int num_of_points = 300;
//
//        for (int i = 0; i < num_of_points; i += 1) {
//            int shift = i/(num_of_points/10);
//
//            double x = shift*3 + random.nextDouble();
//            double y = shift*3 + random.nextDouble();
//
//            Point p = new Point(i, x, y);
//            Points.add(p);
//        }
//
//        KMBRFinder finder = new KMBRFinder((Point[]) Points.toArray());
//
//        MBR mbr = null;
//
//        double[] timeArray = new double[100];
//
//        for (int i = 0; i < timeArray.length; i += 1) {
//            for (int j = 0; j < 500; j += 1) {
//                Point originalPoint = Points.get(random.nextInt(Config.NUM_OF_POINTS));
//
//                long pid = originalPoint.getId();
//                double x = originalPoint.getX();
//                double y = originalPoint.getY();
//
//                double xOffset = (random.nextDouble() - 0.5) * 20;
//                double yOffset = (random.nextDouble() - 0.5) * 20;
//
////                Point newPoint = new Point(pid, x + xOffset, y + yOffset);
////                finder.movePoint(originalPoint, newPoint);
//                finder.movePoint(originalPoint, x + xOffset, y + yOffset);
//            }
//
//            long currentTime = System.currentTimeMillis();
//            mbr = finder.find();
//            double time = ((double) (System.currentTimeMillis() - currentTime))/1000;
//            System.out.println("Computing time: " + time + " secs.");
//
//            timeArray[i] = time;
//        }
//
//        double avgTime = 0.0;
//        for (int i = 0; i < timeArray.length; i += 1) {
//            avgTime += timeArray[i] / timeArray.length;
//        }
//
//        System.out.println("Average computing time: " + avgTime);
//
////        writeKMBR(mbr);
////        writePointSet(finder.getTree());
//    }
//
//    @Test
//    public void localPointMoveTest() {
//        KMBRFinder finder = KMBRFinder.load();
//        List<Point> Points = PointUtilities.loadPoints();
//
//        Random random = new Random();
//        random.setSeed(System.currentTimeMillis());
//
//        MBR mbr = null;
//        double[] timeArray = new double[100];
//
//        for (int i = 0; i < timeArray.length; i += 1) {
//            int numOfPointsInGroup = Config.NUM_OF_POINTS/Config.NUM_OF_GROUPS;
//            int group = random.nextInt(Config.NUM_OF_GROUPS);
//
//            for (int j = 0; j < 500; j += 1) {
//                Point originalPoint = Points.get(group*numOfPointsInGroup + random.nextInt(numOfPointsInGroup));
//
//                int pid = originalPoint.getId();
//                double x = originalPoint.getX();
//                double y = originalPoint.getY();
//
//                double xOffset = (random.nextDouble() - 0.5) * 3;
//                double yOffset = (random.nextDouble() - 0.5) * 3;
//
////                Point newPoint = new Point(pid, x + xOffset, y + yOffset);
////                finder.movePoint(originalPoint, newPoint);
//                finder.movePoint(originalPoint, x + xOffset, y + yOffset);
//            }
//
//            long currentTime = System.currentTimeMillis();
//            mbr = finder.find();
//            double time = ((double) (System.currentTimeMillis() - currentTime))/1000;
//            System.out.println("Computing time: " + time + " secs.");
//
//            timeArray[i] = time;
//        }
//
//        double avgTime = 0.0;
//        for (int i = 0; i < timeArray.length; i += 1) {
//            avgTime += timeArray[i] / timeArray.length;
//        }
//
//        System.out.println("Average computing time: " + avgTime);
//
////        writeKMBR(mbr);
////        writePointSet(finder.getTree());
//    }
//
////    @Test
////    public void movePointTest() {
////        int NUM_OF_POINT = 3000;
////
////        BinaryTree tree = new BinaryTree();
////        KMBRFinder finder = new KMBRFinder(tree);
////        Random random = new Random(System.currentTimeMillis());
////
////        Point[] points = new Point[NUM_OF_POINT];
////
////        for (int i = 0; i < NUM_OF_POINT; i += 1) {
//////            System.out.println("\n\n\ni: " + i);
////            int shift = i%10;
////
////            double x = shift + random.nextDouble();
////            double y = shift + random.nextDouble();
////
////            Point p = new Point(i, x, y);
////            finder.addPoint(p);
////            points[i] = p;
////
//////            tree.printTree();
//////            printAlphaMap(finder);
////        }
////
////        long startTime = System.currentTimeMillis();
////        MBR mbr = finder.find();
////        System.out.println("Computing time: " + (double)(System.currentTimeMillis() - startTime)/1000 + ".");
////
////        for (int i = 0; i < 50; i += 10) {
////            Point oldPoint = points[i];
////            Point newPoint = new Point(i, oldPoint.getX() + 0.1, oldPoint.getY() + 0.1);
////            finder.movePoint(oldPoint, newPoint);
////
//////            tree.printTree();
//////            printAlphaMap(finder);
////        }
////
////        startTime = System.currentTimeMillis();
////        mbr = finder.find();
////        System.out.println("Computing time: " + (double)(System.currentTimeMillis() - startTime)/1000 + ".");
////        System.out.println(mbr.index + ", " + mbr.getSize());
////        for (Point p: mbr.points) {
////            System.out.println("(" + p.getX() + ", " + p.getY() + ")");
////        }
////
////        for (int i = 7; i < 200; i += 10) {
////            Point oldPoint = points[i];
////            Point newPoint = new Point(i, oldPoint.getX() + 0.1, oldPoint.getY() + 0.1);
////            finder.movePoint(oldPoint, newPoint);
////
//////            tree.printTree();
//////            printAlphaMap(finder);
////        }
////
////        startTime = System.currentTimeMillis();
////        mbr = finder.find();
////        System.out.println("Computing time: " + (double)(System.currentTimeMillis() - startTime)/1000 + ".");
////        System.out.println(mbr.index + ", " + mbr.getSize());
////        for (Point p: mbr.points) {
////            System.out.println("(" + p.getX() + ", " + p.getY() + ")");
////        }
////    }
//
//    @Test
//    public void computeMBRTest() {
//        Map<Integer, Point> points = readPoints();
//
//        System.out.println("Creating a binary tree...");
//        BinaryTree tree = new BinaryTree();
//
//        System.out.println("Inserting data into tree...");
//        for (Integer key: points.keySet()) {
//            tree.add(points.get(key));
//        }
//        System.out.println("Computing MBR...");
//        long startTime = System.currentTimeMillis();
//        KMBRFinder finder = new KMBRFinder(tree);
//        System.out.println("Computing time: " + (double)(System.currentTimeMillis() - startTime)/1000 + ".");
//
//        MBR mbr = finder.find();
//        System.out.println(mbr.index + ", " + mbr.size());
////        for (Point p: mbr.points) {
////            System.out.println("(" + p.getX() + ", " + p.getY() + ")");
////        }
//
//        writeKMBR(mbr);
//        writePointSet(tree);
//
//        KMBRFinder.save(finder);
//    }
//
//    @Test
//    public void computeMBRTest2() {
//        int[] numOfPointsArr = new int[] {
//                1000, 5000, 10000, 30000, 50000, 100000
//        };
//
//        DatabaseApp dbApp = new DatabaseApp();
//        KMBRApp kmbrApp = new KMBRApp();
//
//        for (int numOfPoints: numOfPointsArr) {
//            List<Point> Points = PointUtilities.generatePoints(numOfPoints);
//
//            BinaryTree tree = new BinaryTree();
//
//            for (Point p : Points) {
//                tree.add(p);
//            }
//            long startTime = System.currentTimeMillis();
//            KMBRFinder finder = new KMBRFinder(tree);
//            System.out.println("Computing time: " + (double) (System.currentTimeMillis() - startTime) / 1000 + " (N=" + numOfPoints + ").");
//
//            MBR mbr = finder.find();
//        }
//    }
//
//    protected Map<Integer, Point> readPoints() {
//        Map<Integer, Point> points = new HashMap<>();
//
//        System.out.println("Reading file...");
//        try (FileInputStream fin = new FileInputStream("data/points/points.txt");
//             InputStreamReader in = new InputStreamReader(fin);
//             BufferedReader reader = new BufferedReader(in)) {
//
//            final int numPoints = Integer.parseInt(reader.readLine().trim());
//
//            for (int i = 0; i < numPoints; i += 1) {
//                String[] splited = reader.readLine().split(" ");
//                int id = Integer.parseInt(splited[0]);
//                double x = Double.parseDouble(splited[1]);
//                double y = Double.parseDouble(splited[2]);
//
//                points.put(id, new Point(id, x, y));
//            }
//
//        } catch (IOException exc) {
//            exc.printStackTrace();
//            System.exit(1);
//        }
//
//        return points;
//    }
//
//    protected void writeKMBR(final MBR mbr) {
//        try (FileOutputStream fout = new FileOutputStream("data/points/kmbr.txt");
//             OutputStreamWriter out = new OutputStreamWriter(fout);
//             BufferedWriter writer = new BufferedWriter(out)) {
//
//            writer.write(String.format("%d\n", mbr.Points.length));
//
//            for (Point p: mbr.Points) {
//                int id = p.id;
//                double x = p.getX();
//                double y = p.getY();
//
//                writer.write(String.format("%d %f %f\n", id, x, y));
//            }
//
//        } catch (IOException exc) {
//            exc.printStackTrace();
//            System.exit(1);
//        }
//    }
//
////    protected void printAlphaMap(final KMBRFinder finder) {
////        System.out.println("AlphaMap:");
////        for (int key: finder.alphaMap.keySet()) {
////            System.out.printf("(%d, %f) ", key, finder.alphaMap.get(key));
////        }
////        System.out.println();
////    }
//
//    protected void writePointSet(final BinaryTree tree) {
//        List<Integer> allPointSetIndex = tree.getDescentPointSetIndices(0);
////        System.out.println(allPointSetIndex);
//
//        try (FileOutputStream fout = new FileOutputStream("data/points/pointset_assginment.txt");
//             OutputStreamWriter out = new OutputStreamWriter(fout);
//             BufferedWriter writer = new BufferedWriter(out)) {
//
//            for (int pointSetIndex : allPointSetIndex) {
//                PointSet PointSet = tree.getPointSet(pointSetIndex);
//                writer.write(pointSetIndex + "\n");
//                writer.write(PointSet.getXSortedPoints()[0].getX() + " " + PointSet.getXSortedPoints()[PointSet.length() - 1].getX() + "\n");
//
////                System.out.println("PointSet " + pointSetIndex);
////                System.out.println("Sorted by x");
////                System.out.println("Min x: " + PointSetUtilities.getMinX(pointSetIndex) + ", Max x: " + PointSetUtilities.getMaxX(pointSetIndex));
//
//                for (Point p : PointSet.getXSortedPoints()) {
//                    if (p == null) break;
//                    writer.write(p.id + " ");
////                    System.out.print(p + " ");
//                }
//                writer.write("\n");
////            System.out.println("Sorted by y");
////            for (Point p: PointSet.getYSortedPoints(i)) {
////                System.out.println(p);
////            }
////                System.out.println();
//            }
//        } catch (IOException exc) {
//            exc.printStackTrace();
//            System.exit(1);
//        }
//    }
//}

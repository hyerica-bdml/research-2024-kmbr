package edu.hanyang.kmbr;

import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.domain.PointSet;
import edu.hanyang.kmbr.utils.*;
import edu.hanyang.kmbr.Config;

import java.io.*;
import java.util.*;

public class KMBRFinder implements Externalizable {

    private static final long serialVersionUID = -4818833800729901709L;

    protected BinaryTree tree;
//    protected DynamicArray<MBRResult> mbrCache;
    protected Map<Integer, MBRResult> mbrCache;
    protected Map<Integer, Integer> cacheBits;
    protected Map<Integer, Double> alphas;

    protected double alphaPlusBeta;

    public MBR globalMBR;

    public KMBRFinder() {
        this.mbrCache = new TreeMap<>();
        this.cacheBits = new TreeMap<>();
        this.alphas = new TreeMap<>();
        this.globalMBR = null;
        this.alphaPlusBeta = Config.INITIAL_ALPHA*2;
//        this.alphaPlusBeta = 1;
        this.tree = new BinaryTree();

        if (Config.useCache) {
            Map<Integer, PointSet> pointSets = tree.getPointSets();

            for (int key: pointSets.keySet())
                alphas.put(key, Config.INITIAL_ALPHA);

//            updateCacheBits();
        }
    }

    public KMBRFinder(final Point[] points) {
        this.mbrCache = new TreeMap<>();
        this.cacheBits = new TreeMap<>();
        this.alphas = new TreeMap<>();
        this.globalMBR = null;
        this.alphaPlusBeta = Config.INITIAL_ALPHA*2;
//        this.alphaPlusBeta = 1;
        this.tree = new BinaryTree(points);

        if (Config.useCache) {
            Map<Integer, PointSet> pointSets = tree.getPointSets();

            for (int key: pointSets.keySet())
                alphas.put(key, Config.INITIAL_ALPHA);

//            updateCacheBits();
        }
    }

    public MBR find() {
        if (Config.useCache && mbrCache.getOrDefault(0, null) != null)
            return mbrCache.get(0).getMBR();

        MBRResult result;

        if (Config.useCache) {
            if (cacheBits.get(0) == 1) {
                result = computeMBR(0, true);
                mbrCache.put(0, result);
            }
            else {
                result = computeMBR(0, false);
            }
        }
        else {
            result = computeMBR(0, true);
        }

        globalMBR = result.getMBR();
        return globalMBR;
    }

    public int search(final Point p) {
        int treeIndex = tree.search(p, 0);
        return treeIndex;
    }

    public double getValue(final int treeIndex) {
        return tree.getValue(treeIndex);
    }

    public PointSet getPointSet(final int pointSetIndex) {
        return tree.getPointSet(pointSetIndex);
    }

    public boolean isLeaf(final int treeIndex) {
        return tree.isLeaf(treeIndex);
    }

    public void addPoint(final Point p) {
        int treeIndex = tree.search(p, 0);
        addPoint(p, treeIndex);
    }

    public void addPoint(final Point p, final int treeIndex) {
        if (Config.useCache) {
            int currentTreeIndex = treeIndex;
            while (currentTreeIndex > -1) {
                removeCache(currentTreeIndex);
                currentTreeIndex = tree.getParent(currentTreeIndex);
            }
        }

        int originalPointSetIndex = (int) tree.getValue(treeIndex);
        int newPointSetIndex = tree.add(p, treeIndex);

        if (Config.useCache) {
            int currentTreeIndex = tree.search(p);
            while (currentTreeIndex > -1) {
                removeCache(currentTreeIndex);
                currentTreeIndex = tree.getParent(currentTreeIndex);
            }

            // posterior update
//            double alphaForOriginalPointSet = alphas.get(originalPointSetIndex) + 1;
            double alphaForOriginalPointSet = alphas.get(originalPointSetIndex);
            alphaForOriginalPointSet += (alphaPlusBeta - alphaForOriginalPointSet)/10;
//            alphaForOriginalPointSet += (alphaPlusBeta - alphaForOriginalPointSet)/2;
            alphas.put(originalPointSetIndex, alphaForOriginalPointSet);
            if (newPointSetIndex >= 0) {
                alphas.put(newPointSetIndex, alphaForOriginalPointSet);
            }

            alphaPlusBeta += 1;
        }

//        if (treeIndex != newTreeIndex && Config.useCache) {
//            updateCacheBits();
//        }

        normalizeAlphas();
    }

    public int treeSize() {
        return tree.treeSize();
    }

    public void removePoint(Point p) {
        int treeIndex = tree.search(p, 0);
        removePoint(p, treeIndex);
    }

    public void removePoint(Point p, int treeIndex) {
        if (Config.useCache) {
            int currentTreeIndex = treeIndex;
            while (currentTreeIndex > -1) {
                removeCache(currentTreeIndex);
                currentTreeIndex = tree.getParent(currentTreeIndex);
            }
        }

        int pointSetIndex = (int) tree.getValue(treeIndex);
        int removedPointSet = tree.remove(p, treeIndex);

        if (Config.useCache) {
            int currentTreeIndex = tree.search(p);
            while (currentTreeIndex > -1) {
                if (getCache(currentTreeIndex) != null)
                    removeCache(currentTreeIndex);

                currentTreeIndex = tree.getParent(currentTreeIndex);
            }

            // posterior update
            if (tree.getPointSet(pointSetIndex) != null) {
                double alpha = alphas.get(pointSetIndex);
                alpha += (alphaPlusBeta - alpha)/10;
//                alphas.put(pointSetIndex, alpha + 1);
                alphas.put(pointSetIndex, alpha);
            }
            else {
                alphas.remove(pointSetIndex);
            }
            alphaPlusBeta += 1;

            if (removedPointSet >= 0) {
                alphas.remove(removedPointSet);
            }
        }

        normalizeAlphas();
    }

    public void movePoint(final Point p, final double x, final double y) {
        int beforeTreeIndex = tree.search(p, 0);
        int beforePointSetIndex = (int) tree.getValue(beforeTreeIndex);

        if (Config.useCache) {
            int currentTreeIndex = beforeTreeIndex;
            while (currentTreeIndex > -1) {
                removeCache(currentTreeIndex);
                currentTreeIndex = tree.getParent(currentTreeIndex);
            }
        }

        int removedPointSetIndex = tree.remove(p, beforeTreeIndex);
        p.set(x, y);
        int afterTreeIndex = tree.search(p);
        int afterPointSetIndex = tree.add(p, afterTreeIndex);

        if (Config.useCache) {
            int currentTreeIndex = afterTreeIndex;
            while (currentTreeIndex > -1) {
                removeCache(currentTreeIndex);
                currentTreeIndex = tree.getParent(currentTreeIndex);
            }

            double alpha = alphas.get(beforePointSetIndex);
            alpha += (alphaPlusBeta - alpha) / 10;
            alphas.put(beforePointSetIndex, alpha);
            if (afterPointSetIndex >= 0)
                alphas.put(afterPointSetIndex, alpha);
            alphaPlusBeta += 1;

            if (removedPointSetIndex >= 0) alphas.remove(removedPointSetIndex);
        }

        normalizeAlphas();
    }

    public void normalizeAlphas() {
        if (alphaPlusBeta >= 10000) {
            for (int key : alphas.keySet()) {
                double alpha = alphas.get(key);
                alphas.put(key, alpha/100);
            }
            alphaPlusBeta /= 100;
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(tree);
        out.writeObject(mbrCache);
        out.writeObject(cacheBits);
        out.writeObject(alphas);
        out.writeObject(globalMBR);
        out.writeDouble(alphaPlusBeta);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tree = (BinaryTree) in.readObject();
        mbrCache = (Map<Integer, MBRResult>) in.readObject();
        cacheBits = (Map<Integer, Integer>) in.readObject();
        alphas = (Map<Integer, Double>) in.readObject();
        globalMBR = (MBR) in.readObject();
        alphaPlusBeta = in.readDouble();
    }

    public void printDirtyProbs() {
        Map<Integer, PointSet> pointSets = tree.getPointSets();
        List<Integer> keySet = new ArrayList<>(pointSets.keySet());

        keySet.sort((k1, k2) -> Double.compare(getDirtyProbability(k1), getDirtyProbability(k2)));

        System.out.println("===== PointSets =====");
        for (int key: keySet) {
            System.out.println(String.format("Dirty probability of PointSet %d: %f", key, getDirtyProbability(key), alphas.get(key)));
        }
        System.out.println("alpha + beta: " + alphaPlusBeta);
        System.out.println("=====================");
    }

    public void printCacheBits(int root) {
        int currentRowLength = 1;
        int count = 0;

        if (cacheBits.size() == 0) return;

        List<Integer> queue = new LinkedList<>();
        queue.add(root);

        System.out.println("=====");
        while (queue.size() > 0) {
            int index = queue.get(0);
            queue.remove(0);

            double value = cacheBits.getOrDefault(index, -1);

            if (value == -1)
                System.out.print("-1 ");
            else
                System.out.print(value + " ");

            count += 1;
            if (count == currentRowLength) {
                count = 0;
                currentRowLength *= 2;
                System.out.println();
            }

            if (tree.getLeftChild(index) < cacheBits.size()) {
                queue.add(tree.getLeftChild(index));
                queue.add(tree.getRightChild(index));
            }
        }
        System.out.println("\n=====");
    }

    public void updateCacheBits() {
        double[] dirtyProbArray = new double[tree.treeSize()];
        double[] timeCostArray = new double[tree.treeSize()];
        double[] timeCostArrayWithoutCaching = new double[tree.treeSize()];
        int[] numOfPointsArray = new int[tree.treeSize()];

        for (int treeIndex = dirtyProbArray.length - 1; treeIndex >= 0; treeIndex -= 1) {
            if (tree.exists(treeIndex)) {
                if (tree.isLeaf(treeIndex)) {
                    int pointSetIndex = (int) tree.getValue(treeIndex);
//                    System.out.println("UPDATE CACHE BITS: " + pointSetIndex);
                    double dirtyProb = getDirtyProbability(pointSetIndex);
                    dirtyProbArray[treeIndex] = dirtyProb;
                    numOfPointsArray[treeIndex] = Config.K;

                    cacheBits.put(treeIndex, 1);
                    timeCostArray[treeIndex] = 1;
//                    timeCostArrayWithoutCaching[treeIndex] = Config.K^3;
                    timeCostArrayWithoutCaching[treeIndex] = 1;
                } else {
                    int leftChild = tree.getLeftChild(treeIndex);
                    int rightChild = tree.getRightChild(treeIndex);

                    double leftDirtyProb = dirtyProbArray[leftChild];
                    double rightDirtyProb = dirtyProbArray[rightChild];

                    double dirtyProb = 1 - (1 - leftDirtyProb) * (1 - rightDirtyProb);
                    dirtyProbArray[treeIndex] = dirtyProb;

                    double leftTimeCost = timeCostArray[leftChild];
                    double leftTimeCostWithoutCaching = timeCostArrayWithoutCaching[leftChild];

                    double rightTimeCost = timeCostArray[rightChild];
                    double rightTimeCostWithoutCaching = timeCostArrayWithoutCaching[rightChild];

                    int leftNumOfPoints = numOfPointsArray[leftChild];
                    int rightNumOfPoints = numOfPointsArray[rightChild];
                    int numOfPoints = leftNumOfPoints + rightNumOfPoints;
                    numOfPointsArray[treeIndex] = numOfPoints;

//                    double timeCostWithChildrenCaching = leftTimeCost + rightTimeCost + numOfPoints;
//                    double timeCostWithoutChildrenCaching = leftTimeCostWithoutCaching + rightTimeCostWithoutCaching + numOfPoints;
//                    double mergeCost = Config.K * Config.K * numOfPoints;
                    double timeCostWithChildrenCaching = leftTimeCost + rightTimeCost + numOfPoints;
                    double timeCostWithoutChildrenCaching = leftTimeCostWithoutCaching + rightTimeCostWithoutCaching + numOfPoints;

                    double timeCostExpectationWithCaching = dirtyProb*timeCostWithoutChildrenCaching + (1 - dirtyProb);

                    // caching
                    // System.out.println("COST: " + timeCostWithChildrenCaching + ", " + timeCostExpectationWithCaching);
                    if (timeCostWithChildrenCaching > timeCostExpectationWithCaching) {
                        cacheBits.put(treeIndex, 1);
                        timeCostArray[treeIndex] = timeCostExpectationWithCaching;
                    } else {
                        cacheBits.put(treeIndex, 0);
                        timeCostArray[treeIndex] = timeCostWithChildrenCaching;
                    }
                    timeCostArrayWithoutCaching[treeIndex] = timeCostWithoutChildrenCaching;
                }
            }
        }
    }

    public void printTree() {
        tree.printTree();
    }

    public void printTree(final int treeIndex) {
        tree.printTree(treeIndex);
    }

    public void printHeights() {
        tree.printHeights();
    }

    public void printHeights(final int treeIndex) {
        tree.printHeights(treeIndex);
    }

    public void printPointSets() {
        tree.printPointSets();
    }

    public void printCacheMemory() {
        printCacheMemory("basic");
    }

    public void printCacheMemory(String type) {
        int[] numOfPointsInEachNode = new int[tree.treeSize()];
        long numOfBytes = 0;

        for (int currentNodeIndex = tree.treeSize() - 1; currentNodeIndex >= 0; currentNodeIndex -= 1) {
            if (tree.exists(currentNodeIndex)) {
                if (tree.isLeaf(currentNodeIndex)) {
                    int pointSetIndex = (int) tree.getValue(currentNodeIndex);
                    numOfPointsInEachNode[currentNodeIndex] = tree.getPointSet(pointSetIndex).size();
                } else {
                    int leftChildIndex = tree.getLeftChild(currentNodeIndex);
                    int rightChildIndex = tree.getRightChild(currentNodeIndex);

                    numOfPointsInEachNode[currentNodeIndex] = 0;

                    if (tree.exists(leftChildIndex))
                        numOfPointsInEachNode[currentNodeIndex] += numOfPointsInEachNode[leftChildIndex];
                    if (tree.exists(rightChildIndex))
                        numOfPointsInEachNode[currentNodeIndex] += numOfPointsInEachNode[rightChildIndex];

                    if (type.equals("all") || (mbrCache.size() > currentNodeIndex) && mbrCache.get(currentNodeIndex) != null) {
                        numOfBytes += 16L * 2 * numOfPointsInEachNode[currentNodeIndex];
                    }
                }
            }
        }

        double memory = (double) numOfBytes / Math.pow(2, 20);
        System.out.printf("Memory consumption of caching: %f MB (%d bytes)\n", memory, numOfBytes);
    }

    public int getNumOfPoints() {
        int num = 0;
        for (int key: tree.getPointSets().keySet()) {
            num += tree.getPointSet(key).size();
        }
        return num;
    }

    public void printCache() {
        System.out.println("===== Cache =====");
        System.out.println("Number of cache: " + mbrCache.size());
        for (int key: mbrCache.keySet()) {
            MBRResult cache = mbrCache.get(key);

            if (cache != null) {
                System.out.println(key + " th node had been cached.");
            }
        }
        System.out.println("=================");
    }

    /**
     *
     * @param treeIndex kMBR 계산을 위한 트리의 루트노드(sub-root node)
     * @param noCache 이 노드 아래의 노드는 캐싱하지 않을때 true
     * @return
     */
    protected MBRResult computeMBR(final int treeIndex, final boolean noCache) {
        MBRResult result;

        if (Config.useCache) {
            result = mbrCache.getOrDefault(treeIndex, null);
            int cacheBit = cacheBits.getOrDefault(treeIndex, 0);
            if (result == null) {
                if (tree.isLeaf(treeIndex)) {
                    result = computeMBRInLeaf(treeIndex);

                    if (!noCache && cacheBit == 1)
                        mbrCache.put(treeIndex, result);
                }
                else {
                    int leftChild = tree.getLeftChild(treeIndex);
                    int rightChild = tree.getRightChild(treeIndex);

                    if (!noCache) {
//                        System.out.println("caching " + treeIndex);
                        if (cacheBit == 1) {
                            // 이 노드를 캐싱하면 아래 children은 캐싱할 필요가 없음
                            MBRResult leftResult = computeMBR(leftChild, true);
                            MBRResult rightResult = computeMBR(rightChild, true);

                            result = computeMBRInNonLeaf(treeIndex, leftResult, rightResult);
                            mbrCache.put(treeIndex, result);
                        } else {
                            MBRResult leftResult = computeMBR(leftChild, false);
                            MBRResult rightResult = computeMBR(rightChild, false);

                            result = computeMBRInNonLeaf(treeIndex, leftResult, rightResult);
                        }
                    } else {
//                        System.out.println("no caching " + treeIndex);
                        MBRResult leftResult = computeMBR(leftChild, true);
                        MBRResult rightResult = computeMBR(rightChild, true);

                        result = computeMBRInNonLeaf(treeIndex, leftResult, rightResult);
                    }
                }
            }
        }
        else {
            if (tree.isLeaf(treeIndex)) {
                result = computeMBRInLeaf(treeIndex);
            }
            else {
                int leftChild = tree.getLeftChild(treeIndex);
                int rightChild = tree.getRightChild(treeIndex);

                MBRResult leftResult = computeMBR(leftChild, true);
                MBRResult rightResult = computeMBR(rightChild, true);

                result = computeMBRInNonLeaf(treeIndex, leftResult, rightResult);
            }
        }

        return result;
    }

    protected MBRResult getCache(final int treeIndex) {
        if (mbrCache.size() <= treeIndex || mbrCache.getOrDefault(treeIndex, null) == null)
            return null;

        return mbrCache.get(treeIndex);
    }

    protected void removeCache(int treeIndex) {
        mbrCache.remove(treeIndex);
    }

    protected MBRResult computeMBRInLeaf(final int treeIndex) {
//        System.out.println("LEAF");
        int pointSetIndex = (int) tree.getValue(treeIndex);
        PointSet pointSet = tree.getPointSet(pointSetIndex);

        Point[] xSortedPoints = Arrays.copyOf(pointSet.getXSortedPoints(), pointSet.size());
        Point[] ySortedPoints = Arrays.copyOf(pointSet.getYSortedPoints(), pointSet.size());

        if (pointSet.size() < Config.K) return new MBRResult(null, xSortedPoints, ySortedPoints);
        else if (pointSet.size() == Config.K) return new MBRResult(new MBR(xSortedPoints), xSortedPoints, ySortedPoints);
//        else throw new RuntimeException("[computeMBRInLeaf] INVALID point set size!");
        else return new MBRResult(Utilities.computeMBR(xSortedPoints, ySortedPoints), xSortedPoints, ySortedPoints);

//        MBR mbr = Utilities.computeMBR(xSortedPoints, ySortedPoints);
//        System.out.println("x range: (" + xSortedPoints[0].getX() + ", " + xSortedPoints[xSortedPoints.length - 1].getX() + ")");
//        System.out.println("y range: (" + ySortedPoints[0].getY() + ", " + ySortedPoints[ySortedPoints.length - 1].getY() + ")");
//        System.out.println("LEAF minMBR size: " + mbr.size());
//        return new MBRResult(mbr, xSortedPoints, ySortedPoints);
    }

    protected MBRResult computeMBRInNonLeaf(final int treeIndex, final MBRResult leftMBR, final MBRResult rightMBR) {
//        System.out.println("NON LEAF");
        Point[] xSortedPointsOfLeft = leftMBR.getXSortedPoints();
        Point[] xSortedPointsOfRight = rightMBR.getXSortedPoints();

        Point[] xMerged = new Point[xSortedPointsOfLeft.length + xSortedPointsOfRight.length];
        System.arraycopy(xSortedPointsOfLeft, 0, xMerged, 0, xSortedPointsOfLeft.length);
        System.arraycopy(xSortedPointsOfRight, 0, xMerged, xSortedPointsOfLeft.length, xSortedPointsOfRight.length);

        Point[] ySortedPointsOfLeft = leftMBR.getYSortedPoints();
        Point[] ySortedPointsOfRight = rightMBR.getYSortedPoints();

        Point[] yMerged = new Point[ySortedPointsOfLeft.length + ySortedPointsOfRight.length];
        int index1 = 0, index2 = 0;
        for (int i = 0; i < yMerged.length; i += 1) {
            if (index1 < ySortedPointsOfLeft.length && index2 < ySortedPointsOfRight.length) {
                if (ySortedPointsOfLeft[index1].getY() < ySortedPointsOfRight[index2].getY()) {
                    yMerged[i] = ySortedPointsOfLeft[index1];
                    index1 += 1;
                }
                else {
                    yMerged[i] = ySortedPointsOfRight[index2];
                    index2 += 1;
                }
            }
            else if (index1 < ySortedPointsOfLeft.length) {
                yMerged[i] = ySortedPointsOfLeft[index1];
                index1 += 1;
            }
            else if (index2 < ySortedPointsOfRight.length) {
                yMerged[i] = ySortedPointsOfRight[index2];
                index2 += 1;
            }
        }

        MBR minMBR = null;

        if (leftMBR.getMBR() != null && rightMBR.getMBR() != null) {
            if (leftMBR.getMBR().size() < rightMBR.getMBR().size())
                minMBR = leftMBR.getMBR();
            else
                minMBR = rightMBR.getMBR();
        }
        else if (leftMBR.getMBR() != null) {
            minMBR = leftMBR.getMBR();
        }
        else if (rightMBR.getMBR() != null) {
            minMBR = rightMBR.getMBR();
        }

        // compute y sorted points only in minMBR band
        if (minMBR != null) {
//            System.out.println("NON LEAF - minMBR exists");

            int size = 0;
            double middleX = (xSortedPointsOfLeft[xSortedPointsOfLeft.length - 1].getX() + xSortedPointsOfRight[0].getX())/2;

//            double range = globalMBR.size()/2;
            double range = minMBR.size()/2;

            Point[] ySubMerged = new Point[yMerged.length];
            for (int i = 0; i < yMerged.length; i += 1)
                if (yMerged[i].getX() > middleX - range && yMerged[i].getX() < middleX + range)
                    ySubMerged[size++] = yMerged[i];

//            System.out.println("Len of points: " + xMerged.length + ", MERGE size: " + size + ", minMBR size: " + minMBR.size());
//            System.out.println("middle: " + middleX + ", x range: (" + xMerged[0].getX() + ", " + xMerged[xMerged.length - 1].getX() + ")");
            // compute middle kMBR
            if (size > Config.K) {
                Point[] mbrBuffer = new Point[Config.K];
                for (int yStart = 0; yStart < size; yStart += 1) { // merge loop
                    int yLimit = yStart + 1;
                    for (; yLimit < size; yLimit += 1)
                        if (ySubMerged[yLimit].getY() - ySubMerged[yStart].getY() >= range)
                            break;

                    if (yLimit - yStart < Config.K) continue;

                    mbrBuffer[0] = ySubMerged[yStart];

                    Point[] subArray = Arrays.copyOfRange(ySubMerged, yStart + 1, yLimit);
                    Arrays.sort(subArray, Comparator.comparingDouble(Point::getX));

//                    System.out.println("Num of points in band: " + subArray.length);

                    for (int yEnd = yStart + Config.K - 1; yEnd < yLimit; yEnd += 1) {
                        if (ySubMerged[yEnd].getY() - ySubMerged[yStart].getY() >= minMBR.size()/2)
                            break;

                        mbrBuffer[1] = ySubMerged[yEnd];
                        int index = 2;

                        for (int xStart = 0; xStart < subArray.length; xStart += 1) {
                            for (int j = xStart; j < subArray.length && index < Config.K; j += 1) {
                                if (subArray[j].getY() < ySubMerged[yEnd].getY()) {
                                    mbrBuffer[index] = subArray[j];
                                    index += 1;
                                }
                            }

                            if (index == Config.K && Utilities.computeMBRSize(mbrBuffer) < minMBR.size())
                                minMBR = new MBR(mbrBuffer);

                            index = 2;
                        }
                    }
                }
            }
            return new MBRResult(minMBR, xMerged, yMerged);
        }
        else {
//            System.out.println("NON LEAF - no minMBR");
//            System.out.println("Len of points: " + xMerged.length);
//            System.out.println("TreeIndex: " + treeIndex + ", height: " + tree.getHeight(treeIndex));
//            System.out.println("Left child is leaf: " + tree.isLeaf(tree.getLeftChild(treeIndex)) + ", right child is leaf: " + tree.isLeaf(tree.getRightChild(treeIndex)));
//            System.out.println("Left child size: " + tree.getPointSet((int) tree.getLeftChild(treeIndex)).size() + ", right child size: " + tree.getPointSet((int) tree.getRightChild(treeIndex)).size());
            minMBR = Utilities.computeMBR(xMerged, yMerged);
            return new MBRResult(minMBR, xMerged, yMerged);
        }
    }

    protected double getDirtyProbability(final int pointSetIndex) {
        if (Config.FIXED_DIRTY_PROB >= 0)
            return Config.FIXED_DIRTY_PROB;

        double alpha = alphas.getOrDefault(pointSetIndex, -1.0);
        if (alpha < 0) {
            throw new RuntimeException("Alpha for point set " + pointSetIndex + " does not exist.");
        }
        return alpha / alphaPlusBeta;
    }
}

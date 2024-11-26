package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.MBR;

public class KMBRInteractor {

    private static KMBRInteractor inst = null;

    public static KMBRInteractor getInstance() {
        synchronized (KMBRInteractor.class) {
            if (inst == null)
                inst = new KMBRInteractor();
        }
        return inst;
    }

    private KMBRFinder finder;

    private KMBRInteractor() {}

    public MBR find() {
        return finder.find();
    }

    public void movePoint(final Point point, final double x, final double y) {
        finder.movePoint(point, x, y);
    }

    public void removePoint(Point p) {
        finder.removePoint(p);
    }

    public int treeSize() {
        return finder.treeSize();
    }

    public void updateCacheBits() {
        finder.updateCacheBits();
    }

    public void createFinder() {
        finder = new KMBRFinder();
    }

    public void createFinder(final Point[] points) {
        finder = new KMBRFinder(points);
    }

    public void addPoint(final Point point) {
        finder.addPoint(point);
    }

    public void printTree() {
        finder.printTree();
    }

    public void printTree(final int treeIndex) {
        finder.printTree(treeIndex);
    }

    public void printHeights() {
        finder.printHeights();
    }

    public void printHeights(final int treeIndex) {
        finder.printHeights(treeIndex);
    }

    public void printCacheBits() {
        finder.printCacheBits(0);
    }

    public void printDirtyProbs() {
        finder.printDirtyProbs();
    }

    public void printCache() {
        finder.printCache();
    }

    public int getNumOfPoints() {
        return finder.getNumOfPoints();
    }
}

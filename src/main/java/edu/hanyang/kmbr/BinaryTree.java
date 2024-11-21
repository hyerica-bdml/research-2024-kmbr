package edu.hanyang.kmbr;

import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.domain.PointSet;
import edu.hanyang.kmbr.utils.DynamicDoubleArray;
import edu.hanyang.kmbr.utils.DynamicIntArray;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class BinaryTree implements Externalizable {

    private DynamicDoubleArray tree;
    private DynamicDoubleArray minXArray;
    private DynamicIntArray heights;
    private Map<Integer, PointSet> pointSets;
    private int maxPointSetIndex;

    public BinaryTree() {
        tree = new DynamicDoubleArray();
        minXArray = new DynamicDoubleArray();
        heights = new DynamicIntArray();
        pointSets = new TreeMap<>();

        PointSet pointSet = new PointSet();
        pointSets.put(0, pointSet);
        setValue(0, 0.001);
        setHeight(0, 1);
        maxPointSetIndex = 0;

        minXArray.set(0, 0);
    }

    public BinaryTree(Point[] points) {
        tree = new DynamicDoubleArray();
        minXArray = new DynamicDoubleArray();
        heights = new DynamicIntArray();
        pointSets = new TreeMap<>();

        int numOfPointSets = (int) Math.ceil((double) points.length / Config.POINTSET_SIZE);
        int treeHeight = (int) Math.ceil(Math.log(numOfPointSets)/Math.log(2.0));
        Arrays.sort(points, Comparator.comparingDouble(Point::getX));

        int idx = 0;
        int start = (int) Math.pow(2, treeHeight) - 1;
        int end = 2*numOfPointSets - 1;
        for (int i = start; i < end; i += 1, idx += 1) {
            int startIndex = idx * Config.POINTSET_SIZE;
            int endIndex = Math.min((idx + 1) * Config.POINTSET_SIZE, points.length);

            PointSet pointSet = new PointSet(Arrays.copyOfRange(points, startIndex, endIndex));
            pointSets.put(idx, pointSet);
            setValue(i, idx);
            setHeight(i, 1);
            setMinXValue(i, pointSet.getMinX());
            maxPointSetIndex = i;

//            System.out.println("BINARY TREE CREATION: poinset(" + idx + ") point range: " + startIndex + ", " + endIndex + ", pointset size: " + pointSet.size() + ", minX: " + pointSet.getMinX() + ", maxX: " + pointSet.getMaxX());
        }

        start = numOfPointSets - 1;
        end = (int) Math.pow(2, treeHeight) - 1;
        for (int i = start; i < end; i += 1, idx += 1) {
            int startIndex = idx * Config.POINTSET_SIZE;
            int endIndex = Math.min((idx + 1) * Config.POINTSET_SIZE, points.length);

            PointSet pointSet = new PointSet(Arrays.copyOfRange(points, startIndex, endIndex));
            pointSets.put(idx, pointSet);
            setValue(i, idx);
            setHeight(i, 1);
            setMinXValue(i, pointSet.getMinX());
            maxPointSetIndex = i;

//            System.out.println("BINARY TREE CREATION: poinset(" + idx + ") point range: " + startIndex + ", " + endIndex + ", pointset size: " + pointSet.size() + ", minX: " + pointSet.getMinX() + ", maxX: " + pointSet.getMaxX());
        }

        for (int treeIndex = numOfPointSets - 2; treeIndex >= 0; treeIndex -= 1) {
            int left = getLeftChild(treeIndex);
            int right = getRightChild(treeIndex);
            double minValue = minXArray.getValue(left);
            double value = minXArray.getValue(right);

            int height = Math.max(getHeight(left), getHeight(right)) + 1;
            setHeight(treeIndex, height);
            setValue(treeIndex, value);
            setMinXValue(treeIndex, minValue);
        }
    }

    public int search(final Point p) {
        return search(p, 0);
    }

    public int search(final Point p, int treeIndex) {
        while (!isLeaf(treeIndex)) {
            double value = getValue(treeIndex);

            if (p.getX() < value)
                treeIndex = getLeftChild(treeIndex);
            else
                treeIndex = getRightChild(treeIndex);
        }

        return treeIndex;
    }

    public int add(Point p) {
        int treeIndex = search(p, 0);
        return add(p, treeIndex);
    }

    /**
     *
     * @param p
     * @param treeIndex
     * @return 새로 생성된 point set index. 만약, 새로 생성된 pointset 이 없으면 -1 반환
     */
    public int add(Point p, int treeIndex) {
        if (!isLeaf(treeIndex))
            throw new RuntimeException("Tree index of " + treeIndex + " is not leaf!");

        int pointSetIndex = (int) getValue(treeIndex);
        PointSet pointSet = getPointSet(pointSetIndex);
        pointSet.add(p);

        if (pointSet.isFull()) {
            int rightPointSetIndex = ++maxPointSetIndex;
            PointSet rightPointSet = pointSet.split();
            double parentValue = rightPointSet.getMinX();

            int left = getLeftChild(treeIndex);
            int right = getRightChild(treeIndex);

            pointSets.put(rightPointSetIndex, rightPointSet);
            setValue(left, pointSetIndex + 0.001);
            setValue(right, rightPointSetIndex + 0.001);
            setValue(treeIndex, parentValue);

            setHeight(treeIndex, 2);
            setHeight(left, 1);
            setHeight(right, 1);

            setMinXValue(left, pointSet.getMinX());
            setMinXValue(right, rightPointSet.getMinX());

            updateTreeValues(treeIndex);
            updateHeights(treeIndex);
            balance(treeIndex);

            return rightPointSetIndex;
        }
        else {
            if (minXArray.getValue(treeIndex) != pointSet.getMinX())
                updateTreeValues(treeIndex);

            return -1;
        }
    }

    public int remove(Point p) {
        int treeIndex = search(p, 0);
        return remove(p, treeIndex);
    }

//    public int remove(Point p, int treeIndex) {
//        if (!isLeaf(treeIndex))
//            throw new RuntimeException("REMOVE: tree of '" + treeIndex + "' is not leaf!");
//
//        int pointSetIndex = (int) getValue(treeIndex);
//        PointSet pointSet = getPointSet(pointSetIndex);
//        pointSet.remove(p);
//
////        printTree(getParent(treeIndex));
////        printTree();
//
//        if (pointSet.isEmpty()) {
//            tree.remove(treeIndex);
//            minXArray.remove(treeIndex);
//            heights.remove(treeIndex);
//            pointSets.remove(pointSetIndex);
//
//            if (treeIndex != 0) {
//                int parentIndex = getParent(treeIndex);
//                int siblingIndex = getSibling(treeIndex);
//
//                moveSubtree(siblingIndex, parentIndex);
//
//                updateTreeValues(parentIndex);
//                updateHeights(parentIndex);
//                balance(parentIndex);
//            }
//
//            return pointSetIndex;
//        }
////        printTree(getParent(treeIndex));
////        printTree();
//
//        return -1;
//    }

    public int remove(Point p, int treeIndex) {
        if (!isLeaf(treeIndex))
            throw new RuntimeException("REMOVE: tree of '" + treeIndex + "' is not leaf!");

        int pointSetIndex = (int) getValue(treeIndex);
        PointSet pointSet = getPointSet(pointSetIndex);
        pointSet.remove(p);

//        printTree(getParent(treeIndex));
//        printTree();

        int removedPointSetIndex = -1;

        if (treeIndex != 0) {
            int siblingIndex = getSibling(treeIndex);
            int parentIndex = getParent(treeIndex);
            int siblingPointSetIndex = (int) getValue(siblingIndex);
            PointSet siblingPointSet = getPointSet(siblingPointSetIndex);

//            System.out.println("TreeIndices: " + treeIndex + ", " + siblingIndex);
//            System.out.println("PointSetIndices: " + pointSetIndex + ", " + siblingPointSetIndex);
//            System.out.println("PointSet: " + pointSet);
//            System.out.println("Sibling PointSet: " + siblingPointSet);
//            printTree(parentIndex);

            if (isLeaf(siblingIndex)) {
                if (mergeCondition(pointSet, siblingPointSet)) {
                    if (siblingIndex < treeIndex) {
                        siblingPointSet.merge(pointSet);

                        tree.remove(treeIndex);
                        minXArray.remove(treeIndex);
                        heights.remove(treeIndex);
                        pointSets.remove(pointSetIndex);
                        removedPointSetIndex = pointSetIndex;

                        moveSubtree(siblingIndex, parentIndex);
                    } else {
                        pointSet.merge(siblingPointSet);

                        tree.remove(siblingIndex);
                        minXArray.remove(siblingIndex);
                        heights.remove(siblingIndex);
                        pointSets.remove(siblingPointSetIndex);
                        removedPointSetIndex = siblingPointSetIndex;

                        moveSubtree(treeIndex, parentIndex);
                    }

                    updateTreeValues(parentIndex);
                    updateHeights(parentIndex);
                    balance(parentIndex);

//                System.out.println("MERGE: " + removedPointSetIndex);
                }
            }
            else {
                if (pointSet.isEmpty()) {
                    tree.remove(treeIndex);
                    minXArray.remove(treeIndex);
                    heights.remove(treeIndex);
                    pointSets.remove(pointSetIndex);
                    removedPointSetIndex = pointSetIndex;

                    moveSubtree(siblingIndex, parentIndex);

                    updateTreeValues(parentIndex);
                    updateHeights(parentIndex);
                    balance(parentIndex);
                }
            }
        }

        return removedPointSetIndex;
    }

    public PointSet getPointSet(final int pointSetIndex) {
        return pointSets.getOrDefault(pointSetIndex, null);
    }

    public void balance(int treeIndex) {

        // re-balancing
//        int currentTreeIndex = getParent(treeIndex);
        int currentTreeIndex = treeIndex;
        // int lastChildIndex = treeIndex;

        while (currentTreeIndex > -1) {
            if (isLeaf(currentTreeIndex)) {
                currentTreeIndex = getParent(currentTreeIndex);
                continue;
            }

            int leftChild = getLeftChild(currentTreeIndex);
            int rightChild = getRightChild(currentTreeIndex);

            int leftChildHeight = getHeight(leftChild);
            int rightChildHeight = getHeight(rightChild);

            if (leftChildHeight - rightChildHeight > 1) {
                // if left subtree is bigger than right subtree by more than 1

                int leftChildOfLeftChild = getLeftChild(leftChild);
                int rightChildOfLeftChild = getRightChild(leftChild);

                int leftChildOfLeftChildHeight = getHeight(leftChildOfLeftChild);
                int rightChildOfLeftChildHeight = getHeight(rightChildOfLeftChild);

                // first rotation of zig-zag rotation
                if (leftChildOfLeftChildHeight < rightChildOfLeftChildHeight) {
                    moveSubtree(leftChildOfLeftChild, getLeftChild(leftChildOfLeftChild));
                    moveSubtree(getLeftChild(rightChildOfLeftChild), getRightChild(leftChildOfLeftChild));
                    copyNonLeafNode(leftChild, leftChildOfLeftChild);
                    copyNonLeafNode(rightChildOfLeftChild, leftChild);
                    moveSubtree(getRightChild(rightChildOfLeftChild), rightChildOfLeftChild);

                    if (getHeight(getLeftChild(leftChildOfLeftChild)) > getHeight(getRightChild(leftChildOfLeftChild)))
                        setHeight(leftChildOfLeftChild, getHeight(getLeftChild(leftChildOfLeftChild)) + 1);
                    else
                        setHeight(leftChildOfLeftChild, getHeight(getRightChild(leftChildOfLeftChild)) + 1);

                    if (getHeight(leftChildOfLeftChild) > getHeight(rightChildOfLeftChild))
                        setHeight(leftChild, getHeight(leftChildOfLeftChild) + 1);
                    else
                        setHeight(leftChild, getHeight(rightChildOfLeftChild) + 1);
                }

                // second rotation of zig-zag rotation
                moveSubtree(rightChild, getRightChild(rightChild));
                moveSubtree(rightChildOfLeftChild, getLeftChild(rightChild));
                copyNonLeafNode(currentTreeIndex, rightChild);
                copyNonLeafNode(leftChild, currentTreeIndex);
                moveSubtree(leftChildOfLeftChild, leftChild);

                if (getHeight(getLeftChild(rightChild)) > getHeight(getRightChild(rightChild)))
                    setHeight(rightChild, getHeight(getLeftChild(rightChild)) + 1);
                else
                    setHeight(rightChild, getHeight(getRightChild(rightChild)) + 1);

                if (getHeight(leftChild) > getHeight(rightChild))
                    setHeight(currentTreeIndex, getHeight(leftChild) + 1);
                else
                    setHeight(currentTreeIndex, getHeight(rightChild) + 1);
            }
            else if (rightChildHeight - leftChildHeight > 1) {
                // if right subtree is bigger than left subtree by more than 1

                int leftChildOfRightChild = getLeftChild(rightChild);
                int rightChildOfRightChild = getRightChild(rightChild);

                int leftChildOfRightChildHeight = getHeight(leftChildOfRightChild);
                int rightChildOfRightChildHeight = getHeight(rightChildOfRightChild);

                // first rotation of zig-zag rotation
                if (leftChildOfRightChildHeight > rightChildOfRightChildHeight) {
                    moveSubtree(rightChildOfRightChild, getRightChild(rightChildOfRightChild));
                    moveSubtree(getRightChild(leftChildOfRightChild), getLeftChild(rightChildOfRightChild));
                    copyNonLeafNode(rightChild, rightChildOfRightChild);
                    copyNonLeafNode(leftChildOfRightChild, rightChild);
                    moveSubtree(getLeftChild(leftChildOfRightChild), leftChildOfRightChild);

                    if (getHeight(getLeftChild(rightChildOfRightChild)) > getHeight(getRightChild(rightChildOfRightChild)))
                        setHeight(rightChildOfRightChild, getHeight(getLeftChild(rightChildOfRightChild)) + 1);
                    else
                        setHeight(rightChildOfRightChild, getHeight(getRightChild(rightChildOfRightChild)) + 1);

                    if (getHeight(rightChildOfRightChild) > getHeight(leftChildOfRightChild))
                        setHeight(rightChild, getHeight(rightChildOfRightChild) + 1);
                    else
                        setHeight(rightChild, getHeight(leftChildOfRightChild) + 1);
                }

                // second rotation of zig-zag rotation
                moveSubtree(leftChild, getLeftChild(leftChild));
                moveSubtree(leftChildOfRightChild, getRightChild(leftChild));
                copyNonLeafNode(currentTreeIndex, leftChild);
                copyNonLeafNode(rightChild, currentTreeIndex);
                moveSubtree(rightChildOfRightChild, rightChild);

                if (getHeight(getLeftChild(leftChild)) > getHeight(getRightChild(leftChild)))
                    setHeight(leftChild, getHeight(getLeftChild(leftChild)) + 1);
                else
                    setHeight(leftChild, getHeight(getRightChild(leftChild)) + 1);

                if (getHeight(leftChild) > getHeight(rightChild))
                    setHeight(currentTreeIndex, getHeight(leftChild) + 1);
                else
                    setHeight(currentTreeIndex, getHeight(rightChild) + 1);
            }

            // lastChildIndex = currentParentIndex;
            currentTreeIndex = getParent(currentTreeIndex);
        }

        //updateHeights(treeIndex);
    }

    protected void moveSubtree(int fromTreeIndex, int toTreeIndex) {
        List<Integer> queueFrom = new LinkedList<>();
        List<Integer> queueTo = new LinkedList<>();
        List<Integer> tempQueueFrom = new LinkedList<>();
        List<Integer> tempQueueTo = new LinkedList<>();

        queueFrom.add(fromTreeIndex);
        queueTo.add(toTreeIndex);

        tempQueueFrom.add(fromTreeIndex);
        tempQueueTo.add(toTreeIndex);

        while (tempQueueFrom.size() > 0) {
            int sourceTreeIndex = tempQueueFrom.get(0);
            int targetTreeIndex = tempQueueTo.get(0);

            tempQueueFrom.remove(0);
            tempQueueTo.remove(0);

            if (!isLeaf(sourceTreeIndex)) {
                queueFrom.add(0, getLeftChild(sourceTreeIndex));
                queueFrom.add(1, getRightChild(sourceTreeIndex));

                queueTo.add(0, getLeftChild(targetTreeIndex));
                queueTo.add(1, getRightChild(targetTreeIndex));

                tempQueueFrom.add(getLeftChild(sourceTreeIndex));
                tempQueueFrom.add(getRightChild(sourceTreeIndex));

                tempQueueTo.add(getLeftChild(targetTreeIndex));
                tempQueueTo.add(getRightChild(targetTreeIndex));
            }
        }

        double[] tempArrayTo = new double[queueTo.size()];
        double[] tempMinXArray = new double[queueTo.size()];
        int[] tempHeightArray = new int[queueTo.size()];

        for (int i = 0; i < queueFrom.size(); i += 1) {
            tempArrayTo[i] = getValue(queueFrom.get(i));
            tempMinXArray[i] = getMinXValue(queueFrom.get(i));
            tempHeightArray[i] = getHeight(queueFrom.get(i));

            tree.remove(queueFrom.get(i));
            minXArray.remove(queueFrom.get(i));
            heights.remove(queueFrom.get(i));
        }

        for (int i = 0; i < queueTo.size(); i += 1) {
            setValue(queueTo.get(i), tempArrayTo[i]);
            setMinXValue(queueTo.get(i), tempMinXArray[i]);
            setHeight(queueTo.get(i), tempHeightArray[i]);
        }
    }

    protected void copyNonLeafNode(int indexFrom, int indexTo) {
        setValue(indexTo, getValue(indexFrom));

        int leftChildIndex = getLeftChild(indexTo);
        int rightChildIndex = getRightChild(indexTo);

        int leftChildHeight = getHeight(leftChildIndex);
        int rightChildHeight = getHeight(rightChildIndex);

        setHeight(indexTo, Math.max(leftChildHeight, rightChildHeight) + 1);

        double minVal = getMinXValue(leftChildIndex);
        setMinXValue(indexTo, minVal);
    }

    public void updateHeights(int treeIndex) {
        if (isLeaf(treeIndex))
            heights.set(treeIndex, 1);

        treeIndex = getParent(treeIndex);

        while (treeIndex >= 0) {
            int leftHeight = getHeight(getLeftChild(treeIndex));
            int rightHeight = getHeight(getRightChild(treeIndex));

            int height = Math.max(leftHeight, rightHeight) + 1;
            heights.set(treeIndex, height);
            treeIndex = getParent(treeIndex);
        }
    }

    public DynamicDoubleArray getTree() { return tree; }

    public double getValue(int treeIndex) {
        return tree.getValue(treeIndex);
    }

    public boolean mergeCondition(PointSet left, PointSet right) {
        return left.size() + right.size() < Config.POINTSET_SIZE * 0.6 || left.isEmpty() || right.isEmpty();
    }

    public void setValue(int treeIndex, double value) {
        tree.set(treeIndex, value);
    }

    public void setMinXValue(final int treeIndex, double value) {
        minXArray.set(treeIndex, value);
    }

    public double getMinXValue(final int treeIndex) {
        return minXArray.getValue(treeIndex);
    }

    public int treeSize() {
        return tree.size();
    }

    public int heightSize() {
        return heights.size();
    }

    public boolean exists(final int treeIndex) {
        return tree.exists(treeIndex);
    }

    public int getHeight(int treeIndex) {
        return heights.getValue(treeIndex);
    }

    public void setHeight(int treeIndex, int height) {
        heights.set(treeIndex, height);
    }

    public boolean isLeaf(final int treeIndex) {
        int left = getLeftChild(treeIndex);
        int right = getRightChild(treeIndex);

        return !tree.exists(left) && !tree.exists(right);
    }

    public int getLeftChild(final int treeIndex) {
        return treeIndex*2 + 1;
    }

    public int getRightChild(final int treeIndex) {
        return treeIndex*2 + 2;
    }

    public int getSibling(int treeIndex) {
        if (treeIndex == 0)
            return -1;
        else if (treeIndex%2 == 1)
            return treeIndex + 1;
        else
            return treeIndex - 1;
    }

    public int getParent(int treeIndex) {
        if (treeIndex == 0)
            return -1;
        if (treeIndex%2 == 1)
            return (treeIndex - 1)/2;
        else
            return (treeIndex - 2)/2;
    }

    public boolean isValidTreeIndex(final int treeIndex) {
        return tree.get(treeIndex) != null;
    }

    public Map<Integer, PointSet> getPointSets() {
        return pointSets;
    }

    private void updateTreeValues(final int treeIndex) {
        int currentTreeIndex = treeIndex;

        while (currentTreeIndex >= 0) {
            if (isLeaf(currentTreeIndex)) {
                PointSet pointSet = pointSets.get((int) getValue(currentTreeIndex));
                setMinXValue(currentTreeIndex, pointSet.getMinX());
            }
            else {
                int left = getLeftChild(currentTreeIndex);
                int right = getRightChild(currentTreeIndex);

                double newMinX = minXArray.getValue(left);
                double value = minXArray.getValue(right);

                setMinXValue(currentTreeIndex, newMinX);
                setValue(currentTreeIndex, value);
            }

            currentTreeIndex = getParent(currentTreeIndex);
        }
    }

    /**
     * get all descent leaf node index of tree whose root is treeIndex.
     * @param treeIndex root node index to start searching
     * @return
     */
    public List<Integer> getDescentPointSetIndices(int treeIndex) {
        List<Integer> leafPointSetIndices = new LinkedList<>();
        _getDescentPointSetIndices(treeIndex, leafPointSetIndices);
        return leafPointSetIndices;
    }

    protected void _getDescentPointSetIndices(int treeIndex, List<Integer> storage) {
        if (isLeaf(treeIndex)) {
            storage.add((int) tree.get(treeIndex).value);
        }
        else {
            _getDescentPointSetIndices(getLeftChild(treeIndex), storage);
            _getDescentPointSetIndices(getRightChild(treeIndex), storage);
        }
    }

    public void printTree(int root) {
        int currentRowLength = 1;
        int count = 0;

        if (tree.size() == 0) return;

        List<Integer> queue = new LinkedList<>();
        queue.add(root);

        System.out.println("=====");
        while (queue.size() > 0) {
            int index = queue.get(0);
            queue.remove(0);

            if (!isValidTreeIndex(index)) {
                System.out.print("N ");
            }
            else {
                double value = getValue(index);

                if (isLeaf(index))
                    System.out.print("P" + value + " ");
                else
                    System.out.print(value + " ");
            }

            count += 1;
            if (count == currentRowLength) {
                count = 0;
                currentRowLength *= 2;
                System.out.println();
            }

            if (getLeftChild(index) < tree.size()) {
                queue.add(getLeftChild(index));
                queue.add(getRightChild(index));
            }
        }
        System.out.println("\n=====");
    }

    public void printTree() {
        printTree(0);
    }

    public void printHeights() {
        int currentRowLength = 1;
        int count = 0;

        if (heights.size() == 0) return;

        System.out.println("=====");
        for (int i = 0; i < heights.size(); i += 1) {
            if (!isValidTreeIndex(i)) {
                System.out.print("-1 ");
            }
            else {
                int height = getHeight(i);
                System.out.print(height + " ");
            }

            count += 1;

            if (count == currentRowLength) {
                currentRowLength *= 2;
                count = 0;
                System.out.println();
            }
        }
        System.out.println("\n=====");
    }

    public void printHeights(int root) {
        int currentRowLength = 1;
        int count = 0;

        if (heights.size() == 0) return;

        List<Integer> queue = new LinkedList<>();
        queue.add(root);

        System.out.println("=====");
        while (queue.size() > 0) {
            int index = queue.get(0);
            queue.remove(0);

            if (!isValidTreeIndex(index)) {
                System.out.print("-1 ");
            }
            else {
                int height = getHeight(index);
                System.out.print(height + " ");
            }

            count += 1;
            if (count == currentRowLength) {
                count = 0;
                currentRowLength *= 2;
                System.out.println();
            }

            if (getLeftChild(index) < heights.size()) {
                queue.add(getLeftChild(index));
                queue.add(getRightChild(index));
            }
        }
        System.out.println("\n=====");
        System.out.println("\n=====");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }
}

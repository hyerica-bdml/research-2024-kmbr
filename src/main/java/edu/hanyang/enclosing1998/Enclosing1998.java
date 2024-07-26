package edu.hanyang.enclosing1998;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.*;
import java.util.stream.Collectors;

public class Enclosing1998 {

    private List<ClusterAssignment> points;
    private int k;

    public Enclosing1998(final List<ClusterAssignment> points, final int k) {
        this.points = points;
        this.k = k;
    }

    public double find() {
        PriorityQueue<ClusterAssignment> R = new PriorityQueue<>(Comparator.comparingDouble(c -> -c.getPoint().getX()));
        R.addAll(points);

        List<ClusterAssignment> poset = new ArrayList<>(points.size());
        List<ClusterAssignment> L = new ArrayList<>(points);
        for (int i = 0; i < points.size() - k + 1; i += 1) {
            ClusterAssignment r = R.poll();
            poset.add(r);
            L.remove(r);
        }

        PriorityQueue<ClusterAssignment> K1 = new PriorityQueue<>(Comparator.comparingDouble(c -> c.getPoint().getY()));
        PriorityQueue<ClusterAssignment> K2 = new PriorityQueue<>(Comparator.comparingDouble(c -> -c.getPoint().getY()));
        PriorityQueue<ClusterAssignment> D = new PriorityQueue<>(Comparator.comparingDouble(c -> c.getPoint().getX()));

        for (ClusterAssignment c: L) {
            K1.add(c);
            K2.add(c);
            D.add(c);
        }

        double minPerimeter = Double.MAX_VALUE;

        for (int rIndex = 0; rIndex < points.size() - k + 1; rIndex += 1) {
            ClusterAssignment r = D.poll();
            D.add(poset.get(poset.size() - 1 - rIndex));
            K1.add(poset.get(poset.size() - 1 - rIndex));
            K2.add(poset.get(poset.size() - 1 - rIndex));

//            System.out.println(K1.size() + ", " + K2.size());

            List<ClusterAssignment> A1 = new ArrayList<>(points.size() - k);
            List<ClusterAssignment> A2 = new ArrayList<>(points.size() - k);

            PriorityQueue<ClusterAssignment> K1Copy = new PriorityQueue<>(K1);
            PriorityQueue<ClusterAssignment> K2Copy = new PriorityQueue<>(K2);

            for (int qIndex = rIndex; qIndex < poset.size(); qIndex += 1) {
//                System.out.println(D.size());
//                System.out.println(K1.size());
//                System.out.println(K2.size());

                ClusterAssignment q = poset.get(poset.size() - 1 - qIndex);

                double xLength = q.getPoint().getX() - r.getPoint().getX();

                if (qIndex == rIndex) {
                    A1.add(K1Copy.poll());
                    A2.add(K2Copy.poll());

//                    System.out.println("left" + r);
//                    System.out.println("right" + q);
//                    System.out.println("bottom" + A1.get(0));
//                    System.out.println("top" + A2.get(0));
//                    System.out.println(isValidRectangle(r, q, A1.get(0), A2.get(0)));

                    if (isValidRectangle(r, q, A1.get(0), A2.get(0))) {
                        double yLength = A2.get(0).getPoint().getY() - A1.get(0).getPoint().getY();
                        double tempPerimeter = 2*(xLength + yLength);

//                        System.out.println(tempPerimeter);

                        if (minPerimeter > tempPerimeter)
                            minPerimeter = tempPerimeter;
                    }
                }
                else {
                    boolean cond1 = q.getPoint().getY() >= A2.get(A2.size() - 1).getPoint().getY();
                    boolean cond2 = q.getPoint().getY() <= A1.get(0).getPoint().getY();

                    if (cond1 && cond2) {
                        for (int i = 0; i < A2.size(); i += 1) {
                            if (q.getPoint().getY() >= A2.get(i).getPoint().getY()) {
                                A2.add(i, q);
                                break;
                            }
                        }

                        if (q.getPoint().getY() < A1.get(A1.size() - 1).getPoint().getY()) {
                            A1.add(q);
                        }
                        else {
                            for (int i = 0; i < A1.size(); i += 1) {
                                if (q.getPoint().getY() >= A1.get(i).getPoint().getY()) {
                                    A1.add(i, q);
                                    break;
                                }
                            }
                        }
                    }
                    else if (cond1) {
                        for (int i = 0; i < A2.size(); i += 1) {
                            if (q.getPoint().getY() >= A2.get(i).getPoint().getY()) {
                                A2.add(i, q);
                                break;
                            }
                        }

                        K1Copy.add(q);
                        A1.add(0, K1Copy.poll());
                    }
                    else if (cond2) {
                        if (q.getPoint().getY() < A1.get(A1.size() - 1).getPoint().getY()) {
                            A1.add(q);
                        }
                        else {
                            for (int i = 0; i < A1.size(); i += 1) {
                                if (q.getPoint().getY() >= A1.get(i).getPoint().getY()) {
                                    A1.add(i, q);
                                    break;
                                }
                            }
                        }

                        K2Copy.add(q);
                        A2.add(K2Copy.poll());
                    }
                    else {
                        K1Copy.add(q);
                        K2Copy.add(q);

                        A1.add(0, K1Copy.poll());
                        A2.add(K2Copy.poll());
                    }

                    for (int i = 0; i < A1.size(); i += 1) {
//                        System.out.println(isValidRectangle(r, q, A1.get(0), A2.get(0)));
                        if (isValidRectangle(r, q, A1.get(i), A2.get(i))) {
                            double yLength = A2.get(i).getPoint().getY() - A1.get(i).getPoint().getY();
                            double tempPerimeter = 2*(xLength + yLength);

//                            System.out.println(tempPerimeter);

                            if (minPerimeter > tempPerimeter)
                                minPerimeter = tempPerimeter;
                        }
                    }
                }
            }

            K1.remove(r);
            K2.remove(r);
        }
        return minPerimeter;
    }

    private boolean isValidRectangle(ClusterAssignment left, ClusterAssignment right, ClusterAssignment bottom, ClusterAssignment top) {
        return left.getPoint().getX() < right.getPoint().getX() && top.getPoint().getY() > bottom.getPoint().getY()
                && left.getPoint().getY() >= bottom.getPoint().getY() && right.getPoint().getY() >= bottom.getPoint().getY()
                && left.getPoint().getY() <= top.getPoint().getY() && right.getPoint().getY() <= top.getPoint().getY()
                && top.getPoint().getX() >= left.getPoint().getX() && bottom.getPoint().getX() >= left.getPoint().getX()
                && top.getPoint().getX() <= right.getPoint().getX() && bottom.getPoint().getX() <= right.getPoint().getX();
    }
}

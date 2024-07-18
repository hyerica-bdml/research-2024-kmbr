package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Smallest2019Basic {

    private final List<ClusterAssignment> ySortedClusterAssignments;

    public Smallest2019Basic(final List<ClusterAssignment> clusterAssignments) {
        ySortedClusterAssignments = new LinkedList<>(clusterAssignments);
        ySortedClusterAssignments.sort(Comparator.comparingDouble(c -> c.getPoint().getY()));
    }

    public SmallestMBRResult find() {
        DistanceMatrix distanceMat = new DistanceMatrix(ySortedClusterAssignments, DistanceMatrix.SortType.X);
        return findRecursive(ySortedClusterAssignments.size(), 0, ySortedClusterAssignments.size(), 0, distanceMat);
    }

    private SmallestMBRResult findRecursive(final int sigmaTop,
                                            final int sigmaBottom,
                                            final int tauTop,
                                            final int tauBottom,
                                            final DistanceMatrix distanceMat) {

        if (sigmaTop - tauBottom < Config.K || tauBottom > sigmaTop) return null;
//        System.out.println(sigmaTop + ", " + sigmaBottom + ", " + tauTop + ", " + tauBottom);

        SmallestMBRResult result;
        int qSigma = sigmaTop - sigmaBottom;
        int qTau = tauTop - tauBottom;
//        System.out.println(qSigma + ", " + qTau);

        if (qSigma > 1 && qTau > 1) {
            DistanceMatrix distanceTempMat1 = distanceMat.duplicate();
            for (int i = tauBottom; i < tauTop - qTau/2; i += 1)
                distanceTempMat1.removePoint(ySortedClusterAssignments.get(i));
            SmallestMBRResult temp1 = findRecursive(
                    sigmaTop,
                    sigmaTop - qSigma/2,
                    tauTop,
                    tauTop - qTau/2,
                    distanceTempMat1
            );

            DistanceMatrix distanceTempMat2 = distanceMat.duplicate();
            SmallestMBRResult temp2 = findRecursive(
                    sigmaTop,
                    sigmaTop - qSigma/2,
                    tauTop - qTau/2,
                    tauBottom,
                    distanceTempMat2
            );

            DistanceMatrix distanceTempMat3 = distanceMat.duplicate();
            for (int i = tauBottom; i < tauTop - qTau/2; i += 1)
                distanceTempMat3.removePoint(ySortedClusterAssignments.get(i));
            for (int i = sigmaTop - qSigma/2; i < sigmaTop; i += 1)
                distanceTempMat3.removePoint(ySortedClusterAssignments.get(i));
            SmallestMBRResult temp3 = findRecursive(
                    sigmaTop - qSigma/2,
                    sigmaBottom,
                    tauTop,
                    tauTop - qTau/2,
                    distanceTempMat3
            );

            DistanceMatrix distanceTempMat4 = distanceMat.duplicate();
            for (int i = sigmaTop - qSigma/2; i < sigmaTop; i += 1)
                distanceTempMat4.removePoint(ySortedClusterAssignments.get(i));
            SmallestMBRResult temp4 = findRecursive(
                    sigmaTop - qSigma/2,
                    sigmaBottom,
                    tauTop - qTau/2,
                    tauBottom,
                    distanceTempMat4
            );

            result = getMinimumMBR(temp1, temp2, temp3, temp4);
        } else if (qSigma == 1 && qTau > 1) {
            DistanceMatrix distanceTempMat1 = distanceMat.duplicate();
            for (int i = tauBottom; i < tauTop - qTau/2; i += 1)
                distanceTempMat1.removePoint(ySortedClusterAssignments.get(i));
            SmallestMBRResult temp1 = findRecursive(
                    sigmaTop,
                    sigmaBottom,
                    tauTop,
                    tauTop - qTau/2,
                    distanceTempMat1
            );

            DistanceMatrix distanceTempMat2 = distanceMat.duplicate();
            SmallestMBRResult temp2 = findRecursive(
                    sigmaTop,
                    sigmaBottom,
                    tauTop - qTau/2,
                    tauBottom,
                    distanceTempMat2
            );

            result = getMinimumMBR(temp1, temp2);
        } else if (qSigma > 1 && qTau == 1) {
            DistanceMatrix distanceTempMat1 = distanceMat.duplicate();
            SmallestMBRResult temp1 = findRecursive(
                    sigmaTop,
                    sigmaTop - qSigma/2,
                    tauTop,
                    tauBottom,
                    distanceTempMat1
            );

            DistanceMatrix distanceTempMat2 = distanceMat.duplicate();
            for (int i = sigmaTop - qSigma/2; i < sigmaTop; i += 1)
                distanceTempMat2.removePoint(ySortedClusterAssignments.get(i));
            SmallestMBRResult temp2 = findRecursive(
                    sigmaTop - qSigma/2,
                    sigmaBottom,
                    tauTop,
                    tauBottom,
                    distanceTempMat2
            );

            result = getMinimumMBR(temp1, temp2);
        } else {
            List<ClusterAssignment> candidates = distanceMat.get();
            result = new SmallestMBRResult(candidates);
        }

        return result;
    }

    private SmallestMBRResult getMinimumMBR(SmallestMBRResult... mbrs) {
        SmallestMBRResult result = null;
        for (SmallestMBRResult mbr : mbrs) {
            if (result == null || (mbr != null && result.size() > mbr.size()))
                result = mbr;
        }
        return result;
    }


//    public SmallestMBRResult find() {
//        return findRecursive(ySortedClusterAssignments.size(), 0, ySortedClusterAssignments.size(), 0);
//    }
//
//    private SmallestMBRResult findRecursive(final int sigmaTop,
//                                            final int sigmaBottom,
//                                            final int tauTop,
//                                            final int tauBottom) {
//
//        if (sigmaTop - tauBottom < Config.K || tauBottom > sigmaTop) return null;
////        System.out.println(sigmaTop + ", " + sigmaBottom + ", " + tauTop + ", " + tauBottom);
//
//        SmallestMBRResult result;
//        int qSigma = sigmaTop - sigmaBottom;
//        int qTau = tauTop - tauBottom;
////        System.out.println(qSigma + ", " + qTau);
//
//        if (qSigma > 1 && qTau > 1) {
//            SmallestMBRResult temp1 = findRecursive(
//                    sigmaTop,
//                    sigmaTop - qSigma/2,
//                    tauTop,
//                    tauTop - qTau/2
//            );
//
//            SmallestMBRResult temp2 = findRecursive(
//                    sigmaTop,
//                    sigmaTop - qSigma/2,
//                    tauTop - qTau/2,
//                    tauBottom
//            );
//
//            SmallestMBRResult temp3 = findRecursive(
//                    sigmaTop - qSigma/2,
//                    sigmaBottom,
//                    tauTop,
//                    tauTop - qTau/2
//            );
//
//            SmallestMBRResult temp4 = findRecursive(
//                    sigmaTop - qSigma/2,
//                    sigmaBottom,
//                    tauTop - qTau/2,
//                    tauBottom
//            );
//
//            result = getMinimumMBR(temp1, temp2, temp3, temp4);
//        } else if (qSigma == 1 && qTau > 1) {
//            SmallestMBRResult temp1 = findRecursive(
//                    sigmaTop,
//                    sigmaBottom,
//                    tauTop,
//                    tauTop - qTau/2
//            );
//
//            SmallestMBRResult temp2 = findRecursive(
//                    sigmaTop,
//                    sigmaBottom,
//                    tauTop - qTau/2,
//                    tauBottom
//            );
//
//            result = getMinimumMBR(temp1, temp2);
//        } else if (qSigma > 1 && qTau == 1) {
//            SmallestMBRResult temp1 = findRecursive(
//                    sigmaTop,
//                    sigmaTop - qSigma/2,
//                    tauTop,
//                    tauBottom
//            );
//
//            SmallestMBRResult temp2 = findRecursive(
//                    sigmaTop - qSigma/2,
//                    sigmaBottom,
//                    tauTop,
//                    tauBottom
//            );
//
//            result = getMinimumMBR(temp1, temp2);
//        } else {
//            DistanceMatrix distanceMat = new DistanceMatrix(ySortedClusterAssignments.subList(tauBottom, sigmaTop), DistanceMatrix.SortType.X);
//            List<ClusterAssignment> candidates = distanceMat.get();
//            result = new SmallestMBRResult(candidates);
//        }
//
//        return result;
//    }
}

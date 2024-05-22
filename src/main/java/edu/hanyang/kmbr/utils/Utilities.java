package edu.hanyang.kmbr.utils;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.domain.Point;

public class Utilities {
    public static MBR computeMBR(Point[] xSortedPoints, Point[] ySortedPoints) {
        if (xSortedPoints.length < Config.K) return null;

        Point[] mbrBuffer = new Point[Config.K];
        MBR minMBR = null;

        for (int yStart = 0; yStart < ySortedPoints.length - Config.K; yStart += 1) {
            mbrBuffer[0] = ySortedPoints[yStart];

            for (int yEnd = yStart + Config.K - 1; yEnd < ySortedPoints.length; yEnd += 1) {
                if (minMBR != null && ySortedPoints[yEnd].getY() - ySortedPoints[yStart].getY() >= minMBR.size()/2)
                    break;

                mbrBuffer[1] = ySortedPoints[yEnd];
                int index = 2;

                for (int xStart = 0; xStart < xSortedPoints.length; xStart += 1) {
                    for (int j = xStart; j < xSortedPoints.length && index < Config.K; j += 1) {
                        if ((xSortedPoints[j] != mbrBuffer[0] && xSortedPoints[j] != mbrBuffer[1]) &&
                                (xSortedPoints[j].getY() >= ySortedPoints[yStart].getY()) &&
                                (xSortedPoints[j].getY() < ySortedPoints[yEnd].getY())) {
                            mbrBuffer[index] = xSortedPoints[j];
                            index += 1;
                        }
                    }

                    if (index == Config.K)
                        if (minMBR == null || Utilities.computeMBRSize(mbrBuffer) < minMBR.size())
                            minMBR = new MBR(mbrBuffer);

                    index = 2;
                }
            }
        }
        return minMBR;
    }

    public static double computeMBRSize(Point[] Points) {
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE, minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (Point p: Points) {
            if (p == null) break;

            double x = p.getX();
            double y = p.getY();

            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
        }

        return ((maxX - minX) + (maxY - minY))*2;
    }

    public static double[] computeCDF(final double[] probs) {
        double[] cdf = new double[probs.length];

        cdf[0] = probs[0];
        for (int i = 1; i < probs.length; i += 1) {
            cdf[i] = cdf[i - 1] + probs[i];
        }

        if (cdf[cdf.length - 1] > 1) cdf[cdf.length - 1] = 1.0;
        return cdf;
    }
}

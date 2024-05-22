package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CitibikeSimulationApp {

    public static void main(String[] args) {
        CitibikeSimulationApp app = new CitibikeSimulationApp();

        String caching;
        if (Config.useCache)
            caching = "with_caching";
        else
            caching = "without_caching";
//
        for (int day = 5; day <= 31; day += 10) {
            for (int hour = 9; hour < 21; hour += 3) {
                String inputPath = String.format("data/citibike/2022_10_%d_%d.csv", day, hour);
                String outputPath = String.format("logs/citibike/2022_10_%d_%d_%s.csv", day, hour, caching);
                app.run(inputPath, outputPath);
            }
        }

//        app.run("data/citibike/2022_10_25_12.csv", "logs/citibike/2022_10_25_12__with_caching.csv");
    }

    private KMBRInteractor kmbr;
    private DatabaseInteractor db;

    public CitibikeSimulationApp() {
        kmbr = KMBRInteractor.getInstance();
        db = DatabaseInteractor.getInstance();
    }

    public void run(String inputPath, String outputPath) {
        System.out.println("Reading data...");
        Point[] points = readInitialPoints(inputPath);
        System.out.printf("Done. (num of points: %d)\n\n", points.length);

        System.out.println("Initializing KMBR finder...");
        kmbr.createFinder(points);
        System.out.println("Done.\n");

        if (Config.useCache)
            kmbr.updateCacheBits();


        for (int i = 0; i < 10; i += 1) {
            long startTime = System.currentTimeMillis();
            kmbr.find();
            long endTime = System.currentTimeMillis();
            double runtime = (endTime - startTime) / 1000.0;
            System.out.printf("Runtime: %f\n", runtime);
        }
//        System.out.println("Computing initial kmbr...");
//        kmbr.find();
//        System.out.println("Done.\n");
//
//        try (FileReader fin = new FileReader(inputPath);
//             BufferedReader bin = new BufferedReader(fin);
//             FileWriter fout = new FileWriter(outputPath);
//             BufferedWriter bout = new BufferedWriter(fout)) {
//
//            String line = bin.readLine();
//            int year = 2022;
//            int month = 10;
//            int prev_minute = 0;
//
//            while ((line = bin.readLine()) != null) {
//                String[] split = line.split(",");
//
//                if (split[0].equals("move")) {
//                    int id = (int) Double.parseDouble(split[1]);
//                    double x = Double.parseDouble(split[2]);
//                    double y = Double.parseDouble(split[3]);
//
//                    int day = (int) Double.parseDouble(split[6]);
//                    int hour = (int) Double.parseDouble(split[7]);
//                    int minute = (int) Double.parseDouble(split[8]);
//
//                    kmbr.movePoint(points[id], x, y);
//
//                    if (prev_minute != minute) {
//                        if (Config.useCache)
//                            kmbr.updateCacheBits();
//
//                        long startTime = System.currentTimeMillis();
//                        kmbr.find();
//                        long endTime = System.currentTimeMillis();
//                        double runtime = (endTime - startTime) / 1000.0;
//                        System.out.printf("(%d/%d/%d %d:%d) Runtime: %f\n", year, month, day, hour, minute, runtime);
//                        bout.write(String.format("%d,%d,%d,%d,%d,%f\n", year, month, day, hour, minute, runtime));
//                    }
//
//                    prev_minute = minute;
//                }
//            }
//        } catch (IOException exc) {
//            exc.printStackTrace();
//        }
    }

    public Point[] readInitialPoints(final String csvFilePath) {
        List<Point> points = new LinkedList<>();

        try (FileReader fin = new FileReader(csvFilePath);
             BufferedReader bin = new BufferedReader(fin)) {

            String line = bin.readLine();

            while ((line = bin.readLine()) != null) {
                String[] split = line.split(",");
                if (split[0].equals("add")) {
                    double x = Double.parseDouble(split[2]);
                    double y = Double.parseDouble(split[3]);

                    Point p = db.newPoint(x, y);
                    points.add(p);
                }
                else {
                    break;
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        Point[] arr = new Point[points.size()];
        points.toArray(arr);
        return arr;
    }
}

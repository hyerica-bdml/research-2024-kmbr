package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CitibikeSimulationApp {

    public static void main(String[] args) {
        int[] days = new int[] {2, 11, 24};
        int[] hours = new int[] {9, 12};

        for (int day : days) {
            for (int hour : hours) {
                String initPath = String.format("data/citibike/parsed/citibike_streaming_init_2022-10-%d-%d.txt", day, hour);
                String inputPath = String.format("data/citibike/parsed/citibike_streaming_2022-10-%d-%d.txt", day, hour);
                String outputPath = String.format("logs/ours/citibike_2022-10-%d-%d-cache-%s.txt", day, hour, Config.useCache);
                new CitibikeSimulationApp().run(initPath, inputPath, outputPath);
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

    public void run(String initPath, String inputPath, String outputPath) {
        System.out.println("Reading data...");
        Point[] points = readInitialPoints(initPath);
        System.out.printf("Done. (num of points: %d)\n\n", points.length);

        System.out.println("Initializing KMBR finder...");
        kmbr.createFinder(points);
        System.out.println("Done.\n");

        if (Config.useCache) {
            kmbr.updateCacheBits();
            kmbr.find();
        }

        int iteration = 0;

        try (FileReader fin = new FileReader(inputPath);
             BufferedReader bin = new BufferedReader(fin, 1024);
             FileWriter fout = new FileWriter(outputPath, true)) {

            String line;

            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");
                long pid = -1;
                double x, y;
                String time = "";

                try {
                    if (split[0].equals("create")) {
                        pid = Long.parseLong(split[1]);
                        x = Double.parseDouble(split[2]);
                        y = Double.parseDouble(split[3]);
                        time = split[4] + " " + split[5];

                        Point p = db.getPointById(pid).getPoint();
                        p.set(x, y);
                        kmbr.addPoint(p);

                    } else if (split[0].equals("remove")) {
                        pid = Long.parseLong(split[1]);
                        x = Double.parseDouble(split[2]);
                        y = Double.parseDouble(split[3]);
                        time = split[4] + " " + split[5];

                        Point p = db.getPointById(pid).getPoint();
                        kmbr.removePoint(p);
                    }
                } catch (NullPointerException exc) {
                    System.out.println(pid);
                }

                iteration += 1;

                if (Config.useCache) {
                    if (iteration % 100 == 0) {
                        kmbr.updateCacheBits();

                        long startTime = System.currentTimeMillis();
                        kmbr.find();
                        double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                        fout.write(String.format("%f,%s\n", runtime, time));
                        System.out.printf("Iteration: %d, runtime: %f\n", iteration, runtime);
                    }
                }
                else {
                    if (iteration % 500 == 0) {
                        long startTime = System.currentTimeMillis();
                        kmbr.find();
                        double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                        fout.write(String.format("%f,%s\n", runtime, time));
                        System.out.printf("Iteration: %d, runtime: %f\n", iteration, runtime);
                    }
                }

                fout.flush();
            }

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public Point[] readInitialPoints(final String initFile) {
        List<ClusterAssignment> assignments = new LinkedList<>();

        try (FileReader fin = new FileReader(initFile);
             BufferedReader bin = new BufferedReader(fin, 1024)) {

            String line;
            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals("create")) {
                    double x = Double.parseDouble(split[2]) + 1000;
                    double y = Double.parseDouble(split[3]) + 1000;

                    ClusterAssignment p = db.newPoint(x, y, 0);
                    assignments.add(p);
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        Point[] arr = new Point[assignments.size()];
        for (int i = 0; i < arr.length; i += 1) {
            arr[i] = assignments.get(i).getPoint();
        }
        return arr;
    }
}

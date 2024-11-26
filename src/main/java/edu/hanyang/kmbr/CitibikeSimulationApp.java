package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.domain.Point;
import edu.hanyang.kmbr.utils.DataWriter;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.MBR;

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
    }

//    public static void main(String[] args) {
//        int[] days = new int[] {2, 11, 24};
//        int[] hours = new int[] {9, 12};

//        for (int day : days) {
//            for (int hour : hours) {
//                String initPath = String.format("data/citibike/parsed/citibike_streaming_init_2022-10-%d-%d.txt", day, hour);
//                String inputPath = String.format("data/citibike/parsed/citibike_streaming_2022-10-%d-%d.txt", day, hour);
//                String outputPath = String.format("logs/ours/citibike_qualitative_2022-10-%d-%d-cache-%s.txt", day, hour, Config.useCache);
//                new CitibikeSimulationApp().run2(initPath, inputPath, outputPath);
//            }
//        }
//    }

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
                        x = Double.parseDouble(split[2]) + 1000;
                        y = Double.parseDouble(split[3]) + 1000;
                        time = split[4] + " " + split[5];

                        Point p = db.getPointById(pid).getPoint();
                        p.set(x, y);
                        kmbr.addPoint(p);

                    } else if (split[0].equals("remove")) {
                        pid = Long.parseLong(split[1]);
                        x = Double.parseDouble(split[2]) + 1000;
                        y = Double.parseDouble(split[3]) + 1000;
                        time = split[4] + " " + split[5];

                        Point p = db.getPointById(pid).getPoint();
                        kmbr.removePoint(p);
                    }
                } catch (NullPointerException exc) {
                    System.out.println(pid);
                }

                iteration += 1;

                if (iteration % 500 == 0) {
                    if (Config.useCache)
                        kmbr.updateCacheBits();

                    long startTime = System.currentTimeMillis();
                    kmbr.find();
                    double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                    fout.write(String.format("%f,%s\n", runtime, time));
                    System.out.printf("Iteration: %d, runtime: %f\n", iteration, runtime);
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
                    long id = Long.parseLong(split[1]);
                    double x = Double.parseDouble(split[2]) + 1000;
                    double y = Double.parseDouble(split[3]) + 1000;

                    ClusterAssignment p = db.newPoint(id, x, y, 0);
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

    // qualitative experiment
    public void run2(final String initPath, final String csvFilePath, String outputPath) {
        System.out.println("Reading data...");
        Point[] points = readInitialPoints2(initPath);
        System.out.printf("Done. (num of points: %d)\n\n", points.length);

        System.out.println("Initializing KMBR finder...");
        kmbr.createFinder(points);
        System.out.println("Done.\n");
        DataWriter writer = new DataWriter(outputPath);

        for (Point p: points) {
            writer.write(EventType.CREATE, p.getId(), p.getX(), p.getY());
        }

        try (FileReader fin = new FileReader(csvFilePath);
             BufferedReader bin = new BufferedReader(fin, 1024)) {

            String line;
            int iteration = 0;

            Random random = new Random();

            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");
                iteration += 1;

                if (split[0].equals("create")) {
                    long pid = Long.parseLong(split[1]);
                    double x = Double.parseDouble(split[2]) + 1000;
                    double y = Double.parseDouble(split[3]) + 1000;
                    String time = split[4] + " " + split[5];

                    ClusterAssignment c = db.getPointById(pid);
                    if (c != null) {
                        Point p = c.getPoint();
                        kmbr.removePoint(p);
                        writer.write(EventType.REMOVE, pid, x, y);
                        p.set(x, y);
                        kmbr.addPoint(p);
                        writer.write(EventType.CREATE, pid, x, y);
                    }
                }

                else if (split[0].equals("remove")) {
                    long pid = Long.parseLong(split[1]);
                    double x = Double.parseDouble(split[2]) + 1000;
                    double y = Double.parseDouble(split[3]) + 1000;
                    String time = split[4] + " " + split[5];

                    ClusterAssignment c = db.getPointById(pid);
                    if (c != null) {
                        Point p = c.getPoint();
                        kmbr.removePoint(p);
                        writer.write(EventType.REMOVE, pid, x, y);
                    }
                }
            }

            kmbr.updateCacheBits();

            long startTime = System.currentTimeMillis();
            MBR mbr = kmbr.find();
            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
            System.out.printf("Iteration: %d, Runtime: %f\n", iteration, runtime);
            writer.writeMBR(mbr.getPointIds());
            writer.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public Point[] readInitialPoints2(final String initFile) {
        List<ClusterAssignment> assignments = new LinkedList<>();

        try (FileReader fin = new FileReader(initFile);
             BufferedReader bin = new BufferedReader(fin, 1024)) {

            String line;
            int iteration = 0;
            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");
                iteration += 1;
                if (iteration % 10 == 0) {
                    if (split[0].equals("create")) {
                        long id = Long.parseLong(split[1]);
                        double x = Double.parseDouble(split[2]) + 1000;
                        double y = Double.parseDouble(split[3]) + 1000;

                        ClusterAssignment p = db.newPoint(id, x, y, 0);
                        assignments.add(p);
                    }
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

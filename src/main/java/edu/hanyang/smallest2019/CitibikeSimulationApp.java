package edu.hanyang.smallest2019;

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
                String outputPath = String.format("logs/related_works/smallest2019/citibike_2022-10-%d-%d.txt", day, hour);
                new CitibikeSimulationApp().run(initPath, inputPath, outputPath);
            }
        }

//        app.run("data/citibike/2022_10_25_12.csv", "logs/citibike/2022_10_25_12__with_caching.csv");
    }

    private DatabaseInteractor db;

    public CitibikeSimulationApp() {
        db = DatabaseInteractor.getInstance();
    }

    public void run(String initPath, String inputPath, String outputPath) {
        System.out.println("Reading data...");
        List<ClusterAssignment> clusterAssignments = readInitialPoints(initPath);
        System.out.printf("Done. (num of points: %d)\n\n", clusterAssignments.size());

        System.out.println("Initializing KMBR finder...");
        System.out.println("Done.\n");

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

                        ClusterAssignment assignment = db.getPointById(pid);
                        assignment.getPoint().set(x, y);
                        clusterAssignments.add(assignment);

                    } else if (split[0].equals("remove")) {
                        pid = Long.parseLong(split[1]);
                        x = Double.parseDouble(split[2]);
                        y = Double.parseDouble(split[3]);
                        time = split[4] + " " + split[5];

                        clusterAssignments.remove(db.getPointById(pid));
                    }
                } catch (NullPointerException exc) {
                    System.out.println(pid);
                }

                iteration += 1;

                if (iteration % 500 == 0) {
                    long startTime = System.currentTimeMillis();
                    new Smallest2019(clusterAssignments).find();
                    double runtime = (System.currentTimeMillis() - startTime) / 1000.0;
                    fout.write(String.format("%f,%s\n", runtime, time));
                    fout.flush();
                    System.out.printf("Iteration: %d, runtime: %f\n", iteration, runtime);
                    if (runtime >= 600) break;
                }
            }

        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public List<ClusterAssignment> readInitialPoints(final String initFile) {
        List<ClusterAssignment> assignments = new LinkedList<>();

        try (FileReader fin = new FileReader(initFile);
             BufferedReader bin = new BufferedReader(fin, 1024)) {

            String line;
            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");
                if (split[0].equals("create")) {
                    double x = Double.parseDouble(split[2]);
                    double y = Double.parseDouble(split[3]);

                    ClusterAssignment p = db.newPoint(x, y, 0);
                    assignments.add(p);
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        return assignments;
    }
}

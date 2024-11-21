package edu.hanyang.enclosing1998;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CityEscooterSimulationApp {

    public static void main(String[] args) {
        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2018-10.txt",
                String.format("logs/related_works/enclosing1998/city_Lousiville_escooter_trip_start_streaming_K%d-2018-10.txt", Config.K)
        );

        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-1.txt",
                String.format("logs/related_works/enclosing1998/city_Lousiville_escooter_trip_start_streaming_K%d-2019-1.txt", Config.K)
        );

        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-4.txt",
                String.format("logs/related_works/enclosing1998/city_Lousiville_escooter_trip_start_streaming_K%d-2019-4.txt", Config.K)
        );

        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-7.txt",
                String.format("logs/related_works/enclosing1998/city_Lousiville_escooter_trip_start_streaming_K%d-2019-7.txt", Config.K)
        );
    }

    private final DatabaseInteractor db;

    public CityEscooterSimulationApp() {
        db = DatabaseInteractor.getInstance();
    }

    public void run(final String csvFilePath, String outputPath) {
        try (FileReader fin = new FileReader(csvFilePath);
             BufferedReader bin = new BufferedReader(fin, 1024);
             FileWriter fout = new FileWriter(outputPath, true)) {

            String line;
            int iteration = 0;
            Random random = new Random();

            List<ClusterAssignment> clusterAssignments = new LinkedList<>();

            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");

                if (split[0].equals("create")) {
                    double x = Double.parseDouble(split[2]) + random.nextDouble()*1e-7 - 5e-8 + 1000;
                    double y = Double.parseDouble(split[3]) + random.nextDouble()*1e-7 - 5e-8 + 1000;
                    String time = split[4] + " " + split[5];

                    ClusterAssignment p = db.newPoint(x, y, 0);
                    clusterAssignments.add(p);

                    iteration += 1;

                    if (iteration % 500 == 0) {
                        long startTime = System.currentTimeMillis();
                        Enclosing1998 method = new Enclosing1998(clusterAssignments, Config.K);
                        method.find();
                        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
                        fout.write(String.format("%f,%s\n", runtime, time));
                        fout.flush();
                        System.out.printf("Iteration: %d, Runtime: %f\n", iteration, runtime);
                        if (runtime >= 600) break;
                    }
                }
                else {
                    break;
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

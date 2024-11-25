package edu.hanyang.kmbr;

import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;
import edu.hanyang.kmbr.utils.DataWriter;
import edu.hanyang.kmbr.utils.EventType;
import edu.hanyang.kmbr.utils.MBR;

import java.io.*;
import java.util.Random;

public class CityEscooterSimulationApp {

    public static void main(String[] args) {
        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2018-10.txt",
                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s-2018-10.txt", Config.K, Config.useCache)
        );

        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-1.txt",
                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s-2019-1.txt", Config.K, Config.useCache)
        );

        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-4.txt",
                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s-2019-4.txt", Config.K, Config.useCache)
        );

        new CityEscooterSimulationApp().run(
                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-7.txt",
                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s-2019-7.txt", Config.K, Config.useCache)
        );

        // qualitative
//        new CityEscooterSimulationApp().run2(
//                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2018-10.txt",
//                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s_qualitative-2018-10.txt", Config.K, Config.useCache)
//        );
//        new CityEscooterSimulationApp().run2(
//                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-1.txt",
//                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s_qualitative-2019-1.txt", Config.K, Config.useCache)
//        );
//        new CityEscooterSimulationApp().run2(
//                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-4.txt",
//                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s_qualitative-2019-4.txt", Config.K, Config.useCache)
//        );
//        new CityEscooterSimulationApp().run2(
//                "data/city-lousiville-escooter-trip-data/parsed/city_Lousiville_escooter_trip_start_streaming-2019-7.txt",
//                String.format("logs/ours/city_Lousiville_escooter_trip_start_streaming_K%d_cache_%s_qualitative-2019-7.txt", Config.K, Config.useCache)
//        );
    }

    private final KMBRInteractor kmbr;
    private final DatabaseInteractor db;

    public CityEscooterSimulationApp() {
        kmbr = KMBRInteractor.getInstance();
        db = DatabaseInteractor.getInstance();
    }

    public void run(final String csvFilePath, String outputPath) {
        kmbr.createFinder();

        try (FileReader fin = new FileReader(csvFilePath);
             BufferedReader bin = new BufferedReader(fin, 1024);
             FileWriter fout = new FileWriter(outputPath, true)) {

            String line;
            int iteration = 0;

            Random random = new Random();

            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");

                if (split[0].equals("create")) {
                    double x = Double.parseDouble(split[2]) + random.nextDouble()*1e-7 - 5e-8 + 1000;
                    double y = Double.parseDouble(split[3]) + random.nextDouble()*1e-7 - 5e-8 + 1000;
                    String time = split[4] + " " + split[5];

                    ClusterAssignment p = db.newPoint(x, y, 0);
                    kmbr.addPoint(p.getPoint());

                    iteration += 1;

                    if (iteration % 500 == 0) {
                        if (Config.useCache)
                            kmbr.updateCacheBits();

                        long startTime = System.currentTimeMillis();
                        kmbr.find();
                        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
                        fout.write(String.format("%f,%s\n", runtime, time));
                        System.out.printf("Iteration: %d, Runtime: %f\n", iteration, runtime);
                    }

//                    if (Config.useCache) {
//                        if (iteration % 100 == 0) {
//                            kmbr.updateCacheBits();
//
//                            long startTime = System.currentTimeMillis();
//                            kmbr.find();
//                            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
//                            fout.write(String.format("%f,%s\n", runtime, time));
//                            System.out.printf("Iteration: %d, Runtime: %f\n", iteration, runtime);
//                        }
//                    }
//                    else {
//                        if (iteration % 500 == 0) {
//                            long startTime = System.currentTimeMillis();
//                            kmbr.find();
//                            double runtime = (System.currentTimeMillis() - startTime)/1000.0;
//                            fout.write(String.format("%f,%s\n", runtime, time));
//                            System.out.printf("Iteration: %d, Runtime: %f\n", iteration, runtime);
//                        }
//                    }

                    fout.flush();
                    // 원래는 50개마다 함
//                    if (iteration % 50 == 0) {
//                        if (Config.useCache)
//                            kmbr.updateCacheBits();
//
//                        long startTime = System.currentTimeMillis();
//                        kmbr.find();
//                        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
//                        bout.write(String.format("%f,%s\n", runtime, time));
//                        System.out.printf("Iteration: %d, Runtime: %f\n", iteration, runtime);
//                    }
                }
                else {
                    break;
                }
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    // qualitative experiment
    public void run2(final String csvFilePath, String outputPath) {
        kmbr.createFinder();
        DataWriter writer = new DataWriter(outputPath);

        try (FileReader fin = new FileReader(csvFilePath);
             BufferedReader bin = new BufferedReader(fin, 1024)) {

            String line;
            int iteration = 0;

            Random random = new Random();

            while ((line = bin.readLine()) != null) {
                String[] split = line.split(" ");
                iteration += 1;

                if (split[0].equals("create")) {
                    if (iteration % 10 == 0) {

                        double x = Double.parseDouble(split[2]) + random.nextDouble()*8e-4 - 4e-4 + 1000;
                        double y = Double.parseDouble(split[3]) + random.nextDouble()*8e-4 - 4e-4 + 1000;
                        String time = split[4] + " " + split[5];

                        ClusterAssignment p = db.newPoint(x, y, 0);
                        kmbr.addPoint(p.getPoint());
                        writer.write(EventType.CREATE, p.getPoint().getId(), x, y);

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
}

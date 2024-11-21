package edu.hanyang.smallest2019;

import edu.hanyang.kmbr.Config;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CovidSimulationApp {

    public static void main(String[] args) {
        new CovidSimulationApp().run(
                "data/covid-19-in-the-american-continent/parsed/covid_american_streaming_2020-1.txt",
                String.format("logs/related_works/smallest2019/covid_american_streaming_k%d_2020-1.txt", Config.K)
        );

        new CovidSimulationApp().run(
                "data/covid-19-in-the-american-continent/parsed/covid_american_streaming_2020-7.txt",
                String.format("logs/related_works/smallest2019/covid_american_streaming_k%d_2020-7.txt", Config.K)
        );

        new CovidSimulationApp().run(
                "data/covid-19-in-the-american-continent/parsed/covid_american_streaming_2021-1.txt",
                String.format("logs/related_works/smallest2019/covid_american_streaming_k%d_2021-1.txt", Config.K)
        );

        new CovidSimulationApp().run(
                "data/covid-19-in-the-american-continent/parsed/covid_american_streaming_2021-7.txt",
                String.format("logs/related_works/smallest2019/covid_american_streaming_k%d_2021-7.txt", Config.K)
        );
    }

    private DatabaseInteractor db;

    public CovidSimulationApp() {
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
                        Smallest2019 smallest2019 = new Smallest2019(clusterAssignments);
                        smallest2019.find();
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

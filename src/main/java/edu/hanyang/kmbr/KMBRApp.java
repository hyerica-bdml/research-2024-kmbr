package edu.hanyang.kmbr;

import edu.hanyang.kmbr.commands.*;
import edu.hanyang.kmbr.database.Database;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.database.PointFactory;
import edu.hanyang.kmbr.domain.ClusterAssignment;

import java.util.*;

public class KMBRApp {

    public static void main(String[] args) {
        new KMBRApp().run();
    }

    private DatabaseInteractor db;
    private KMBRInteractor kmbr;
    private boolean running;

    private List<Command> commands;
    private List<ClusterAssignment> points;

    public KMBRApp() {
        db = DatabaseInteractor.getInstance();
        kmbr = KMBRInteractor.getInstance();
        commands = new LinkedList<>();
        points = new LinkedList<>();
        kmbr.createFinder();
        running = true;
    }

    public void run() {
        Scanner in = new Scanner(System.in);
        Thread commandThread = createCommandThread();

        try {
            while (running) {
                String[] parsedInput = getInput(in);

                try {
                    if (parsedInput[0].equals("print")) {
                        if (parsedInput[1].equals("tree"))
                            kmbr.printTree(0);
                        else if (parsedInput[1].equals("height"))
                            kmbr.printHeights(0);
                        else if (parsedInput[1].equals("cachebits"))
                            kmbr.printCacheBits();
                        else if (parsedInput[1].equals("dirty"))
                            kmbr.printDirtyProbs();
                        else if (parsedInput[1].equals("cache"))
                            kmbr.printCache();

                    } else if (parsedInput[0].equals("insert")) {
                        double x = Double.parseDouble(parsedInput[1]);
                        double y = Double.parseDouble(parsedInput[2]);

                        commands.add(new AddPointCommand(db, kmbr, x, y));
                    } else if (parsedInput[0].equals("compute")) {
                        if (Config.useCache)
                            commands.add(new UpdateCacheBitCommand(kmbr));
                        commands.add(new ComputeKMBRCommand(kmbr));
                    } else if (parsedInput[0].equals("generate")) {
                        if (parsedInput[1].equals("new")) {
                            this.points.clear();
                            int numOfPoints = Integer.parseInt(parsedInput[2]);
                            int numOfClusters = Integer.parseInt(parsedInput[3]);

                            double xLowerLimit = 0.0, xUpperLimit = 100.0;
                            double yLowerLimit = 0.0, yUpperLimit = 100.0;
                            double stdLowerLimit = 1.0, stdUpperLimit = 5.0;

                            if (parsedInput.length > 4) {
                                xLowerLimit = Double.parseDouble(parsedInput[4]);
                                xUpperLimit = Double.parseDouble(parsedInput[5]);
                                yLowerLimit = Double.parseDouble(parsedInput[6]);
                                yUpperLimit = Double.parseDouble(parsedInput[7]);

                                if (parsedInput.length > 8) {
                                    stdLowerLimit = Double.parseDouble(parsedInput[8]);
                                    stdUpperLimit = Double.parseDouble(parsedInput[9]);
                                }
                            }

                            commands.add(new GenerateRandomPointsCommand(
                                    this,
                                    db,
                                    kmbr,
                                    numOfPoints,
                                    numOfClusters,
                                    new double[] {xLowerLimit, xUpperLimit},
                                    new double[] {yLowerLimit, yUpperLimit},
                                    new double[] {stdLowerLimit, stdUpperLimit},
                                    true
                            ));
                        }
                        else {
                            int numOfPoints = Integer.parseInt(parsedInput[1]);
                            int numOfClusters = Integer.parseInt(parsedInput[2]);

                            double xLowerLimit = 0.0, xUpperLimit = 100.0;
                            double yLowerLimit = 0.0, yUpperLimit = 100.0;
                            double stdLowerLimit = 1.0, stdUpperLimit = 5.0;

                            if (parsedInput.length > 3) {
                                xLowerLimit = Double.parseDouble(parsedInput[3]);
                                xUpperLimit = Double.parseDouble(parsedInput[4]);
                                yLowerLimit = Double.parseDouble(parsedInput[5]);
                                yUpperLimit = Double.parseDouble(parsedInput[6]);

                                if (parsedInput.length > 7) {
                                    stdLowerLimit = Double.parseDouble(parsedInput[7]);
                                    stdUpperLimit = Double.parseDouble(parsedInput[8]);
                                }
                            }

                            commands.add(new GenerateRandomPointsCommand(
                                    this,
                                    db,
                                    kmbr,
                                    numOfPoints,
                                    numOfClusters,
                                    new double[] {xLowerLimit, xUpperLimit},
                                    new double[] {yLowerLimit, yUpperLimit},
                                    new double[] {stdLowerLimit, stdUpperLimit},
                                    false
                            ));
                        }
                    } else if (parsedInput[0].equals("move")) {
                        int numOfClusters = Integer.parseInt(parsedInput[1]);
                        int minMove = Integer.parseInt(parsedInput[2]);
                        int maxMove = Integer.parseInt(parsedInput[3]);
                        commands.add(new MoveCommand(this, kmbr, db, minMove, maxMove, numOfClusters));
                    } else if (parsedInput[0].equals("clear")) {
                        commands.add(new ClearCommand(kmbr));
                    } else if (parsedInput[0].equals("update")) {
                        commands.add(new UpdateCacheBitCommand(kmbr));
                    } else if (parsedInput[0].equals("exit")) {
                        stop();
                    }
                } catch (RuntimeException exc) {
                    System.out.println();
                    exc.printStackTrace();
                    System.out.println();
                }
            }
        } finally {
            stop();

            try {
                commandThread.join(1000);
            } catch (InterruptedException ignored) {}
        }
    }

    public Thread createCommandThread() {
        Thread th = new Thread(() -> {
            while (running) {
                if (commands.size() > 0) {
                    Command command = commands.remove(0);
                    command.execute();
                }
                Thread.yield();
            }
        });
        th.start();
        return th;
    }

    public void stop() {
        running = false;
    }

    protected String[] getInput(Scanner in) {
        System.out.print("INPUT> ");
        String input = in.nextLine();
        return parseInput(input);
    }

    protected String[] parseInput(String input) {
        input = input.replaceAll("  ", " ").trim().toLowerCase(Locale.ROOT);
        return input.split(" ");
    }

    public void addClusterAssignments(final ClusterAssignment[] points) {
        this.points.addAll(Arrays.asList(points));
    }

    public List<ClusterAssignment> getPoints() {
        return points;
    }
}

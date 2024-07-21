package edu.hanyang.kmbr;

public class Config {

    public static final int K = Integer.parseInt(System.getenv().getOrDefault("K", "10"));
//    public static final int POINTSET_SIZE = Integer.parseInt(System.getenv().getOrDefault("POINTSET_SIZE", "15"));
    public static final int POINTSET_SIZE = K;
    public static final int INITIAL_ARRAY_SIZE = Integer.parseInt(System.getenv().getOrDefault("INITIAL_ARRAY_SIZE", "4096"));
    public static final boolean useCache = Boolean.parseBoolean(System.getenv().getOrDefault("USE_CACHE", "true"));
    public static final String kmbrSimulationFile = System.getenv().getOrDefault("KMBR_SIMULATION_FILE", "data/points/simulation.txt");
    public static final int NUM_OF_POINTS = Integer.parseInt(System.getenv().getOrDefault("NUM_OF_POINTS", "300"));
    public static final int NUM_OF_GROUPS = Integer.parseInt(System.getenv().getOrDefault("NUM_OF_GROUPS", "10"));
    public static final double INITIAL_ALPHA = Double.parseDouble(System.getenv().getOrDefault("INITIAL_ALPHA", "1.0"));
    public static final double FIXED_DIRTY_PROB = Double.parseDouble(System.getenv().getOrDefault("FIXED_DIRTY_PROB", "-1.0"));

    private Config() {}
}

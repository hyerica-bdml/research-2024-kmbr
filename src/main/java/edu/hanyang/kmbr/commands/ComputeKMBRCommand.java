package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRInteractor;

public class ComputeKMBRCommand implements Command {

    private KMBRInteractor kmbr;

    public ComputeKMBRCommand(final KMBRInteractor kmbr) {
        this.kmbr = kmbr;
    }

    @Override
    public void execute() {
        long startTime = System.currentTimeMillis();
        double size = kmbr.find().size();
        double runtime = (System.currentTimeMillis() - startTime)/1000.0;
        System.out.println("The kMBR size is " + size + " (runtime: " + runtime + ").");
    }
}

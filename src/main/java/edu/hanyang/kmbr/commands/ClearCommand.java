package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRInteractor;

public class ClearCommand implements Command {

    private KMBRInteractor kmbr;

    public ClearCommand(final KMBRInteractor kmbr) {
        this.kmbr = kmbr;
    }

    @Override
    public void execute() {
        kmbr.createFinder();
    }
}

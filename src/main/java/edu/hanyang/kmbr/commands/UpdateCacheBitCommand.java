package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRInteractor;

public class UpdateCacheBitCommand implements Command {

    private KMBRInteractor kmbr;

    public UpdateCacheBitCommand(final KMBRInteractor kmbr) {
        this.kmbr = kmbr;
    }

    @Override
    public void execute() {
        this.kmbr.updateCacheBits();
    }
}

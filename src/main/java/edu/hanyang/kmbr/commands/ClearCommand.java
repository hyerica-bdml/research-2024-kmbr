package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.database.DatabaseInteractor;

public class ClearCommand implements Command {

    private KMBRInteractor kmbr;
    private DatabaseInteractor db;

    public ClearCommand(final KMBRInteractor kmbr, final DatabaseInteractor db) {
        this.kmbr = kmbr;
        this.db = db;
    }

    @Override
    public void execute() {
        kmbr.createFinder();
        db.clearPoints();
    }
}

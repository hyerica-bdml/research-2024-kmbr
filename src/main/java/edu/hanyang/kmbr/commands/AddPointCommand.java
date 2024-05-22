package edu.hanyang.kmbr.commands;

import edu.hanyang.kmbr.KMBRInteractor;
import edu.hanyang.kmbr.database.DatabaseInteractor;
import edu.hanyang.kmbr.domain.Point;

public class AddPointCommand implements Command {

    private final DatabaseInteractor db;
    private final KMBRInteractor kmbr;

    private final double x, y;

    public AddPointCommand(final DatabaseInteractor db,
                           final KMBRInteractor kmbr,
                           final double x,
                           final double y) {
        this.db = db;
        this.kmbr = kmbr;
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute() {
        Point point = db.newPoint(x, y);
        kmbr.addPoint(point);
    }
}

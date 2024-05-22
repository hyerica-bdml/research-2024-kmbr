package edu.hanyang.kmbr.database;

import edu.hanyang.kmbr.domain.Point;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.TreeMap;

public class Database implements Externalizable {

    private Map<Long, Point> points;
    private long maxPointId;

    public Database() {
        points = new TreeMap<>();
        maxPointId = 0;
    }

    public void addPoint(final Point p) {
        points.put(p.getId(), p);
        if (p.getId() > maxPointId) maxPointId = p.getId();
    }

    public Point getPointById(final long id) {
        return points.getOrDefault(id, null);
    }

    public void removePoint(final Point p) {
        points.remove(p.getId());
    }

    public void removePoint(final long id) {
        points.remove(id);
    }

    public Point[] getPoints() {
        Point[] pointArr = new Point[points.size()];
        points.values().toArray(pointArr);
        return pointArr;
    }

    public long getMaxPointId() {
        return maxPointId;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();

        for (int i = 0; i < size; i++) {
            Point p = (Point) in.readObject();
            points.put(p.getId(), p);

            if (p.getId() > maxPointId) maxPointId = p.getId();
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(points.size());
        points.forEach((id, p) -> {
            try {
                out.writeObject(p);
            } catch (IOException ignored) {}
        });
    }
}

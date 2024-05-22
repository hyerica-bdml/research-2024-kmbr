package edu.hanyang.kmbr.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DataWriter {

    private BufferedWriter writer;

    public DataWriter(final String fileName) {
        try {
            FileWriter fwriter = new FileWriter(fileName);
            writer = new BufferedWriter(fwriter);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void write(EventType type, final long id, final double x, final double y) {
        try {
            writer.write(String.format("%s %d %f %f\n", type.toString(), id, x, y));
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void writeMBR(final long[] ids) {
        try {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("mbr");

            for (long id: ids) {
                strBuilder.append(" ");
                strBuilder.append(id);
            }
            strBuilder.append("\n");

            writer.write(strBuilder.toString());
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}

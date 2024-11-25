package edu.hanyang.kmbr.utils;

import edu.hanyang.kmbr.Config;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

public class DynamicDoubleArray implements Externalizable {

    private Entry[] array;
    private int size;

    public DynamicDoubleArray() {
        array = new Entry[Config.INITIAL_ARRAY_SIZE];
        size = 0;
    }

    public void set(int index, double value) {
        while (index >= array.length)
            expandArray();

        array[index] = new Entry(value);

        if (index >= size) size = index + 1;
    }

    public Entry get(int index) {
        if (index >= size)
            throw new RuntimeException("Index out of range: array size is " + size + ", and index is " + index);

        return array[index];
    }

    public double getValue(int index) {
        return get(index).value;
    }

    public int size() {
        return size;
    }

    public boolean exists(int index) {
        return index < size && array[index] != null;
    }

    public void remove(int index) {
        if (index >= size)
            throw new RuntimeException("Index out of range: array size is " + size + ", and index is " + index);

        array[index] = null;

        if (index + 1 == size) {
            while (array[index] != null) index--;
            size = index + 1;
        }

        if (size < array.length / 4) shrinkArray();
    }

    private void expandArray() {
        this.array = Arrays.copyOf(array, array.length*2);
    }

    private void shrinkArray() {
        this.array = Arrays.copyOf(array, array.length/2);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size);
        for (int i = 0; i < size; i += 1) {
            if (array[i] != null) {
                out.writeInt(i);
                out.writeDouble(array[i].value);
            }
            else {
                out.writeInt(-1);
                out.writeDouble(-9999999);
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        size = in.readInt();
        for (int i = 0; i < size; i += 1) {
            int index = in.readInt();
            if (index >= 0) set(index, in.readDouble());
            else in.readDouble();
        }
    }

    public static class Entry {
        public double value;

        public Entry(double value) {
            this.value = value;
        }

        public Entry() {}
    }
}

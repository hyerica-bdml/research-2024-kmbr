package edu.hanyang.kmbr.utils;

public enum EventType {

    CREATE("create"), MOVE("move"), REMOVE("remove");

    private final String type;

    private EventType(final String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}

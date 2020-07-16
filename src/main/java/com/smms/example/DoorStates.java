package com.smms.example;

public interface DoorStates {
    int OPENED = 1;
    int LOCKED = 2;
    int CLOSED = 3;

    static String getState(int type) {
        switch (type) {
            case OPENED:
                return "OPENED";
            case LOCKED:
                return "LOCKED";
            case CLOSED:
                return "CLOSED";
            default:
                return null;
        }
    }
}

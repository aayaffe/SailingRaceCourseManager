package com.aayaffe.sailingracecoursemanager.communication;


/**
 * Created by aayaffe on 30/09/2015.
 */
public enum ObjectTypes {
    RaceManager(0),
    WorkerBoat(1),
    Buoy(2),
    FlagBuoy(3),
    TomatoBuoy(4),
    TriangleBuoy(5),
    StartLine(6),
    FinishLine(7),
    StartFinishLine(8),
    Gate(9),
    ReferencePoint(10),
    Satellite(11),
    Other(12);

    private int value;

    ObjectTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
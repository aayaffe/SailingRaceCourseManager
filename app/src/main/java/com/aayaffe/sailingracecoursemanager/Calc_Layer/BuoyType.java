package com.aayaffe.sailingracecoursemanager.Calc_Layer;
import java.io.Serializable;
/**
 * Created by Jonathan on 27/10/2016.
 */
public enum BuoyType {
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
    Other(11);
    private int value;
    BuoyType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}

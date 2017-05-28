package com.aayaffe.sailingracecoursemanager.calclayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Jonathan on 27/10/2016.
 */
public enum BuoyType {
    RACE_OFFICER(0),
    MARK_LAYER(1),
    BUOY(2),
    FLAG_BUOY(3),
    TOMATO_BUOY(4),
    TRIANGLE_BUOY(5),
    START_LINE(6),
    FINISH_LINE(7),
    START_FINISH_LINE(8),
    GATE(9),
    REFERENCE_POINT(10),
    OTHER(11);
    private int value;
    BuoyType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
    public static List<String> getBuoyTypes(){
        ArrayList<String> ret = new ArrayList<>();
        ret.add(BUOY.toString());
        ret.add(FLAG_BUOY.toString());
        ret.add(TOMATO_BUOY.toString());
        ret.add(TRIANGLE_BUOY.toString());
        return ret;
    }
}

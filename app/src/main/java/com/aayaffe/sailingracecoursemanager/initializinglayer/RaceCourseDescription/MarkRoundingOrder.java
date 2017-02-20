package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import java.util.List;

/**
 * Created by aayaffe on 18/02/2017.
 */

/**
 * Denotes a rounding order of a race course
 */
public class MarkRoundingOrder {
    private String name;
    private List<Integer> marks;

    public MarkRoundingOrder(String name, List<Integer> marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getMarks() {
        return marks;
    }


}

package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

/**
 * Created by aayaffe on 23/04/2016.
 */
public class RaceCourseMark extends RaceCourseObject {

    public RaceCourseMark(AviLocation loc) {
        super(loc);
        type = ObjectTypes.Buoy;
    }
}

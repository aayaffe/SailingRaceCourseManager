package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

/**
 * Created by aayaffe on 23/04/2016.
 */
public class RaceCourseStartLine extends RaceCourseObjectLong {
    public RaceCourseStartLine(AviLocation stbd, AviLocation port, String name) {
        super(stbd, port,name);
        type = ObjectTypes.StartLine;
    }
}

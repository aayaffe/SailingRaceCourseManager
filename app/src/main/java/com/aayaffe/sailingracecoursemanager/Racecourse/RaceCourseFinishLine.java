package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

/**
 * Created by aayaffe on 23/04/2016.
 */
public class RaceCourseFinishLine extends RaceCourseObjectLong {
    public RaceCourseFinishLine(AviLocation stbd, AviLocation port, String name) {
        super(stbd, port,name);
        type = ObjectTypes.FinishLine;
    }
}

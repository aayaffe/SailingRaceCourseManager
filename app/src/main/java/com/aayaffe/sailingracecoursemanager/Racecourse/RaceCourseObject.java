package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aayaffe on 23/04/2016.
 */
public abstract class RaceCourseObject {
    AviLocation stbd;
    AviLocation port;
    AviLocation center;

    ObjectTypes type;
    public AviLocation getLoc(){
        return center;
    }

    public ObjectTypes getType(){
        return type;
    }

    public RaceCourseObject(AviLocation loc){
        center = loc;
    }

}

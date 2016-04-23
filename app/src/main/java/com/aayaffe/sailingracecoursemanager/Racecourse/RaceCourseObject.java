package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.List;

/**
 * Created by aayaffe on 23/04/2016.
 */
public abstract class RaceCourseObject {
    List<AviLocation> locs = null;
    ObjectTypes type;
    public AviLocation getLoc(){
        return locs.get(0);
    }

    public ObjectTypes getType(){
        return type;
    }

    public RaceCourseObject(AviLocation loc){
        locs.add(loc);
    }

}

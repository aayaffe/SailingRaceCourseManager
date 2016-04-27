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



    String name;

    ObjectTypes type;
    public AviLocation getLoc(){
        return center;
    }

    public ObjectTypes getType(){
        return type;
    }

    public RaceCourseObject(AviLocation loc, String name){
        center = loc;this.name = name;
    }

    public String getName() {
        return name;
    }

}

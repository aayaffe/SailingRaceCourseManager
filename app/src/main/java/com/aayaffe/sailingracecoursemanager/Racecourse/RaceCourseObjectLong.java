package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

/**
 * Created by aayaffe on 23/04/2016.
 */
public abstract class RaceCourseObjectLong extends RaceCourseObject {
    public AviLocation getStbLoc(){
        return stbd;
    }

    public AviLocation getPrtLoc(){
        return port;
    }

    @Override
    public AviLocation getLoc(){
        return GeoUtils.getLocationFromDirDist(getStbLoc(), (int)getDirection(), (int)getLength() / 2);
    }

    /***
     *
     * @return the object length in meters
     */
    public int getLength(){
        return (int)getStbLoc().distanceTo(getPrtLoc());
    }

    /***
     *
     * @return The direction in degrees from true north of the object from right to left. -1 if a point object;
     */
    public float getDirection(){
        return (int)getStbLoc().bearingTo(getPrtLoc());
    }

    public RaceCourseObjectLong(AviLocation stbd, AviLocation port){
        super(null);
        this.stbd = stbd;
        this.port = port;
    }

}

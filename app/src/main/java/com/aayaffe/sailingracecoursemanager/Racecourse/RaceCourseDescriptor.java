package com.aayaffe.sailingracecoursemanager.Racecourse;

import android.support.annotation.Nullable;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import java.util.List;

/**
 * Created by aayaffe on 22/04/2016.
 */
public  class RaceCourseDescriptor {
    public List<ObjectTypes> objects;
    List<DirDist> dirDist;
    boolean startFinishSeparate;
    int finishLineLength = 55;
    int iterator = 0;
    AviLocation lastLoc;
    public RaceCourseDescriptor(List<ObjectTypes> objects, List<DirDist> dirDistList){
        this.objects = objects;
        this.dirDist = dirDistList;
    }
    public RaceCourseObject getFirst(AviLocation loc, int windDir, int startLineLength){
        iterator = 0;
        lastLoc = loc;
        return generateObject(objects.get(iterator++),loc,GeoUtils.relativeToTrueDirection(windDir,-90),startLineLength);
    }

    public RaceCourseObject getNext(int commonLength, int windDir, int startLineLength){
        if (iterator>objects.size()-1)
            return null;
        AviLocation loc = GeoUtils.getLocationFromDirDist(lastLoc,dirDist.get(iterator-1).direction,getDistance(dirDist.get(iterator-1),commonLength));
        lastLoc = loc;
        return generateObject(objects.get(iterator++),loc,GeoUtils.relativeToTrueDirection(windDir,-90),startLineLength);
    }

    private int getDistance(DirDist dirDist, int commonLength) {
        if (dirDist.isDistAbsolute)
            return dirDist.distAbsolute;
        return (int)(commonLength*dirDist.distFractionional);
    }

    @Nullable
    private RaceCourseObject generateObject(ObjectTypes type, AviLocation loc, int objectDir, int objectLength){
        switch (type){
            case StartLine:
                return new RaceCourseStartLine(getStbd(loc,objectDir,objectLength),getPort(loc,objectDir,objectLength));
            case Buoy:
                return new RaceCourseMark(loc);
            case Gate:
                return new RaceCourseGate(getStbd(loc,objectDir,finishLineLength),getPort(loc,objectDir,finishLineLength));
            case FinishLine:
                return new RaceCourseFinishLine(getStbd(loc,objectDir,finishLineLength),getPort(loc,objectDir,finishLineLength));
            case StartFinishLine:
                return new RaceCourseStartFinishLine(getStbd(loc,objectDir,objectLength),getPort(loc,objectDir,objectLength));
            default:
                return null;
        }
    }

    private AviLocation getStbd(AviLocation loc, int objectDir, int objectLength){
        return GeoUtils.getLocationFromDirDist(loc, GeoUtils.relativeToTrueDirection(objectDir,180), objectLength/2);
    }

    private AviLocation getPort(AviLocation loc, int objectDir, int objectLength){
        return GeoUtils.getLocationFromDirDist(loc, objectDir, objectLength/2);
    }


}

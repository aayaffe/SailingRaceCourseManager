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
    List<String> names;
    //boolean startFinishSeparate;
    int finishLineLength = 55;
    int iterator = 0;
    AviLocation lastLoc;
    public RaceCourseDescriptor(List<ObjectTypes> objects, List<DirDist> dirDistList, List<String> names){
        this.objects = objects;
        this.dirDist = dirDistList;
        this.names = names;

    }

    /***
     *
     * @param loc - The center location of the first element in the race course (StartLine)
     * @param windDir - The direction from which the wind comes from
     * @param startLineLength - the start line length
     * @return the first object of the race (The start line)
     */
    public RaceCourseObject getFirst(AviLocation loc, int windDir, int startLineLength){
        iterator = 0;
        lastLoc = loc;
        return generateObject(objects.get(iterator),loc,GeoUtils.relativeToTrueDirection(windDir,-90),startLineLength, names.get(iterator));
    }

    /***
     *
     * @param commonLength - the length from which the legs' fractional length will be calculated
     * @param windDir - The direction from which the wind comes from
     * @param startLineLength - the start line length //TODO remove for there should never be a start line as the next element
     * @return the next object of the race, null if no more elements
     */
    @Nullable
    public RaceCourseObject getNext(int commonLength, int windDir, int startLineLength){
        iterator++;
        if (iterator>objects.size()-1)
            return null;
        AviLocation loc = GeoUtils.getLocationFromDirDist(lastLoc,dirDist.get(iterator-1).direction,getDistance(dirDist.get(iterator-1),commonLength));
        lastLoc = loc;
        return generateObject(objects.get(iterator),loc,GeoUtils.relativeToTrueDirection(windDir,-90),startLineLength,names.get(iterator));

    }

    @org.jetbrains.annotations.Contract(pure = true)
    private int getDistance(DirDist dirDist, int commonLength) {
        if (dirDist.isDistAbsolute)
            return dirDist.distAbsolute;
        return (int)(commonLength*dirDist.distFractionional);
    }

    @Nullable
    private RaceCourseObject generateObject(ObjectTypes type, AviLocation loc, int objectDir, int objectLength, String name){
        switch (type){
            case StartLine:
                return new RaceCourseStartLine(getStbd(loc,objectDir,objectLength),getPort(loc,objectDir,objectLength),name);
            case Buoy:
                return new RaceCourseMark(loc,name);
            case Gate:
                return new RaceCourseGate(getStbd(loc,objectDir,finishLineLength),getPort(loc,objectDir,finishLineLength),name);
            case FinishLine:
                return new RaceCourseFinishLine(getStbd(loc,objectDir,finishLineLength),getPort(loc,objectDir,finishLineLength),name);
            case StartFinishLine:
                return new RaceCourseStartFinishLine(getStbd(loc,objectDir,objectLength),getPort(loc,objectDir,objectLength),name);
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

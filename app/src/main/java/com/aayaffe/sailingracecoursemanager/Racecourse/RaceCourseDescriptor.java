package com.aayaffe.sailingracecoursemanager.Racecourse;

import android.support.annotation.Nullable;

import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by aayaffe on 22/04/2016.
 */
public  class RaceCourseDescriptor implements Iterable<RaceCourseObject>{
    private final String type;
    private final AviLocation startLineLoc;

    public AviLocation getStartLineLoc() {
        return startLineLoc;
    }

    public int getCommonLength() {
        return commonLength;
    }

    public int getWindDir() {
        return windDir;
    }

    public List<ObjectTypes> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectTypes> objects) {
        this.objects = objects;
    }

    public List<DirDist> getDirDist() {
        return dirDist;
    }

    public void setDirDist(List<DirDist> dirDist) {
        this.dirDist = dirDist;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public int getFinishLineLength() {
        return finishLineLength;
    }

    public void setFinishLineLength(int finishLineLength) {
        this.finishLineLength = finishLineLength;
    }

    public int getIterator() {
        return iterator;
    }

    public void setIterator(int iterator) {
        this.iterator = iterator;
    }

    public AviLocation getLastLoc() {
        return lastLoc;
    }

    public void setLastLoc(AviLocation lastLoc) {
        this.lastLoc = lastLoc;
    }

    private final int commonLength;
    private final int windDir;

    public int getStartLineLength() {
        return startLineLength;
    }

    private final int startLineLength;
    public List<ObjectTypes> objects;
    List<DirDist> dirDist;
    List<String> names;
    //boolean startFinishSeparate;
    int finishLineLength = 55;
    int iterator = 0;
    AviLocation lastLoc;

    public String getType() {
        return type;
    }

    /***
     *
     * @param startlineLoc - The center location of the first element in the race course (StartLine)
     * @param windDir - The direction from which the wind comes from
     * @param commonLength - the length from which the legs' fractional length will be calculated
     * @param startLineLength - the start line length //TODO remove for there should never be a start line as the next element
     * @return the first object of the race (The start line)
     */
    public RaceCourseDescriptor(String type, List<ObjectTypes> objects, List<DirDist> dirDistList, List<String> names, AviLocation startlineLoc, int windDir, int startLineLength, int commonLength){
        this.type = type;
        this.objects = objects;
        this.dirDist = dirDistList;
        this.names = names;
        this.startLineLoc = startlineLoc;
        this.windDir = windDir;
        this.startLineLength = startLineLength;
        this.commonLength = commonLength;
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


    @Override
    public Iterator<RaceCourseObject> iterator() {
        Iterator<RaceCourseObject> it = new Iterator<RaceCourseObject>() {
            private int currentIndex = 0;
            @Override
            public boolean hasNext() {
                return currentIndex < objects.size() && objects.get(currentIndex) != null;
            }

            @Override
            public RaceCourseObject next() {
                if (lastLoc!=null) {
                    lastLoc = GeoUtils.getLocationFromDirDist(lastLoc, GeoUtils.relativeToTrueDirection(windDir,dirDist.get(currentIndex - 1).direction), getDistance(dirDist.get(currentIndex - 1), commonLength));
                }else{
                    lastLoc = startLineLoc;
                }
                int objectDir = GeoUtils.relativeToTrueDirection(windDir,-90);
                if (objects.get(currentIndex)==ObjectTypes.FinishLine){ //TODO assuming finish line does not come first - add checks for race course
                    objectDir = GeoUtils.relativeToTrueDirection(windDir,-90+dirDist.get(currentIndex - 1).direction);
                }
                return generateObject(objects.get(currentIndex),lastLoc,objectDir,startLineLength,names.get(currentIndex++));
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
}

package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.calclayer.BuoyType;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 29/12/2016.
 */
public class Legs implements Serializable{
    private static final String TAG = "LEGS";
    public String name;
    public List<Double> lengthFactors = Collections.emptyList();
    public List<Mark> marks = Collections.emptyList();
    public List<MarkRoundingOrder> markRoundingOptions;
    public MarkRoundingOrder defaultMarkRounding;

    private UUID uuid;

    public Legs(){
        uuid = UUID.randomUUID();
    }

    public void setUuidString(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getUuidString() {
        return this.uuid.toString();
    }

    public String getName() {
        return name;
    }

    public List<Double> getCourseFactors() {
        return lengthFactors;
    }

    /***
     * Returns the distance of a course (relative to distance to m1 parts or absolute parts) according
     * to the MarkRoundingOrder and specific point of sail
     * @param mro desired Mark rounding order
     * @param pos desired Point of sail
     * @param dt Relative parts or absolute parts.
     * @return The distance required (absolute in NM or relative to distance to M1)
     */
    @Exclude
    public double GetLength(MarkRoundingOrder mro, Boat.PointOfSail pos, DistanceType dt) throws RaceCourseException {
        double abs = 0;
        double rel = 0;
        Mark last = marks.get(0);
        for(int id: mro.getMarks()) {
            for (Mark m : marks) {
                if (m.id == id){
                    Distance d = markDistance(last.id,m.id);
                    if (d==null){
                        Log.d(TAG,"Unable to calc distance");
                        throw new RaceCourseException("Unable to calculate course length");
                    }
                    if (Boat.dir2PointOfSail(markDir(last.id,m.id))==pos) {
                        switch (d.dt) {
                            case Relative:
                                rel += d.dist;
                                break;
                            case Absolute:
                                abs += d.dist;
                                break;
                        }
                    }
                    last = m;
                }
            }
        }
        return dt==DistanceType.Absolute?abs:rel;
    }

    private int markDir(int m1, int m2) {
        int ret = -1;
        for (Mark m : marks) {
            if (m.id == m1) {
                if (m.ml.fromMarkId == m2){
                    return (m.ml.direction - 180)<0? m.ml.direction - 180 + 360:m.ml.direction - 180;
                }
            }
            if (m.id == m2) {
                if (m.ml.fromMarkId == m1){
                    return (m.ml.direction)<0? m.ml.direction + 360:m.ml.direction;
                }
            }
        }
        return ret;
    }

    private Distance markDistance(int m1, int m2) {
        Distance ret = null;
        for (Mark m : marks) {
            if (m.id == m1) {
                if (m.ml.fromMarkId == m2){
                    ret = new Distance();
                    ret.dt = m.ml.relativeDistance?DistanceType.Relative:DistanceType.Absolute;
                    ret.dist = m.ml.distance;
                    return ret;
                }
            }
            if (m.id == m2) {
                if (m.ml.fromMarkId == m1){
                    ret = new Distance();
                    ret.dt = m.ml.relativeDistance?DistanceType.Relative:DistanceType.Absolute;
                    ret.dist = m.ml.distance;
                    return ret;
                }
            }
        }
        return ret;
    }
    public List<Mark> getOptions() {
        List<Mark> ret = new ArrayList<>();
        for(Mark m : marks){
            if (m!=null&&m.go!=null&&m.go.gateOption!=null&&m.go.gateOption == GateOption.GATABLE)
                    ret.add(m);
        }
        return ret;
    }

    /**
     * referencePoint - the referencePoint location. NOT SIGNAL BOAT
     * multiplication - known also as dist2m1 (the distance toward the first mark)
     * windDir  -wind direction
     *
     * Each Mark is a tree root to marks that uses it's location, so this function must act recursively
     */
    @Exclude
    public List<DBObject> parseBuoys(AviLocation rcLocation, double dist2m1, int windDir, float startLineLength,float gateLength, UUID raceCourseUUID, Map<String, Boolean> selectedOptions) throws RaceCourseException {  //parses the mark and his sons into buoys
        List<DBObject> buoys = new ArrayList<>();
        Map<Integer,AviLocation> id2Location = new HashMap();
        AviLocation lastLoc = rcLocation;
        for (Mark m: marks){
            AviLocation loc = new AviLocation();
            if (m!=null) {
                if (m.ml!=null){
                    switch (m.ml.locationOptions){
//                        case FROM_LAST_MARK:
//                            if (m.ml.relativeDistance){
//                                loc = GeoUtils.getLocationFromDirDist(lastLoc,m.ml.direction+windDir,m.ml.distance*dist2m1);
//                            }
//                            else{
//                                loc = GeoUtils.getLocationFromDirDist(lastLoc,m.ml.direction+windDir,m.ml.distance);
//                            }
//                            lastLoc = loc;
//                            break;
                        case FROM_RACE_COMMITTEE:
                            if (m.ml.relativeDistance){
                                loc = GeoUtils.getLocationFromDirDist(rcLocation,m.ml.direction+windDir,m.ml.distance*dist2m1);
                            }
                            else{
                                loc = GeoUtils.getLocationFromDirDist(rcLocation,m.ml.direction+windDir,m.ml.distance);
                            }
                            lastLoc = loc; //TODO Assert correctness
                            break;
                        case FROM_MARK_ID:
                            AviLocation refMark = id2Location.get(m.ml.fromMarkId);
                            if (refMark ==null)
                                throw new RaceCourseException("Wrong mark order (missing refmark location)");
                            if (m.ml.relativeDistance){
                                loc = GeoUtils.getLocationFromDirDist(refMark,m.ml.direction+windDir,m.ml.distance*dist2m1);
                            }
                            else{
                                loc = GeoUtils.getLocationFromDirDist(refMark,m.ml.direction+windDir,m.ml.distance);
                            }
                            lastLoc = loc;
                            break;
                        case GEOGRAPHICAL:
                            throw new UnsupportedOperationException();
                    }

                }

                if (m.isGatable) {
                    if (m.go.gateType == GateType.START_FINISH_LINE || m.go.gateType==GateType.START_LINE){
                        lastLoc = GeoUtils.getLocationFromDirDist(lastLoc,m.ml.direction+windDir+m.go.gateDirection,startLineLength/2);
                    }
                    BuoyType port = getPortBuoyType(m);
                    BuoyType stbd = getStbdBuoyType(m);
                    switch (m.go.gateOption) {
                        case GATABLE:
                            if (selectedOptions.containsKey(m.name) && (selectedOptions.get(m.name))) { //isGatable true and option selected
                                buoys.addAll(getGateMarks(windDir, raceCourseUUID, m, gateLength, startLineLength, loc, port, stbd));
                            } else {
                                buoys.add(new DBObject(m.name, loc, BuoyType.TOMATO_BUOY, raceCourseUUID));
                            }
                            break;
                        case ALWAYS_GATED:
                            buoys.addAll(getGateMarks(windDir, raceCourseUUID, m, gateLength,startLineLength, loc, port, stbd));
                            break;
                        case NEVER_GATABLE:
                            buoys.add(new DBObject(m.name, loc, BuoyType.TOMATO_BUOY, raceCourseUUID));
                            break;
                    }
                } else
                    buoys.add(new DBObject(m.name, loc, BuoyType.TOMATO_BUOY, raceCourseUUID));
                id2Location.put(m.id,lastLoc);
            }
        }
        return buoys;
    }
    private BuoyType getPortBuoyType(Mark m){
        return getGateBuoyType(m, true);
    }
    private BuoyType getStbdBuoyType(Mark m){
        return getGateBuoyType(m, false);
    }
    private BuoyType getGateBuoyType(Mark m, boolean isPort) {
        BuoyType port;
        BuoyType stbd;
        switch (m.go.gateType){
            case FINISH_LINE:
                port =  BuoyType.FINISH_LINE;
                stbd =  BuoyType.FINISH_LINE;
                break;
            case START_LINE:
                port =  BuoyType.START_LINE;
                stbd =  BuoyType.START_LINE;
                break;
            case START_FINISH_LINE:
                port =  BuoyType.START_FINISH_LINE;
                stbd =  BuoyType.START_FINISH_LINE;
                break;
            case SATELLITE:
                switch (m.go.gateReference){
                    case LEFT_MARK:
                        stbd = BuoyType.TRIANGLE_BUOY;
                        port = BuoyType.TOMATO_BUOY;
                        break;
                    case GATE_CENTER:
                    case RIGHT_MARK:
                    default:
                        port = BuoyType.TRIANGLE_BUOY;
                        stbd = BuoyType.TOMATO_BUOY;
                        break;
                }
                break;
            case GATE:
            default:
                port = BuoyType.GATE;
                stbd = BuoyType.GATE;
                break;
        }
        return isPort?port:stbd;
    }

    private static List<DBObject> getGateMarks(int windDir, UUID raceCourseUUID, Mark m, double gateLength, double startLength, AviLocation loc, BuoyType portType, BuoyType stbdType) {
        List<DBObject> ret = new ArrayList<>();
        DBObject port;
        DBObject stbd;
        double width;
        if (m.go.gateRelativeWidth){
            if (m.go.gateType == GateType.START_FINISH_LINE || m.go.gateType == GateType.START_LINE) {
                width = startLength;
            }
            else width = gateLength;
        }
        else width = m.go.gateWidth;
        switch(m.go.gateReference) {
            case GATE_CENTER:
                port = new DBObject(m.name + "P", GeoUtils.getLocationFromDirDist(loc, windDir + m.ml.direction + m.go.gateDirection, width / 2), portType,raceCourseUUID);
                stbd = new DBObject(m.name + "S", GeoUtils.getLocationFromDirDist(loc, windDir + m.ml.direction + m.go.gateDirection - 180, width / 2), stbdType,raceCourseUUID);
                ret.add(port);
                ret.add(stbd);
                break;
            case LEFT_MARK:
                port = new DBObject(m.name + "P", loc, portType,raceCourseUUID);
                stbd = new DBObject(m.name + "S", GeoUtils.getLocationFromDirDist(loc, windDir + m.ml.direction - m.go.gateDirection, width), stbdType,raceCourseUUID);
                ret.add(port);
                ret.add(stbd);
                break;
            case RIGHT_MARK:
                port = new DBObject(m.name + "P", GeoUtils.getLocationFromDirDist(loc, windDir + m.ml.direction + m.go.gateDirection, width), portType,raceCourseUUID);
                stbd = new DBObject(m.name + "S", loc, stbdType,raceCourseUUID);
                ret.add(port);
                ret.add(stbd);
                break;
        }
        return ret;
    }
}

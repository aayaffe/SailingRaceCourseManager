package com.aayaffe.sailingracecoursemanager.Racecourse;

import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.aayaffe.sailingracecoursemanager.communication.ObjectTypes;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 23/04/2016.
 */
public class RaceCourseObject {
    AviLocation stbd;//jonathan:Remove
    AviLocation port;//jonathan:Remove
    public String name;
    public ObjectTypes type;
    public AviLocation center;
    private int internalDir=0; //direction of the port mark, from the main mark (satellite case) or from starboard mark, non-clockwise
    private double internalDist=0; //distance between the two internal marks (like 3star-3port, 1-1a, signalBoat-pinEnd)


    public RaceCourseObject(AviLocation loc, String name){  //jonathan:Remove
        center = loc;this.name = name;
    }

    public RaceCourseObject(String name, ObjectTypes type, AviLocation loc){
        this.center = loc;
        this.name = name;
        this. type = type;
    }
    public RaceCourseObject(String name, ObjectTypes type, AviLocation loc, int internalDir, double internalDist){
        this.center = loc;
        this.name = name;
        this. type = type;
        this.internalDir=internalDir;
        this.internalDist=internalDist;

    }

    public List<AviObject> getInternalObjects(int windDir, String color, UUID raceUUID){
    //this method returns the actual marks, AviObjects, as a list. this list can join many others to create one long list of all the marks.
        List<AviObject> MarksArray= new ArrayList<AviObject>();
        switch(type){
            case Buoy:
                MarksArray.add(new AviObject(this.name, center, type, color, raceUUID));
                break;
            case Satellite:
                MarksArray.add(new AviObject(this.name, center, type, color, raceUUID));
                MarksArray.add(new AviObject(this.name+"a", new AviLocation(center, windDir-internalDir, internalDist ), type, color, raceUUID));
                break;
            case Gate:
                MarksArray.add(new AviObject(this.name+"Strb", new AviLocation(center, windDir+internalDir, internalDist/2 ), type, color, raceUUID));
                MarksArray.add(new AviObject(this.name+"Port", new AviLocation(center, windDir-internalDir, internalDist/2 ), type, color, raceUUID));
                break;
            case ReferencePoint:
                break;
            default:
                MarksArray.add(new AviObject(this.name, center, type, color, raceUUID));
                break;
        }
        return MarksArray;
    }

    public AviLocation getLoc(){
        return center;
    }
    public ObjectTypes getType(){
        return type;
    }
    public String getName() {
        return name;
    }

}

package com.aayaffe.sailingracecoursemanager.Calc_Layer;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonathan on 23/07/2016.
 */
public class Mark {
    private String name;
    private int direction = 0; //direction from reference point. clockwise(usually minus, as a result);
    private double distance = 0;
    private boolean distanceFactor = false;  //to multiply the distance with Dist2m1 or not?

    public ArrayList<Mark> referedMarks;

    private boolean isGatable = false;
    private String gateType = "Buoy";  //TODO make it enum.
    private int gateDirection = (-90); //satellite direction from Main buoy OR port side direction from starboard side
    private double gateDistance = 0;  //distance between gate's buoys

    public Mark(String name) {
        this.name = name;
        referedMarks = new ArrayList<Mark>();
    }


    public void setName(String name) {
        if (name != null) this.name = name;
        else Log.w("Mark Class insertion", "null name set for Mark named:" + this.name);
    }

    public String getName() {
        return name;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        if (direction != null) this.direction = Integer.parseInt(direction);
        else Log.w("Mark Class insertion", "null direction set - Mark named:" + this.name);
    }

    public int getDirection() {
        return direction;
    }

    public void setDistance(double distance) {
        this.distance = this.distance;
    }

    public void setDistance(String distance) {
        if (distance != null) this.distance = Double.parseDouble(distance);
        else Log.w("Mark Class insertion", "null distance set - Mark named:" + this.name);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistanceFactor(boolean distanceFactor) {
        this.distanceFactor = distanceFactor;
    }

    public double getAbsDistance(double multiplication) {
        if (distanceFactor) return multiplication * getDistance();
        return getDistance();
    }

    public void setDistaneFactor(String distaneFactor) {
        if (distaneFactor != null)
            this.distanceFactor = distaneFactor.equals("true") || distaneFactor.equals("always");
        else Log.w("Mark Class insertion", "null distanceFactor set - Mark named:" + this.name);
    }

    public boolean addReferedMark(Mark referedMark) {
        return referedMarks.add(referedMark);
    }

    public void setIsGatable(boolean isGatable) {
        this.isGatable = isGatable;
    }

    public ArrayList<Mark> getReferedMarks() {
        return referedMarks;
    }

    public void setIsGatable(String isGatable) {
        if (isGatable != null)
            this.isGatable = isGatable.equals("true") || isGatable.equals("always");
        else Log.w("Mark Class insertion", "null isGatable set - Mark named:" + this.name);
    }

    public void setGateDirection(int gateDirection) {
        this.gateDirection = gateDirection;
    }

    public void setGateDirection(String gateDirection) {
        if (gateDirection != null) this.gateDirection = Integer.parseInt(gateDirection);
        else Log.w("Mark Class insertion", "null gateDirection set - Mark named:" + this.name);

    }

    public int getGateDirection() {
        return gateDirection;
    }

    public void setGateDistance(double gateDistance) {
        this.gateDistance = gateDistance;
    }

    public void setGateDistance(String gateDistance) {
        if (gateDistance != null) this.gateDistance = Double.parseDouble(gateDistance);
        else Log.w("Mark Class insertion", "null gateDistance set - Mark named:" + this.name);
    }

    public double getGateDistance() {
        return gateDistance;
    }

    public void setGateType(String gateType) {
        this.gateType = gateType;
    }

    public List<Buoy> parseBuoys(AviLocation referencePoint, double multiplication, int windDir) {  //parses the mark and his sons into buoys
        /**
         * referencePoint - the referencePoint location. NOT SIGNAL BOAT
         * multiplication - known also as dist2m1 (the distance toward the first mark)
         * windDir  -wind direction
         *
         * Each Mark is a tree root to marks that uses it's location, so this function must act recursively
         */
        List<Buoy> buoys = new ArrayList<>();
        AviLocation location = new AviLocation(referencePoint, getDirection() + windDir, getAbsDistance(multiplication));
        if (isGatable || gateType.equals("ReferencePoint")) {
            switch (gateType) {
                case "Buoy":  //adds a single buoy
                    buoys.add(new Buoy(this.getName(), location, BuoyType.Buoy));
                    Log.i("Mark class parsing", "buoy added, gateType Buoy, name:" + this.getName());
                    break;
                case "Gate":
                    buoys.add(new Buoy(this.getName() + " S", new AviLocation(location, windDir - getGateDirection(), getGateDistance() / 2), BuoyType.Gate));
                    buoys.add(new Buoy(this.getName() + " P", new AviLocation(location, windDir + getGateDirection(), getGateDistance() / 2), BuoyType.Gate));
                    Log.i("Mark class parsing", "buoys added, gateType Gate, name:" + this.getName());
                    break;
                case "FinishLine":
                    buoys.add(new Buoy(this.getName() + " S", new AviLocation(location, windDir - getGateDirection(), getGateDistance() / 2), BuoyType.FinishLine));
                    buoys.add(new Buoy(this.getName() + " P", new AviLocation(location, windDir + getGateDirection(), getGateDistance() / 2), BuoyType.FinishLine));
                    Log.i("Mark class parsing", "buoys added, gateType FinishLine, name:" + this.getName());
                    break;
                case "StartLine":
                    buoys.add(new Buoy(this.getName() + " S", new AviLocation(location, windDir - getGateDirection(), getGateDistance() / 2), BuoyType.StartLine));
                    buoys.add(new Buoy(this.getName() + " P", new AviLocation(location, windDir + getGateDirection(), getGateDistance() / 2), BuoyType.StartLine));
                    Log.i("Mark class parsing", "buoys added, gateType Buoy, name:" + this.getName());
                    break;
                case "Satellite":
                    buoys.add(new Buoy(this.getName(), location, BuoyType.Buoy));
                    buoys.add(new Buoy(this.getName() + "a", new AviLocation(location, windDir + getGateDirection(), getGateDistance()), BuoyType.TriangleBuoy));
                    Log.i("Mark class parsing", "buoys added, gateType StartLine, name:" + this.getName());
                    break;
                case "ReferencePoint":
                    // buoys.add(new Buoy(this.getName(), location, BuoyType.ReferencePoint)); //TODO: add reference point icon
                    break;
                default:
                    buoys.add(new Buoy(this.getName(), location));
                    Log.e("Mark class parsing", "gateType not recognized. default buoy added. failed at" + gateType);
            }
        } else {
            buoys.add(new Buoy(this.getName(), location, BuoyType.Buoy));
            Log.i("Mark class parsing", "buoy added (non-gatable), gateType Buoy, name:" + this.getName());
        }

        //parseChildren
        for (int i = 0; i < this.getReferedMarks().size(); i++) {
            Mark child = this.getReferedMarks().get(i);
            buoys.addAll(child.parseBuoys(location, multiplication, windDir));
        }
        return buoys;
    }

}

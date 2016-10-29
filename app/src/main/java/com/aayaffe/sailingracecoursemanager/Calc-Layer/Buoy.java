package radial_design.racecoursedialogui;

import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Jonathan on 27/08/2016.
 */
public class Buoy {
    private String name;
    private AviLocation aviLocation;
    public String color;
    public Date lastUpdate;
    public long id;
    private UUID _uuid;
    private UUID _raceCourseUUID;
    private BuoyType buoyType; //replaces ObjectTypes

    public Buoy(String name, AviLocation loc){
        this.name=name;
        this.aviLocation=loc;
        this.lastUpdate = new Date();
    }
    public Buoy(String name, AviLocation loc, String color){
        this.name=name;
        this.aviLocation=loc;
        this.color=color;
        this.lastUpdate = new Date();
    }
    public Buoy(String name, AviLocation loc, String color, BuoyType buoyType){
        this.name=name;
        this.aviLocation=loc;
        this.color=color;
        this.buoyType=buoyType;
        this.lastUpdate = new Date();
    }

    public String getName() {
        return name;
    }
    public void setAviLocation(AviLocation aviLocation) {
        this.aviLocation = aviLocation;
    }

    public AviLocation getAviLocation() {
        return aviLocation;
    }
    public AviLocation getLoc() {
        return aviLocation;
    }

    public void setBuoyType(BuoyType buoyType) {
        this.buoyType = buoyType;
    }

    public BuoyType getBuoyType() {
        return buoyType;
    }

    public UUID getRaceCourseUUID() {
        return _raceCourseUUID;
    }

    public UUID getUUID() {
        return _uuid;
    }
}

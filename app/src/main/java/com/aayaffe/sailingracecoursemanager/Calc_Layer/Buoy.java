package com.aayaffe.sailingracecoursemanager.Calc_Layer;

import android.location.Location;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.android.gms.maps.model.LatLng;

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
    public UUID getUuid() {
        return _uuid;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLoc(Location loc) {
        this.aviLocation = GeoUtils.toAviLocation(loc);
    }
    public Location getLoc() {
        return GeoUtils.toLocation(aviLocation);
    }
    public LatLng getLatLng() {
        return GeoUtils.toLatLng(aviLocation);
    }



}

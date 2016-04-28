package com.aayaffe.sailingracecoursemanager.communication;

import android.location.Location;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by aayaffe on 30/09/2015.
 */
public class AviObject {
    public String name;
    private AviLocation aviLocation;
    public ObjectTypes type;
    public String color;
    public Date lastUpdate;
    public long id;
    private UUID uuid;
    private UUID raceCourseUUID;

    public AviObject(){
        uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        boolean result = false;
        if (o instanceof AviObject) {
            AviObject that = (AviObject) o;
            result = (that.canEqual(this) && Objects.equals(this.name,that.name));
        }
        return result;
    }
    public boolean canEqual(AviObject other) {
        return (other instanceof AviObject);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public AviLocation getAviLocation(){
        return aviLocation;
    }
    public void setAviLocation(AviLocation al){
        aviLocation = al;
    }

    @JsonIgnore
    public Location getLoc() {
        return GeoUtils.toLocation(aviLocation);
    }
    @JsonIgnore
    public LatLng getLatLng() {
        return GeoUtils.toLatLng(aviLocation);
    }
    @JsonIgnore
    public void setLoc(Location Location) {
        this.aviLocation = GeoUtils.toAviLocation(Location);
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getRaceCourseUUID() {
        return raceCourseUUID;
    }
    public void setRaceCourseUUID(UUID raceCourseUUID) {
        this.raceCourseUUID = raceCourseUUID;
    }
}

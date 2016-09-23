package com.aayaffe.sailingracecoursemanager.communication;

import android.location.Location;

import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by aayaffe on 30/09/2015.
 */

/** Jonathan, notes, 23/6/16;
 * AviObject represents a mark.
 *
 * To get a gate instead of a mark: gateSpan and gateDir must be set, and then it is necessary to use addGate(List, AviObject) to add both gate's marks.
 */
public class AviObject {
    public String name;
    private AviLocation aviLocation;
    private ObjectTypes type;
    public String color;
    public Long lastUpdate;
    public long id;
    private UUID _uuid;
    private UUID _raceCourseUUID;
    private double gateSpan=0;
    private int gateDir=0; //(non-clockwise), from Starboard mark to Port mark

    public AviObject(String name, AviLocation loc, ObjectTypes type, String color, UUID _raceCourseUUID){
        _uuid = UUID.randomUUID();
        this.name=name;
        this.aviLocation=loc;
        this.type=type;
        this.color=color;
        this._raceCourseUUID=_raceCourseUUID;
    }

    public AviObject(String name, AviLocation loc, ObjectTypes type, String color, UUID _raceCourseUUID, double gateSpan, int gateDir){
        _uuid = UUID.randomUUID();
        this.name=name;
        this.aviLocation=loc;
        this.type=type;
        this.color=color;
        this._raceCourseUUID=_raceCourseUUID;
        this.gateDir=gateDir;
        this.gateSpan=gateSpan;
    }

    public AviObject(){
        _uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(java.lang.Object o) {
        boolean result = false;
        if (o instanceof AviObject) {
            AviObject that = (AviObject) o;
            if (that!=null && this.name!=null &&that.name!=null)
                result = (that.canEqual(this) && this.name.equals(that.name));
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

    @Exclude
    public Location getLoc() {
        return GeoUtils.toLocation(aviLocation);
    }
    @Exclude
    public LatLng getLatLng() {
        return GeoUtils.toLatLng(aviLocation);
    }
    @Exclude
    public void setLoc(Location Location) {
        this.aviLocation = GeoUtils.toAviLocation(Location);
    }
    @Exclude
    public UUID getUUID() {
        return _uuid;
    }
    @Exclude
    public UUID getRaceCourseUUID() {
        return _raceCourseUUID;
    }
    @Exclude
    public void setRaceCourseUUID(UUID raceCourseUUID) {
        this._raceCourseUUID = raceCourseUUID;
    }

    public String getRaceCourseUuid() {
        if (_raceCourseUUID==null)
            return null;
        return _raceCourseUUID.toString();
    }
    public void setRaceCourseUuid(String raceCourseUUID) {
        this._raceCourseUUID = UUID.fromString(raceCourseUUID);
    }

    public String getType() {
        return type.toString();
    }

    public void setType(String type) {
        this.type = ObjectTypes.valueOf(type);
    }
    @Exclude
    public ObjectTypes getEnumType(){
        return type;
    }
    @Exclude
    public void setEnumType(ObjectTypes type){
        this.type = type;
    }
    public String getUuid() {
        return _uuid.toString();
    }

    public String getName(){
        return name;
    }
    public void setName(String nname){
        this.name=nname;
    }

    public double getGateSpan(){
        return gateSpan;
    }

    public int getGateDir(){
        return gateDir;
    }

    public void setUuid(String uuid) {
        this._uuid = UUID.fromString(uuid);
    }
    public String getColor(){
        return color;
    }
    @Exclude
    public Date getLastUpdate() {
        return new Date(lastUpdate);
    }
    @Exclude
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate.getTime();
    }
}

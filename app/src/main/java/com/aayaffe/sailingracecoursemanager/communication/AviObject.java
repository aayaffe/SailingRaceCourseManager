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
public class AviObject {
    public String name;
    private AviLocation aviLocation;
    private ObjectTypes type;
    public String color;
    public Date lastUpdate;
    public long id;
    private UUID _uuid;
    private UUID _raceCourseUUID;

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

    public void setUuid(String uuid) {
        this._uuid = UUID.fromString(uuid);
    }
}

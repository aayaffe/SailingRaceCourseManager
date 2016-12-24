package com.aayaffe.sailingracecoursemanager.calclayer;

import android.graphics.Color;
import android.location.Location;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Jonathan on 27/08/2016.
 */
public class DBObject implements Serializable {
    private String name;
    private AviLocation aviLocation;
    public int color = Color.BLACK;
    public Long lastUpdate;
    public long id;
    public String userUid;
    private UUID _uuid;
    private UUID _raceCourseUUID;
    private BuoyType buoyType;
    public HashMap<String,String> assigned;

    public static final int ORANGE = 0xf49842;

    public DBObject() {
        _uuid = UUID.randomUUID();
    }

    public DBObject(String name, AviLocation loc) {
        _uuid = UUID.randomUUID();
        this.name = name;
        this.aviLocation = loc;
        this.lastUpdate = new Date().getTime();
        this.buoyType = BuoyType.OTHER;
    }

    public DBObject(String name, AviLocation loc, BuoyType buoyType, UUID raceCourseUUID) {
        _uuid = UUID.randomUUID();
        this.name = name;
        this.aviLocation = loc;
        this.buoyType = buoyType;
        this.lastUpdate = new Date().getTime();
        this._raceCourseUUID = raceCourseUUID;

        switch (buoyType) {
            case FINISH_LINE:
                this.color = Color.BLUE;
                break;
            case START_LINE:
                this.color = ORANGE;
                break;
            case GATE:
                this.color = Color.YELLOW;
                break;
            case BUOY:
                this.color = Color.RED;
                break;
        }
    }

    public DBObject(String name, AviLocation loc, int color, BuoyType buoyType) {
        _uuid = UUID.randomUUID();
        this.name = name;
        this.aviLocation = loc;
        this.color = color;
        this.buoyType = buoyType;
        this.lastUpdate = new Date().getTime();
    }
    public DBObject(String name, AviLocation loc, int color, BuoyType buoyType, String userUid) {
        _uuid = UUID.randomUUID();
        this.name = name;
        this.aviLocation = loc;
        this.color = color;
        this.buoyType = buoyType;
        this.lastUpdate = new Date().getTime();
        this.userUid = userUid;
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

    @Exclude
    public void setBuoyType(BuoyType buoyType) {
        this.buoyType = buoyType;
    }

    @Exclude
    public BuoyType getBuoyType() {
        return buoyType;
    }

    public String getType() {
        return buoyType.toString();
    }

    public void setType(String type) {
        this.buoyType = BuoyType.valueOf(type);
    }

    @Exclude
    public UUID getRaceCourseUUID() {
        return _raceCourseUUID;
    }
    @Exclude
    public void setRaceCourseUUID(UUID _raceCourseUUID) {
        this._raceCourseUUID = _raceCourseUUID;
    }

    @Exclude
    public UUID getUUID() {
        return _uuid;
    }

    public void setRCUuidString(String uuid) {
        this._raceCourseUUID = UUID.fromString(uuid);
    }
    public String getRCUuidString() {
        if (_raceCourseUUID!=null)
            return this._raceCourseUUID.toString();
        else return null;
    }

    public void setUuidString(String uuid) {
        this._uuid = UUID.fromString(uuid);
    }
    public String getUuidString() {
        return this._uuid.toString();
    }




    @Exclude
    public Date getLastUpdate() {
        return new Date(lastUpdate);
    }

    @Exclude
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate.getTime();
    }

    @Exclude
    public void setLoc(Location loc) {
        this.aviLocation = GeoUtils.toAviLocation(loc);
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
    public int getIconResourceId() {
        if (this.getBuoyType() == BuoyType.FLAG_BUOY || this.getBuoyType() == BuoyType.FINISH_LINE || this.getBuoyType() == BuoyType.START_LINE) {
            switch (this.color) {
                case Color.RED:
                    return R.mipmap.flag_buoy_red;
                case Color.BLUE:
                    return R.mipmap.flag_buoy_blue;
                case Color.YELLOW:
                    return R.mipmap.flag_buoy_yellow;
                case ORANGE:
                default:
                    return R.mipmap.flag_buoy_orange;
            }
        } else if (this.getBuoyType() == BuoyType.TOMATO_BUOY || this.getBuoyType() == BuoyType.BUOY || this.getBuoyType() == BuoyType.GATE) {

            switch (this.color) {
                case Color.RED:
                    return R.mipmap.tomato_buoy_red;
                case Color.BLUE:
                    return R.mipmap.tomato_buoy_blue;
                case Color.YELLOW:
                    return R.mipmap.tomato_buoy_yellow;
                case ORANGE:
                default:
                    return R.mipmap.tomato_buoy_orange;
            }
        } else if (this.getBuoyType() == BuoyType.TRIANGLE_BUOY) {
            switch (this.color) {
                case Color.RED:
                    return R.mipmap.triangle_buoy_red;
                case Color.BLUE:
                    return R.mipmap.triangle_buoy_blue;
                case Color.YELLOW:
                    return R.mipmap.triangle_buoy_yellow;
                case ORANGE:
                    return R.mipmap.triangle_buoy_orange;
                default:
                    return R.mipmap.triangle_buoy;

            }
        } else
            return R.mipmap.tomato_buoy_black_empty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBObject buoy = (DBObject) o;


        return (_uuid != null ? _uuid.equals(buoy._uuid) : buoy._uuid == null);


    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (_uuid != null ? _uuid.hashCode() : 0);
        result = 31 * result + (_raceCourseUUID != null ? _raceCourseUUID.hashCode() : 0);
        result = 31 * result + (buoyType != null ? buoyType.hashCode() : 0);
        return result;
    }
}

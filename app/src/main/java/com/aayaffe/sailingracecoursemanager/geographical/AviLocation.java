package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import java.util.Date;

/**
 * Created by aayaffe on 09/01/2016.
 */
public class AviLocation {
    public double lat;
    public double lon;
    public double sog;
    public double cog;
    public double depth;
    public Date lastUpdate;

    public AviLocation() {

    }
    public AviLocation(double latitude, double longitude) {
        lat = latitude;
        lon = longitude;
    }
    public AviLocation(double latitude, double longitude, double sog, double cog, double depth, Date lastUpdate) {
        lat = latitude;
        lon = longitude;
        this.sog = sog;
        this.cog = cog;
        this.depth = depth;
        this.lastUpdate = lastUpdate;

    }

    public Location toLocation(){
        return GeoUtils.createLocation(lat,lon);
    }
}

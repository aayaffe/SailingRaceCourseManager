package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by aayaffe on 09/01/2016.
 */
public class AviLocation {
    public double lat;
    public double lon;
    public double sog;
    public float cog;
    public double depth;
    public Date lastUpdate;

    public AviLocation() {

    }
    public AviLocation(double latitude, double longitude) {
        lat = latitude;
        lon = longitude;
    }
    public AviLocation(double latitude, double longitude,  float cog, double sog, double depth, Date lastUpdate) {
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

    public static long Age(AviLocation aviLocation) {
        if (aviLocation==null) return -1;
        if (aviLocation.lastUpdate==null) return -1;
        long diffInMs = new Date().getTime() - aviLocation.lastUpdate.getTime();
        return TimeUnit.MILLISECONDS.toSeconds(diffInMs);
    }

    /**
     * Returns the approximate distance in meters between this
     * location and the given location.  Distance is defined using
     * the WGS84 ellipsoid.
     *
     * @param dest the destination location
     * @return the approximate distance in meters, -1 if error
     */
    public float distanceTo(AviLocation dest) {
        if (dest!=null) {
            return GeoUtils.toLocation(this).distanceTo(GeoUtils.toLocation(dest));
        }
        return -1;
    }


    /**
     * Returns the approximate initial bearing in degrees East of true
     * North when traveling along the shortest path between this
     * location and the given location.  The shortest path is defined
     * using the WGS84 ellipsoid.  Locations that are (nearly)
     * antipodal may produce meaningless results.
     *
     * @param dest the destination location
     * @return the initial bearing in degrees
     */
    public float bearingTo(AviLocation dest) {
        if (dest!=null) {
            return GeoUtils.toLocation(this).bearingTo(GeoUtils.toLocation(dest));
        }
        return -1;
    }
}

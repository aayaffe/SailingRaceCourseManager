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
    public double sog = 0;
    public float cog = 0;
    public double depth = 0;
    public Date lastUpdate;

    public AviLocation() {

    }
    public AviLocation(double latitude, double longitude) {
        lat = latitude;
        lon = longitude;
        lastUpdate = new Date();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AviLocation that = (AviLocation) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        if (Double.compare(that.lon, lon) != 0) return false;
        if (Double.compare(that.sog, sog) != 0) return false;
        return Float.compare(that.cog, cog) == 0 && Double.compare(that.depth, depth) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(sog);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (cog != +0.0f ? Float.floatToIntBits(cog) : 0);
        temp = Double.doubleToLongBits(depth);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

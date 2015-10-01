package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import org.mapsforge.core.model.LatLong;

/**
 * Created by aayaffe on 30/09/2015.
 */
public class GeoUtils {
    static public Location toLocation(LatLong l){
        if (l==null)
            return null;
        Location ret = new Location("Converted");
        ret.setLatitude(l.latitude);
        ret.setLongitude(l.longitude);
        return ret;
    }
    static public LatLong toLatLong(Location l){
        if (l==null)
            return null;
        LatLong ret = new LatLong(l.getLatitude(),l.getLongitude());
        return ret;
    }
    static public Location createLocation(double lat, double lon){
        Location ret = new Location("Converted");
        ret.setLatitude(lat);
        ret.setLongitude(lon);
        return ret;
    }
}

package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by aayaffe on 30/09/2015.
 */
public class GeoUtils {
//    static public Location toLocation(LatLong l){
//        if (l==null)
//            return null;
//        Location ret = new Location("Converted");
//        ret.setLatitude(l.latitude);
//        ret.setLongitude(l.longitude);
//        return ret;
//    }
    static public Location toLocation(AviLocation l){
        if (l==null)
            return null;
        Location ret = new Location("Converted");
        ret.setLatitude(l.lat);
        ret.setLongitude(l.lon);
        ret.setBearing(l.cog);
        ret.setAltitude(l.depth);
        return ret;
    }

//    static public LatLong toLatLong(AviLocation l){
//        if (l==null)
//            return null;
//        LatLong ret = new LatLong(l.lat,l.lon);
//        return ret;
//    }
    static public LatLng toLatLng(Location l){
        if (l==null)
            return null;
        LatLng ret = new LatLng(l.getLatitude(), l.getLongitude());
        return ret;
    }
    static public LatLng toLatLng(AviLocation l){
        if (l==null)
            return null;
        LatLng ret = new LatLng(l.lat, l.lon);
        return ret;
    }


    static public Location createLocation(double lat, double lon){
        Location ret = new Location("Converted");
        ret.setLatitude(lat);
        ret.setLongitude(lon);
        return ret;
    }
    static public AviLocation toAviLocation(Location l){
        if (l==null)
            return null;
        AviLocation ret = new AviLocation(l.getLatitude(),l.getLongitude(),l.getBearing(),l.getSpeed(),l.getAltitude(),new Date(l.getTime()));
        return ret;
    }

    public static AviLocation getLocationFromDirDist(AviLocation loc, int dir, int dist) {
        double dis = (dist/1000.0)/6371;
        double brng = Math.toRadians(dir);
        double lat1 = Math.toRadians(loc.lat);
        double lon1 = Math.toRadians(loc.lon);

        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dis) + Math.cos(lat1) * Math.sin(dis) * Math.cos(brng));
        double a = Math.atan2(Math.sin(brng)*Math.sin(dis)*Math.cos(lat1), Math.cos(dis)-Math.sin(lat1)*Math.sin(lat2));
        double lon2 = lon1 + a;

        lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
        AviLocation ret = new AviLocation(Math.toDegrees(lat2),Math.toDegrees(lon2));
        return ret;



    }

    public static Location getLocationFromDirDist(Location loc, float dir, int dist) {
        return toLocation(getLocationFromDirDist(toAviLocation(loc),(int)dir,dist));
    }

    public static int relativeToTrueDirection(int trueDir, int relativDir){
        return ((trueDir + relativDir) % 360);
    }

    public static double toHours(int minutes){
        return ((double)minutes)/((double)60);
    }
}

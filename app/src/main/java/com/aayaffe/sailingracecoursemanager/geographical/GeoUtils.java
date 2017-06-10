package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 30/09/2015.
 */
public class GeoUtils {

    public static final double NM2m = 1852.5;
    static public Location toLocation(AviLocation l){
        if (l==null)
            return null;
        Location ret = new Location("Converted");
        ret.setLatitude(l.lat);
        ret.setLongitude(l.lon);
        ret.setBearing(l.cog);
        return ret;
    }

    static public LatLng toLatLng(Location l){
        if (l==null)
            return null;
        return new LatLng(l.getLatitude(), l.getLongitude());
    }
    static public LatLng toLatLng(AviLocation l){
        if (l==null)
            return null;
        return new LatLng(l.lat, l.lon);
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
        return new AviLocation(l.getLatitude(),l.getLongitude(),l.getBearing(),l.getSpeed(),new Date(l.getTime()));
    }

    public static AviLocation getLocationFromDirDist(AviLocation loc, double dir, int distm) {
        double dis = (distm/1000.0)/6371.009;
        double brng = Math.toRadians(dir);
        double lat1 = Math.toRadians(loc.lat);
        double lon1 = Math.toRadians(loc.lon);
        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dis) + Math.cos(lat1) * Math.sin(dis) * Math.cos(brng));
        double a = Math.atan2(Math.sin(brng)*Math.sin(dis)*Math.cos(lat1), Math.cos(dis)-Math.sin(lat1)*Math.sin(lat2));
        double lon2 = lon1 + a;
        lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
        return new AviLocation(Math.toDegrees(lat2),Math.toDegrees(lon2));
    }

//    public static Location getLocationFromDirDistm(Location loc, double dir, int distm) {
//        double dis = (distm)/6371.009;
//        double brng = Math.toRadians(dir);
//        double lat1 = Math.toRadians(loc.getLatitude());
//        double lon1 = Math.toRadians(loc.getLongitude());
//        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(dis) + Math.cos(lat1) * Math.sin(dis) * Math.cos(brng));
//        double a = Math.atan2(Math.sin(brng)*Math.sin(dis)*Math.cos(lat1), Math.cos(dis)-Math.sin(lat1)*Math.sin(lat2));
//        double lon2 = lon1 + a;
//        lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
//        Location ret = new Location(loc);
//        ret.setLatitude(Math.toDegrees(lat2));
//        ret.setLongitude(Math.toDegrees(lon2));
//        return ret;
//    }

    public static AviLocation getLocationFromDirDist(AviLocation loc, double dir, double distNM) {
        return getLocationFromDirDist(loc,dir,(int)(distNM*GeoUtils.NM2m));
    }

    public static AviLocation getLocationFromTriangulation(AviLocation p1, double brng1, AviLocation p2,  double brng2){
        AviLocation ret = new AviLocation();
        double lat1 = Math.toRadians(p1.getLat()), lon1 = Math.toRadians(p1.getLon());
        double lat2 = Math.toRadians(p2.getLat()), lon2 = Math.toRadians(p2.getLon());
        double brng13 = Math.toRadians(brng1), brng23 = Math.toRadians(brng2);
        double dLat = lat2 - lat1, dLon = lon2 - lon1;
        double dist12 = 2 * Math.asin(Math.sqrt(Math.sin(dLat / 2)
                * Math.sin(dLat / 2) + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2)));
        Double brngA = Math.acos((Math.sin(lat2) - Math.sin(lat1) * Math.cos(dist12)) / (Math.sin(dist12) * Math.cos(lat1)));
        if (brngA.isNaN())
            brngA = 0.0;
        Double brngB = Math.acos((Math.sin(lat1) - Math.sin(lat2) * Math.cos(dist12)) / (Math.sin(dist12) * Math.cos(lat2)));
        double brng12, brng21;
        if (Math.sin(lon2 - lon1) > 0) {
            brng12 = brngA;
            brng21 = 2 * Math.PI - brngB;
        } else {
            brng12 = 2 * Math.PI - brngA;
            brng21 = brngB;
        }
        double alpha1 = (brng13 - brng12 + Math.PI) % (2 * Math.PI) - Math.PI; // angle
        double alpha2 = (brng21 - brng23 + Math.PI) % (2 * Math.PI) - Math.PI; // angle

        double alpha3 = Math.acos(-Math.cos(alpha1) * Math.cos(alpha2)
                + Math.sin(alpha1) * Math.sin(alpha2) * Math.cos(dist12));
        double dist13 = Math.atan2(
                Math.sin(dist12) * Math.sin(alpha1) * Math.sin(alpha2),
                Math.cos(alpha2) + Math.cos(alpha1) * Math.cos(alpha3));
        double lat3 = Math.asin(Math.sin(lat1) * Math.cos(dist13)
                + Math.cos(lat1) * Math.sin(dist13) * Math.cos(brng13));
        double dLon13 = Math.atan2(
                Math.sin(brng13) * Math.sin(dist13) * Math.cos(lat1),
                Math.cos(dist13) - Math.sin(lat1) * Math.sin(lat3));
        double lon3 = lon1 + dLon13;
        lon3 = (lon3 + Math.PI) % (2 * Math.PI) - Math.PI;
        ret.lat = Math.toDegrees(lat3);
        ret.lon = Math.toDegrees(lon3);
        return ret;
    }

    public static AviLocation getMidPointLocation(AviLocation p1, AviLocation p2){ //Middle point
        AviLocation ret = new AviLocation();
        double lon2 =p2.getLon();
        double lon1 = p1.getLon();
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1 = Math.toRadians(p1.getLat());
        double lat2 = Math.toRadians(p2.getLat());
        lon1 = Math.toRadians(p1.getLon());
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        ret.lat=Math.toDegrees(lat3);
        ret.lon=Math.toDegrees(lon3);
        return ret;
    }

    public static int relativeToTrueDirection(int trueDir, int relativDir){
        return ((trueDir + relativDir) % 360);
    }

    public static double toHours(int minutes){
        return ((double)minutes)/((double)60);
    }

    public static double toNauticalMiles(double meters){
        return meters/NM2m;
    }

    public static long ageInSeconds(Date d){
        if (d==null) return -1;
        long diffInMs = new Date().getTime() - d.getTime();
        return TimeUnit.MILLISECONDS.toSeconds(diffInMs);
    }
}
enum LengthUnit {
    /**
     * Miles, using the scale factor 0.6213712 miles per kilometer.
     */
    MILE(0.6213712),
    /**
     * Nautical miles, using the scale factor 0.5399568 nautical miles per kilometer.
     */
    NAUTICAL_MILE(0.5399568),
    /**
     * Rods, using the scale factor 198.8387815 rods to the kilometer.
     * Because your car gets forty rods to the hogshead and that's
     * they way you likes it.
     */
    ROD(198.8387815),
    /**
     * Kilometers, the primary unit.
     */
    KILOMETER(1.0),
    /**
     * Meters, for ease of use.
     */
    METER(1000.0);

    /**
     * The primary length unit. All scale factors are relative
     * to this unit. Any conversion not involving the primary
     * unit will first be converted to this unit, then to
     * the desired unit.
     */
    public static final LengthUnit PRIMARY = KILOMETER;

    private double scaleFactor;

    LengthUnit(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Convert a value of this unit type to the units specified
     * in the parameters.
     *
     * @param toUnit the unit to convert to.
     * @param value the value to convert.
     * @return the converted value.
     */
    public double convertTo(LengthUnit toUnit, double value) {
        if (this == toUnit) {
            return value;
        }

        double _value = value;
        if (this != PRIMARY) {
            _value /= this.scaleFactor; // Convert to primary unit.
        }
        if (toUnit != PRIMARY) {
            _value *= toUnit.scaleFactor; // Convert to destination unit.
        }
        return _value;
    }

    /**
     * Retrieve the scale factor between this unit and the primary
     * length unit.
     *
     * @return the scale factor.
     */
    public double getScaleFactor() {
        return scaleFactor;
    }
}
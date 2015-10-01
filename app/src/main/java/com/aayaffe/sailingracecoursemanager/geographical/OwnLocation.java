package com.aayaffe.sailingracecoursemanager.geographical;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by aayaffe on 28/09/2015.
 */
public class OwnLocation implements IGeo {

    private LocationManager locationManager;


    public OwnLocation (Activity a){
        InitGPS(a);
    }

    @Override
    public Location getLoc() {
        return getLastBestLocation();
    }
    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
    private void InitGPS(Activity a){
        locationManager = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
    }
}

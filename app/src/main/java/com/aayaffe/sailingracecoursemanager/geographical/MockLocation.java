package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

import java.util.Random;

/**
 * Created by aayaffe on 30/09/2015.
 */
public class MockLocation implements IGeo {

    private double minLat = 32.5;
    private double maxLat = 32.7;

    private double minLon = 32.5;
    private double maxLon = 33;
    @Override
    public Location getLoc() {
        Random r = new Random();
        double lat = minLat + (maxLat - minLat) * r.nextDouble();
        double lon = minLon + (maxLon - minLon) * r.nextDouble();
        Location ret =  new Location("Mock");
        ret.setLatitude(lat);
        ret.setLongitude(lon);
        return ret;

    }
}

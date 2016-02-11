package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

/**
 * Created by aayaffe on 22/09/2015.
 */
public interface IGeo {

    public Location getLoc();

    void stopLocationUpdates();

    void startLocationUpdates();
}

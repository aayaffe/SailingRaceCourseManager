package com.aayaffe.sailingracecoursemanager.geographical;

import android.location.Location;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 22/09/2015.
 */
public interface IGeo {

    Location getLoc();

    void stopLocationUpdates();

    void startLocationUpdates();
}

package com.aayaffe.sailingracecoursemanager.Map_Layer;

import com.google.android.gms.maps.model.Marker;

import java.util.UUID;

/**
 * Created by aayaffe on 11/11/2016.
 */

public interface MapClickMethods {
    void infoWindowClick(UUID m);
    void infoWindowLongClick(UUID m);

}

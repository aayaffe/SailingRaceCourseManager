package com.aayaffe.sailingracecoursemanager.map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;


import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;

/**
 * Created by aayaffe on 29/01/2016.
 */
public interface MapLayer {
    void Init(Activity a, Context c, MapView mv, SharedPreferences sp, Location center, int zoom);

    void setCenter(Location l);

    void setCenter(LatLong ll);

    void setCenter(double lat, double lon);

    void setZoomLevel(int zoom);

    void destroy();

    Marker addMark(Marker m);

    boolean contains(Marker m);

    Marker removeMark(Marker m);

    void loadMap();

    LatLong getLastTapLatLong();
}

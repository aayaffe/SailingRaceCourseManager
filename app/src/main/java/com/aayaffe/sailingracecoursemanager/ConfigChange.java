package com.aayaffe.sailingracecoursemanager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.map.GoogleMaps;
import com.aayaffe.sailingracecoursemanager.map.GoogleMapsActivity;

import java.io.IOException;
import java.util.HashSet;

/**
 * Created by aayaffe on 29/09/2015.
 */

public class ConfigChange implements SharedPreferences.OnSharedPreferenceChangeListener
{


    private static final String TAG = "ConfigChange";

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.v("ConfigChange", "Configuration key " + key + " Changed.");
        if (key.equals("username")) {
            String id = sharedPreferences.getString("username", "Manager1");
            GoogleMapsActivity.login(id);
        }
        if (key.equals("refreshRate")){
            GoogleMapsActivity.REFRESH_RATE = Integer.parseInt(sharedPreferences.getString("refreshRate","10"))*1000;
            Log.d(TAG, "New refresh rate is: " + GoogleMapsActivity.REFRESH_RATE);
        }


    }
}

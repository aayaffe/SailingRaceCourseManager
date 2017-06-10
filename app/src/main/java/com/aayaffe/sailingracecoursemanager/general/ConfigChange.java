package com.aayaffe.sailingracecoursemanager.general;

import android.content.SharedPreferences;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.activities.GoogleMapsActivity;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 29/09/2015.
 */

public class ConfigChange implements SharedPreferences.OnSharedPreferenceChangeListener
{


    private static final String TAG = "ConfigChange";

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.v("ConfigChange", "Configuration key " + key + " Changed.");
        if (key.equals("refreshRate")){
            GoogleMapsActivity.refreshRate = Integer.parseInt(sharedPreferences.getString("refreshRate","10"))*1000;
            Log.d(TAG, "New refresh rate is: " + GoogleMapsActivity.refreshRate);
        }


    }
}

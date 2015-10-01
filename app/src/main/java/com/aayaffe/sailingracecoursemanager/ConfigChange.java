package com.aayaffe.sailingracecoursemanager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

/**
 * Created by aayaffe on 29/09/2015.
 */

public class ConfigChange implements SharedPreferences.OnSharedPreferenceChangeListener
{


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        Log.v("ConfigChange", "Configuration key " + key + " Changed.");
        if (key.equals("username")) {
            String id = sharedPreferences.getString("username", "Manager1");
            MainActivity.login(id);
            MainActivity.resetMap();
        }
        if (key.equals("refeshRate")){
            MainActivity.REFRESH_RATE = Integer.parseInt(sharedPreferences.getString("refreshRate","10"))*1000;
        }

//        if (sharedPreferences.getBoolean("manager",false)){
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            HashSet<String> hs = new HashSet<>();
//            hs.add("Manager1");
//            hs.add("Manager2");
//            editor.putStringSet("UserNames",hs);
//            editor.commit();
//        }
    }
}

package com.aayaffe.sailingracecoursemanager;

/**
 * Created by aayaffe on 29/09/2015.
 */

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AppPreferences extends PreferenceActivity {
    public static final String RENDERTHEME_MENU = "renderthememenu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO validate preferences.
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
package com.aayaffe.sailingracecoursemanager.General;

/**
 * Created by aayaffe on 29/09/2015.
 */

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.aayaffe.sailingracecoursemanager.R;

public class AppPreferences extends PreferenceActivity {
    public static final String RENDERTHEME_MENU = "renderthememenu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO validate preferences.
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        boolean manager = true;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            manager = extras.getBoolean("MANAGER");
        }
        if(!manager) {

            findPreference("category_race_course").setEnabled(false);
            findPreference("category_conditions").setEnabled(false);

        }
    }

}
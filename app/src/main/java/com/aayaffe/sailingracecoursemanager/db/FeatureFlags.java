package com.aayaffe.sailingracecoursemanager.db;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.BuildConfig;
import com.aayaffe.sailingracecoursemanager.R;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.List;

public class FeatureFlags {

    private static final String TAG = "FeatureFlags";
    private final FirebaseRemoteConfig mFirebaseRemoteConfig;
    public final List<FeatureFlagFetchListener> listeners = new ArrayList<>();

    public FeatureFlags(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        int fetchInterval = BuildConfig.BUILD_TYPE.equals("debug")?0:3600;
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(fetchInterval)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.feature_flags_defaults);
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Log.d(TAG, "Config params updated: " + updated);
                        if (updated) notifyListners();
                    } else {
                        Log.d(TAG, "Config fetch and update failed");
                    }
                });
    }

    public void setOnFetchEventListener(FeatureFlagFetchListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListners(){
        for (FeatureFlagFetchListener l: listeners){
            if (l!=null){
                l.onFetch();
            }
        }
    }

    public boolean getFlag(String flag){
        return mFirebaseRemoteConfig.getBoolean(flag);
    }

}

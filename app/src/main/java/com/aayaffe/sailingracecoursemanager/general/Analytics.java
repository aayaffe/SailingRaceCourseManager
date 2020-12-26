package com.aayaffe.sailingracecoursemanager.general;

import android.content.Context;
import android.os.Bundle;

import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Date;

/**
 * Used as the static analytics tool for the application
 * Created by aayaffe on 04/03/2017.
 */

public class Analytics {
    private static final String TAG = "Analytics";
    private static FirebaseAnalytics firebaseAnalytics;
    public enum EventName {
        ADD_EVENT,DELETE_EVENT,ENTER_EVENT,LEAVE_EVENT,ADD_RACECOURSE,ADD_BUOY
    }
    public Analytics(Context c, String userId, Boolean admin) {
        if (firebaseAnalytics == null) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(c);
        }
        firebaseAnalytics.setUserId(userId);
        firebaseAnalytics.setUserProperty("role", admin?"admin":null);

    }
    public void LogAddEvent(String name, Date start, Date end, User u){
        try{
            Bundle params = new Bundle();
            params.putString("EVENT_NAME", name);
            params.putString("DATE_START", start.toString());
            params.putString("DATE_END", end.toString());
            params.putString("USER_NAME", u.DisplayName);
            params.putString("UID", u.Uid);
            firebaseAnalytics.logEvent(EventName.ADD_EVENT.name(), params);
        } catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
    public void LogDeleteEvent(Event e, User u){
        try{
            Bundle params = new Bundle();
            params.putString("EVENT_NAME", e.getName());
            params.putString("EVENT_UUID", e.getUuid());
            params.putString("USER_NAME", u.DisplayName);
            params.putString("UID", u.Uid);
            firebaseAnalytics.logEvent(EventName.DELETE_EVENT.name(), params);
        } catch (Exception ex){
            FirebaseCrashlytics.getInstance().recordException(ex);
        }
    }
}

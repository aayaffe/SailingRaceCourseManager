package com.aayaffe.sailingracecoursemanager.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * This file is part of an
 * Avi Marine Innovations project: SailingRaceCourseManager
 * first created by aayaffe on 30/12/2017.
 */


public class FirebaseBackgroundService extends Service {
    private static final String TAG = "FirebaseBackgroundServi";
    private Users users;
    private boolean connected = false;
    private ValueEventListener vel;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Users.Init(FirebaseDB.getInstance(this), PreferenceManager.getDefaultSharedPreferences(this));
        users = Users.getInstance();
        FirebaseDB.getInstance(FirebaseBackgroundService.this).fb = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(this.getString(R.string.firebase_database_url));
        vel = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDB.ds = dataSnapshot;
                if (users.getCurrentUser() == null) {
                    Users.setCurrentUser(FirebaseDB.getInstance(FirebaseBackgroundService.this).findUser(FirebaseDB.getInstance(FirebaseBackgroundService.this).getLoggedInUid()));
                }
                if ((FirebaseDB.getInstance(FirebaseBackgroundService.this).listeners != null) && (!connected)) {
                    for (CommManagerEventListener listener : FirebaseDB.getInstance(FirebaseBackgroundService.this).listeners) {
                        if (listener != null)
                            listener.onConnect(new Date());
                    }
                } else {
                    Log.d(TAG, "no listeners registered");
                }
                connected = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Is this really necessary?
            }

        };
        FirebaseDB.getInstance(FirebaseBackgroundService.this).fb.addValueEventListener(vel);


        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            Log.d(TAG, "in onAuthStateChange");
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (FirebaseDB.ds != null)
                FirebaseDB.getInstance(FirebaseBackgroundService.this).setUser(user);
            else {
                if (user != null)
                    FirebaseDB.getInstance(FirebaseBackgroundService.this).setUser(user);
                else
                    FirebaseDB.getInstance(FirebaseBackgroundService.this).setUser(null);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        FirebaseDB.getInstance(FirebaseBackgroundService.this).fb.removeEventListener(vel);
        super.onDestroy();
    }

}
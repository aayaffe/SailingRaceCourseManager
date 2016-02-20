package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aayaffe on 21/11/2015.
 */
public class Firebase implements ICommManager {
    private static final String TAG = "Firebase";
    private Context c;
    private static com.firebase.client.Firebase fb;
    private DataSnapshot ds;

    public Firebase(Context c) {
        this.c = c;
    }

    private String EventName = "Event1";//TODO Handle different events
    private String Uid;

    @Override
    public int login(String user, String password, String nickname) {
        com.firebase.client.Firebase.setAndroidContext(c);//TODO Better login indviduals
        if (fb == null) {
            fb = new com.firebase.client.Firebase("https://avi.firebaseio.com"); //TODO: Save string in a concentrated place
        }
            fb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ds = dataSnapshot;
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
            fb.addAuthStateListener(new com.firebase.client.Firebase.AuthStateListener() {
                @Override
                public void onAuthStateChanged(AuthData authData) {
                    if (authData != null) {
                        Uid = authData.getUid();

                        Log.d(TAG, "Uid " + Uid + " Is logged in.");

                    } else {
                        // user is not logged in
                    }
                }
            });

        return 0;
    }

    @Override
    public int writeBoatObject(AviObject o) {
        if (o == null || o.name == null || o.name.isEmpty()) return -1;
        fb.child("Boats").child(o.name).setValue(o); //TODO: Save string in a concentrated place
        return 0;
    }

    @Override
    public int writeBuoyObject(AviObject o) {
        if (o == null || o.name == null || o.name.isEmpty()) return -1;
        fb.child("Buoys").child(o.name).setValue(o); //TODO: Save string in a concentrated place
        fb.child("Events").child(EventName).child("lastBuoyId").setValue(o.id);
        return 0;
    }

    @Override
    public List<AviObject> getAllBoats() {
        ArrayList<AviObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null) return ret;
        for (DataSnapshot ps : ds.child("Boats").getChildren()) {
            AviObject o = ps.getValue(AviObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public List<AviObject> getAllBuoys() {
        ArrayList<AviObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null) return ret;
        for (DataSnapshot ps : ds.child("Buoys").getChildren()) {
            AviObject o = ps.getValue(AviObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public int sendAction(RaceManagerAction a, AviObject o) {
        //TODO Implement
        return 0;
    }

    @Override
    public long getNewBuoyId() {
        if (ds == null || ds.getValue() == null) return 1;
        Long id = (Long) ds.child("Events").child(EventName).child("lastBuoyId").getValue();
        if (id != null) {
            return id + 1;
        } else return 1;
    }

    @Override
    public void removeBueyObject(String title) {
        fb.child("Buoys").child(title).removeValue();
    }

    @Override
    public User findUser(String uid) {
        try {
            User u = ds.child("Users").child(uid).getValue(User.class);
            return u;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addUser(User u) {
        fb.child("Users").child(u.Uid).setValue(u);
    }

    public com.firebase.client.Firebase getFireBaseRef() {
        return fb;
    }

    public void writeEvent(Event neu) {
        fb.child("Events").child(neu.getName()).setValue(neu);
    }

}

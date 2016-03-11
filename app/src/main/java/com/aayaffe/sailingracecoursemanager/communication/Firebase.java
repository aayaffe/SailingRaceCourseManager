package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
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
    private String currentEventName = "Event1";//TODO Handle different events
    private String Uid;
    private Users users;
    public Firebase(Context c) {
        this.c = c;
        users = new Users(this);
    }

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
                    if(users.getCurrentUser()==null){
                        users.setCurrentUser(findUser(Uid));
                    }
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
                        Log.d(TAG, "Uid " + getLoggedInUid() + " Is logged in.");
                        users.setCurrentUser(findUser(Uid));

                    } else {
                        Uid = null;
                        users.logout();
                        Log.d(TAG,"User has logged out.");
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
        fb.child("Events").child(getCurrentEventName()).child("lastBuoyId").setValue(o.id);
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
        Long id = (Long) ds.child("Events").child(getCurrentEventName()).child("lastBuoyId").getValue();
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
            User u;
            u = ds.child("Users").child(uid).getValue(User.class);
            return u;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addUser(User u) {
        fb.child("Users").child(u.Uid).setValue(u);
    }

    @Override
    public void logout() {
        fb.unauth();
        Uid = null;

    }

    @Override
    public Event getEvent(String eventName) {
        try {
            Event e = ds.child("Events").child(eventName).getValue(Event.class);
            return e;
        } catch (Exception e) {
            return null;
        }
    }

    public com.firebase.client.Firebase getFireBaseRef() {
        return fb;
    }

    public void writeEvent(Event neu) {
        fb.child("Events").child(neu.getName()).setValue(neu);
    }


    public String getCurrentEventName() {
        return currentEventName;
    }

    public void setCurrentEventName(String currentEventName) {
        this.currentEventName = currentEventName;
    }

    /***
     *
     * @return the Uid of the currently logged in user.
     * Null if not logged in
     */
    public String getLoggedInUid() {
        return Uid;
    }
}

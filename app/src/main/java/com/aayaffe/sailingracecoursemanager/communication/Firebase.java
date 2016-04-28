package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.Boats.BoatTypes;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by aayaffe on 21/11/2015.
 */
public class Firebase implements ICommManager {
    private static final String TAG = "Firebase";
    private static Context c;
    private static com.firebase.client.Firebase fb;
    private static com.firebase.client.Firebase currentEventFB;
    private static DataSnapshot ds;
    private static String currentEventName;
    private String Uid;
    private Users users;
    private int CompatibleVersion;
    private CommManagerEventListener listener;
    private boolean connected = false;
    private DataSnapshot eventDs;

    public Firebase(Context c) {
        if (Firebase.c ==null)
            Firebase.c = c;
        users = new Users(this);
    }

    public int loginToEvent(String eventName){
        currentEventFB = new com.firebase.client.Firebase(c.getString(R.string.db_base_address)+"/"+c.getString(R.string.db_events)+"/"+eventName); //TODO check for errors
        currentEventFB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventDs = dataSnapshot;
                if(users.getCurrentUser()==null){
                    users.setCurrentUser(findUser(Uid));
                }
                if ((listener != null)&&(!connected))
                    listener.onConnect(new Date());
                connected = true;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return 0;
    } //TODO to be used for finer grained events firing
    @Override
    public int login(String user, String password, String nickname) {
        if (fb == null) {
            com.firebase.client.Firebase.setAndroidContext(c);
            fb = new com.firebase.client.Firebase(c.getString(R.string.db_base_address));
        }
        fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ds = dataSnapshot;
                if(users.getCurrentUser()==null){
                    users.setCurrentUser(findUser(Uid));
                }
                if ((listener != null)&&(!connected))
                    listener.onConnect(new Date());
                connected = true;
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
                    if (findUser(Uid) == null){
                        String displayName;
                        try {
                            displayName = authData.getProviderData().get("displayName").toString();
                        }catch(Exception e){
                            Random r = new Random();
                            displayName = "User" + r.nextInt(10000);
                        }
                        users.setCurrentUser(Uid, displayName);
                    }
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
    public void setCommManagerEventListener(CommManagerEventListener listener){
        this.listener = listener;
    }

    @Override
    public int writeBoatObject(AviObject o) {
        if (o == null || o.name == null || o.name.isEmpty() || currentEventName == null) return -1;
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(o.name).setValue(o);
        return 0;
    }

    @Override
    public int writeBuoyObject(AviObject o) {
        if (o == null || o.name == null || o.name.isEmpty()|| currentEventName == null) return -1;
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(o.name).setValue(o);
        fb.child(c.getString(R.string.db_events)).child(getCurrentEventName()).child(c.getString(R.string.db_lastbuoyid)).setValue(o.id);
        return 0;
    }

    @Override
    public List<AviObject> getAllBoats() {
        ArrayList<AviObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).getChildren()) {
            AviObject o = ps.getValue(AviObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public List<AviObject> getAllBuoys() {
        ArrayList<AviObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).getChildren()) {
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
        if (ds == null || ds.getValue() == null|| currentEventName == null) return 1;
        Long id = (Long) ds.child(c.getString(R.string.db_events)).child(getCurrentEventName()).child(c.getString(R.string.db_lastbuoyid)).getValue();
        if (id != null) {
            return id + 1;
        } else return 1;
    }

    @Override
    public void removeBueyObject(String title) {
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(title).removeValue();
    }

    @Override
    public User findUser(String uid) {
        try {
            User u;
            u = ds.child(c.getString(R.string.db_users)).child(uid).getValue(User.class);
            return u;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void addUser(User u) {
        fb.child(c.getString(R.string.db_users)).child(u.Uid).setValue(u);
    }

    @Override
    public void logout() {
        fb.unauth();
        Uid = null;

    }

    @Override
    public Event getEvent(String eventName) {
        try {
            Event e = ds.child(c.getString(R.string.db_events)).child(eventName).getValue(Event.class);
            return e;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getSupportedVersion() {
       if (ds == null || ds.getValue() == null) return -1;
       long id = (long)ds.child(c.getString(R.string.db_compatible_version)).getValue();
       return id;
    }

    public com.firebase.client.Firebase getFireBaseRef() {
        return fb;
    }

    public void writeEvent(Event neu) {
        fb.child(c.getString(R.string.db_events)).child(neu.getName()).setValue(neu);
    }


    public String getCurrentEventName() {
        return currentEventName;
    }

    public void setCurrentEventName(String currentEventName) {
        //loginToEvent(currentEventName); //TODO: To enable better and finer grained events
        Firebase.currentEventName = currentEventName;
    }


    public HashMap<String, BoatTypes> getAllBoatTypes() {
        HashMap<String, BoatTypes> ret = new HashMap<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_boattypes)).getChildren()) {
            BoatTypes o = ps.getValue(BoatTypes.class);
            ret.put(o.getBoatClass(),o);
        }
        return ret;
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

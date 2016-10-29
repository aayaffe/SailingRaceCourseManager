package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.Boats.BoatTypes;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private static DatabaseReference fb;
    private static DatabaseReference currentEventFB;
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
        currentEventFB = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(c.getString(R.string.firebase_base_url)+"/"+c.getString(R.string.db_events)+"/"+eventName);
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
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        return 0;
    } //TODO to be used for finer grained events firing
    @Override
    public int login(String user, String password, String nickname) {
        if (fb == null) {
            fb = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl(c.getString(R.string.firebase_base_url));
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
            public void onCancelled(DatabaseError databaseError) {

            }

        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Uid = user.getUid();
                    Log.d(TAG, "Uid " + getLoggedInUid() + " Is logged in.");
                    if (findUser(Uid) == null){
                        String displayName;
                        try {
                            displayName = user.getDisplayName();
                            if (displayName==null)
                            {
                                displayName = convertToAcceptableDisplayName(user.getEmail());
                            }
                        }catch(Exception e){
                            Random r = new Random();
                            displayName = "User" + r.nextInt(10000);
                        }
                        users.setCurrentUser(Uid, displayName);
                    }
                    users.setCurrentUser(findUser(Uid));

                } else {
                    Uid = null;
                    //users.logout();
                    Log.d(TAG,"User has logged out.");
                }
            }
        });

        return 0;
    }


    /***
     * Firebase does not accept emails in the database path (i.e. '.' are not allowed)
     * There for we will convert to a better display name
     * @param email
     * @return email's user name only without '.', '#', '$', '[', or ']'
     */
    private String convertToAcceptableDisplayName(String email) {
        email = email.replace('.',' ');
        email = email.replace('#',' ');
        email = email.replace('$',' ');
        email = email.replace('[',' ');
        email = email.replace(']',' ');
        int index = email.indexOf('@');
        return email.substring(0,index);
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
    public int writeRaceCourseDescriptor(RaceCourseDescriptorGeneral rcd) {
        if (rcd == null) return -1;
        fb.child(c.getString(R.string.db_courseDescriptors)).child(rcd.getType()).setValue(rcd);
        return 0;
    }

    @Override
    public List<RaceCourseDescriptorGeneral> getRaceCourseDescriptors() {
        ArrayList<RaceCourseDescriptorGeneral> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_courseDescriptors)).getChildren()) {
            RaceCourseDescriptorGeneral rcd = ps.getValue(RaceCourseDescriptorGeneral.class);
            ret.add(rcd);
        }
        return ret;
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
        FirebaseAuth.getInstance().signOut();
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

    public DatabaseReference getFireBaseRef() {
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

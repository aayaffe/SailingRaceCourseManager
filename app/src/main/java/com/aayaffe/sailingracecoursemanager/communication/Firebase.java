package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.Boat;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by aayaffe on 21/11/2015.
 */
public class Firebase implements ICommManager {
    private static final String TAG = "Firebase";
    private Context c;
    private static DatabaseReference fb;
    private static DataSnapshot ds;
    private static String currentEventName;
    private String uid;
    private Users users;
    private CommManagerEventListener listener;
    private boolean connected = false;

    public Firebase(Context c) {
        if (this.c ==null)
            this.c = c;
        users = new Users(this);
    }

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
                    users.setCurrentUser(findUser(uid));
                }
                if ((listener != null)&&(!connected))
                    listener.onConnect(new Date());
                connected = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Is this really necessary?
            }

        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    Log.d(TAG, "Uid " + getLoggedInUid() + " Is logged in.");
                    if (findUser(uid) == null){
                        String displayName;
                        try {
                            displayName = getDisplayName(user);
                        }catch(Exception e){
                            Log.e(TAG,"Error getting display name",e);
                            displayName = "User" + new Random().nextInt(10000);
                        }
                        users.setCurrentUser(uid, displayName);
                    }
                    users.setCurrentUser(findUser(uid));

                } else {
                    uid = null;
                    Log.d(TAG,"User has logged out.");
                }
            }
        });
        return 0;
    }


    private String getDisplayName(FirebaseUser user)
    {
        String displayName = user.getDisplayName();
        String email = user.getEmail();
        // If the above were null, iterate the provider data
        // and set with the first non null data
        for (UserInfo userInfo : user.getProviderData()) {
            if (displayName == null && userInfo.getDisplayName() != null) {
                displayName = userInfo.getDisplayName();
            }
            if (email == null && userInfo.getEmail() != null) {
                email = userInfo.getEmail();
            }
        }
        if (displayName==null)
            return convertToAcceptableDisplayName(email);
        return displayName;
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
    public int writeBoatObject(DBObject o) {
        if (o == null || o.getName() == null || o.getName().isEmpty() || currentEventName == null) return -1;
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(o.getName()).setValue(o);
        Log.d("Firebase class","writeBoatObject has written boat:"+ o.toString());
        return 0;
    }

    @Override
    public int writeBuoyObject(DBObject o) {
        if (o == null || o.getName() == null || o.getName().isEmpty()|| currentEventName == null) return -1;
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(o.getName()).setValue(o);
        fb.child(c.getString(R.string.db_events)).child(getCurrentEventName()).child(c.getString(R.string.db_lastbuoyid)).setValue(o.id);
        Log.d("Firebase class","writeBuoyObject has written buoy:"+ o.toString());
        return 0;
    }

    @Override
    public List<DBObject> getAllBoats() {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).getChildren()) {
            DBObject o = ps.getValue(DBObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public List<DBObject> getAllBuoys() {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).getChildren()) {
            DBObject o = ps.getValue(DBObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public int sendAction(RaceManagerAction a, DBObject o) {
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
    public void removeBuoyObject(String title) {
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(title).removeValue();
    }

    @Override
    public User findUser(String uid) {
        try {
            User u;
            u = ds.child(c.getString(R.string.db_users)).child(uid).getValue(User.class);
            return u;
        } catch (Exception e) {
            Log.e(TAG, "Error finding user",e);
            return null;
        }
    }
    public User findUser(UUID uid) {
        return findUser(uid.toString());
    }

    @Override
    public void addUser(User u) {
        fb.child(c.getString(R.string.db_users)).child(u.Uid).setValue(u);
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        uid = null;

    }

    @Override
    public Event getEvent(String eventName) {
        try {
            return ds.child(c.getString(R.string.db_events)).child(eventName).getValue(Event.class);
        } catch (Exception ex) {
            Log.e(TAG, "Failed to get Event: " + eventName,ex);
            return null;
        }
    }

    @Override
    public long getSupportedVersion() {
        if (ds == null || ds.getValue() == null) return -1;
        return (long)ds.child(c.getString(R.string.db_compatible_version)).getValue();
    }

    @Override
    public DBObject getObjectByUUID(UUID u) {
        for(DBObject b: getAllBoats()){
            if (b.getUUID().equals(u)) return b;
        }
        for(DBObject b: getAllBuoys()){
            if (b.getUUID().equals(u)) return b;
        }
        return null;
    }

    public DatabaseReference getFireBaseRef() {
        return fb;
    }

    @Override
    public void writeEvent(Event neu) {
        fb.child(c.getString(R.string.db_events)).child(neu.getName()).setValue(neu);
    }


    @Override
    public String getCurrentEventName() {
        return currentEventName;
    }

    @Override
    public void setCurrentEventName(String currentEventName) {
        //loginToEvent(currentEventName); //TODO: To enable better and finer grained events
        Firebase.currentEventName = currentEventName;
    }



    /***
     *
     * @return the Uid of the currently logged in user.
     * Null if not logged in
     */
    public String getLoggedInUid() {
        return uid;
    }

    public DatabaseReference getEventBoatsReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats));
    }
    public DatabaseReference getEventBuoysReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys));
    }

    @Override
    public ArrayList<DBObject> getAssignedBuoys(DBObject b) {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(b.getName()).child(c.getString(R.string.db_assinged)).getChildren()) {
            DBObject o = getObjectByUUID(UUID.fromString(ps.getValue(String.class)));
            if (o!=null)
                ret.add(o);
        }
        return ret;
    }

    @Override
    public ArrayList<DBObject> getAssignedBoats(DBObject b) {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(b.getName()).child(c.getString(R.string.db_assinged)).getChildren()) {
            DBObject o = getObjectByUUID(UUID.fromString(ps.getValue(String.class)));
            if (o!=null)
                ret.add(o);
        }
        return ret;
    }

    @Override
    public DBObject getBoat(String boatName) {
        if (ds == null || ds.getValue() == null|| currentEventName == null) return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(boatName).getValue(DBObject.class);
    }

    @Override
    public void assignBuoy(DBObject boat, String selectedBuoyName) {
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ;
        DBObject buoy = getBuoy(selectedBuoyName);
        if(buoy==null){
            Log.e(TAG,"Error finding buoy for assignment");
            return;
        }

        removeAssignments(boat);
        removeAssignments(buoy);
        for (DBObject b: getAssignedBoats(buoy)){
            removeAssignment(buoy,b);
            removeAssignments(b);
        }
        for (DBObject b: getAssignedBuoys(boat)){
            removeAssignment(b,boat);
            removeAssignments(b);
        }

        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(boat.getName()).child(c.getString(R.string.db_assinged)).child(buoy.getUuidString()).setValue(buoy.getUuidString());
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(buoy.getName()).child(c.getString(R.string.db_assinged)).child(boat.userUid).setValue(boat.getUuidString());
    }

    private DBObject getBuoy(String selectedBuoyName) {
        if (ds == null || ds.getValue() == null|| currentEventName == null) return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(selectedBuoyName).getValue(DBObject.class);
    }

    @Override
    public void removeAssignment(DBObject buoy, DBObject boat) {
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(buoy.getName()).child(c.getString(R.string.db_assinged)).child(boat.userUid).removeValue();
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(boat.getName()).child(c.getString(R.string.db_assinged)).child(buoy.getUuidString()).removeValue();
    }

    @Override
    public void removeAssignments(DBObject b){
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(b.getName()).child(c.getString(R.string.db_assinged)).removeValue();
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(b.getName()).child(c.getString(R.string.db_assinged)).removeValue();
    }

    @Override
    public List<Boat> getBoatTypes() {
        ArrayList<Boat> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_boattypes)).getChildren()) {
            Boat b = ps.getValue(Boat.class);
            ret.add(b);
        }
        return ret;
    }

    @Override
    public void removeBoat(UUID u) {
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(getObjectByUUID(u).getName()).removeValue();
    }
    @Override
    public void deleteEvent(Event event) {
        fb.child(c.getString(R.string.db_events)).child(event.getName()).removeValue();
    }
}

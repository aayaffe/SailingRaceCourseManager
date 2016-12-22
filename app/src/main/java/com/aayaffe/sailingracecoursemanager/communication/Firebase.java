package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.calclayer.Buoy;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.Boat;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private String Uid;
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
                            Log.e(TAG,"Error getting display name",e);
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
    public int writeBoatObject(Buoy o) {
        if (o == null || o.getName() == null || o.getName().isEmpty() || currentEventName == null) return -1;
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(o.getName()).setValue(o);
        Log.d("Firebase class","writeBoatObject has written boat:"+ o.toString());
        return 0;
    }

    @Override
    public int writeBuoyObject(Buoy o) {
        if (o == null || o.getName() == null || o.getName().isEmpty()|| currentEventName == null) return -1;
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(o.getName()).setValue(o);
        fb.child(c.getString(R.string.db_events)).child(getCurrentEventName()).child(c.getString(R.string.db_lastbuoyid)).setValue(o.id);
        Log.d("Firebase class","writeBuoyObject has written buoy:"+ o.toString());
        return 0;
    }

    @Override
    public List<Buoy> getAllBoats() {
        ArrayList<Buoy> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).getChildren()) {
            Buoy o = ps.getValue(Buoy.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public List<Buoy> getAllBuoys() {
        ArrayList<Buoy> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).getChildren()) {
            Buoy o = ps.getValue(Buoy.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public int sendAction(RaceManagerAction a, Buoy o) {
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
        Uid = null;

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
    public Buoy getObjectByUUID(UUID u) {
        for(Buoy b: getAllBoats()){
            if (b.getUUID().equals(u)) return b;
        }
        for(Buoy b: getAllBuoys()){
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
        return Uid;
    }

    public DatabaseReference getEventBoatsReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats));
    }
    public DatabaseReference getEventBuoysReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys));
    }

    @Override
    public ArrayList<Buoy> getAssignedBuoys(Buoy b) {
        ArrayList<Buoy> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(b.getName()).child(c.getString(R.string.db_assinged)).getChildren()) {
            Buoy o = ps.getValue(Buoy.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public ArrayList<Buoy> getAssignedBoats(Buoy b) {
        ArrayList<Buoy> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(b.getName()).child(c.getString(R.string.db_assinged)).getChildren()) {
            Buoy o = ps.getValue(Buoy.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public Buoy getBoat(String currentBoatName) {
        if (ds == null || ds.getValue() == null|| currentEventName == null) return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(currentBoatName).getValue(Buoy.class);
    }

    @Override
    public void assignBuoy(Buoy boat, String selectedBuoyName) {
        if (ds == null || ds.getValue() == null|| currentEventName == null) return ;
        Buoy buoy = getBuoy(selectedBuoyName);
        if(buoy==null){
            Log.e(TAG,"Error finding buoy for assignment");
            return;
        }
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(boat.getName()).child(c.getString(R.string.db_assinged)).child(buoy.getName()).setValue(buoy);
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(buoy.getName()).child(c.getString(R.string.db_assinged)).child(boat.getName()).setValue(boat);
    }

    private Buoy getBuoy(String selectedBuoyName) {
        if (ds == null || ds.getValue() == null|| currentEventName == null) return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(selectedBuoyName).getValue(Buoy.class);
    }

    @Override
    public void removeAssignment(Buoy buoy, Buoy boat) {
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_buoys)).child(buoy.getName()).child(c.getString(R.string.db_assinged)).removeValue();
        fb.child(c.getString(R.string.db_events)).child(currentEventName).child(c.getString(R.string.db_boats)).child(boat.getName()).child(c.getString(R.string.db_assinged)).child(buoy.getName()).removeValue();
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
}

package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.activities.GoogleMapsActivity;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.crash.FirebaseCrash;
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
//    private static String currentEventName;
    private static Event currentEvent;
    private String uid;
    private Users users;
    private CommManagerEventListener listener;
    private boolean connected = false;
    public EventDeleted eventDeleted;


    public Firebase(Context c) {
        if (this.c ==null)
            this.c = c;
        users = new Users(this);
    }

    @Override
    public int login() {
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
        String e  = email;
        e = e.replace('.',' ');
        e = e.replace('#',' ');
        e = e.replace('$',' ');
        e = e.replace('[',' ');
        e = e.replace(']',' ');
        int index = e.indexOf('@');
        return e.substring(0,index);
    }

    @Override
    public void setCommManagerEventListener(CommManagerEventListener listener){
        this.listener = listener;
    }

    @Override
    public int writeBoatObject(DBObject o) {
        if (o == null || o.userUid == null || o.userUid.isEmpty() || currentEvent == null)
            return -1;
        if (isEventExist(currentEvent)) {
            fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(o.userUid).setValue(o);
            Log.v(TAG, "writeBoatObject has written boat:" + o.getName());
            return 0;
        }
        return -1;
    }

    private boolean isEventExist(String uuid){
        if (uuid == null || uuid.isEmpty())
            return false;
        return ds.child(c.getString(R.string.db_events)).hasChild(uuid);
    }
    private boolean isEventExist(Event e){
        if (e == null || e.getUuid()==null)
            return false;
        return ds.child(c.getString(R.string.db_events)).hasChild(e.getUuid());
    }

    @Override
    public int writeBuoyObject(DBObject o) {
        if (o == null || o.getUUID() == null || currentEvent == null)
            return -1;
        if (isEventExist(currentEvent)) {
            fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(o.getUuidString()).setValue(o);
            fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_lastbuoyid)).setValue(o.id);
            Log.d("Firebase class", "writeBuoyObject has written buoy:" + o.toString());
            return 0;
        }
        return -1;
    }

    @Override
    public int updateBoatLocation(Event e, DBObject boat, AviLocation loc) {
        if(loc==null)
            return -1;
        if (isBoatExist(e,boat)){
            fb.child(c.getString(R.string.db_events)).child(e.getUuid()).child(c.getString(R.string.db_boats)).child(boat.userUid).child("aviLocation").setValue(loc);
            return 0;
        }
        return -1;
    }

    private boolean isBoatExist(Event e, DBObject boat) {
        if (e==null || !isEventExist(e.getUuid()))
            return false;
        if (boat == null || boat.getUUID()==null)
            return false;
        return ds.child(c.getString(R.string.db_events)).child(e.getUuid().toString()).child(c.getString(R.string.db_boats)).hasChild(boat.getUuidString());
    }

    @Override
    public List<DBObject> getAllBoats() {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEvent == null)
            return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).getChildren()) {
            DBObject o = ps.getValue(DBObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public List<DBObject> getAllBuoys() {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEvent == null)
            return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).getChildren()) {
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
        if (ds == null || ds.getValue() == null|| currentEvent == null)
            return 1;
        Long id = (Long) ds.child(c.getString(R.string.db_events)).child(getCurrentEvent().getUuid()).child(c.getString(R.string.db_lastbuoyid)).getValue();
        if (id != null) {
            return id + 1;
        } else return 1;
    }

    @Override
    public void removeBuoyObject(String uuid) {
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(uuid).removeValue();
    }

    @Override
    @Nullable
    public User findUser(String uid) {
        if (uid==null || ds ==null)
            return null;
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

    /**
     * Returns the first event with the name eventName.
     * null if no event with this name found.
     * @param eventName
     * @return the first event with the name eventName.
     * null if no event with this name found.
     */
    @Override
    public Event getEvent(String eventName) {
        try {
            for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).getChildren()){
                Event e = ps.getValue(Event.class);
                if (e.getName().equals(eventName))
                    return e;
            }
            return null;
        } catch (Exception ex) {
            Log.e(TAG, "Failed to get Event: " + eventName,ex);
            return null;
        }
    }

    @Override
    public long getSupportedVersion() {
        if (ds == null || ds.getValue() == null)
            return -1;
        return (long)ds.child(c.getString(R.string.db_compatible_version)).getValue();
    }

    @Override
    public DBObject getObjectByUUID(UUID u) {
        for(DBObject b: getAllBoats()){
            if (b.getUUID().equals(u))
                return b;
        }
        for(DBObject b: getAllBuoys()){
            if (b.getUUID().equals(u))
                return b;
        }
        return null;
    }

    public DatabaseReference getFireBaseRef() {
        return fb;
    }

    @Override
    public void writeEvent(Event neu) {
        fb.child(c.getString(R.string.db_events)).child(neu.getUuid().toString()).setValue(neu);
    }


    @Override
    public Event getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public void setCurrentEvent(Event e) {
        //loginToEvent(currentEventName); //TODO: To enable better and finer grained events
        Firebase.currentEvent = e;
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
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats));
    }
    public DatabaseReference getEventBuoysReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys));
    }

    @Override
    public List<DBObject> getAssignedBuoys(DBObject b) {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (b==null|| b.userUid == null)
            return ret;
        if (ds == null || ds.getValue() == null|| currentEvent == null )
            return ret;

        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(b.userUid).child(c.getString(R.string.db_assinged)).getChildren()) {
            DBObject o = getObjectByUUID(UUID.fromString(ps.getValue(String.class)));
            if (o!=null)
                ret.add(o);
        }
        return ret;
    }

    @Override
    public List<DBObject> getAssignedBoats(DBObject b) {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null|| currentEvent == null)
            return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(b.getUuidString()).child(c.getString(R.string.db_assinged)).getChildren()) {
            DBObject o = getObjectByUUID(UUID.fromString(ps.getValue(String.class)));
            if (o!=null)
                ret.add(o);
        }
        return ret;
    }

    @Override
    public DBObject getBoat(String uuid) {
        if (ds == null || ds.getValue() == null|| currentEvent == null || uuid==null || uuid.isEmpty())
            return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(uuid).getValue(DBObject.class);
    }

    @Override
    public void assignBuoy(DBObject boat, String uuid) {
        if (ds == null || ds.getValue() == null|| currentEvent == null)
            return ;
        DBObject buoy = getBuoy(uuid);
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

        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(boat.userUid).child(c.getString(R.string.db_assinged)).child(buoy.getUuidString()).setValue(buoy.getUuidString());
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(buoy.getUuidString()).child(c.getString(R.string.db_assinged)).child(boat.userUid).setValue(boat.getUuidString());
    }

    private DBObject getBuoy(String uuid) {
        if (ds == null || ds.getValue() == null|| currentEvent == null)
            return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(uuid).getValue(DBObject.class);
    }

    @Override
    public void removeAssignment(DBObject buoy, DBObject boat) {
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(buoy.getUuidString()).child(c.getString(R.string.db_assinged)).child(boat.userUid).removeValue();
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(boat.userUid).child(c.getString(R.string.db_assinged)).child(buoy.getUuidString()).removeValue();
    }

    @Override
    public void removeAssignments(DBObject b){
        for(DBObject o: getAssignedBuoys(b)){
            removeAssignment(o,b);
        }
        for(DBObject o: getAssignedBoats(b)){
            removeAssignment(b,o);
        }
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(b.getUuidString()).child(c.getString(R.string.db_assinged)).removeValue();
        if (b.userUid == null)
            return ;
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(b.userUid).child(c.getString(R.string.db_assinged)).removeValue();
    }

    @Override
    public List<Boat> getBoatTypes() {
        ArrayList<Boat> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null)
            return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_boattypes)).getChildren()) {
            Boat b = ps.getValue(Boat.class);
            ret.add(b);
        }
        return ret;
    }

    @Override
    public void removeBoat(UUID u) {
        fb.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(getObjectByUUID(u).userUid).removeValue();
    }
    @Override
    public void deleteEvent(Event event) {
        fb.child(c.getString(R.string.db_events)).child(event.getUuid().toString()).removeValue();
    }

    @Override
    public void addRaceCourseDescriptor(RaceCourseDescriptor ct) {
        fb.child(c.getString(R.string.db_race_course_descriptors)).child(ct.getName()).setValue(ct);
    }

    @Override
    public List<RaceCourseDescriptor> getRaceCourseDescriptors(){
        List<RaceCourseDescriptor> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null)
            return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_race_course_descriptors)).getChildren()) {
            RaceCourseDescriptor b = ps.getValue(RaceCourseDescriptor.class);
            ret.add(b);
        }
        return ret;
    }

    @Override
    public boolean isAdmin(User u) {
        if (u==null){
            return false;
        }
        return ds.child(c.getString(R.string.db_admins)).hasChild(u.Uid);
    }

    public void subscribeToEventDeletion(final Event event, boolean subscribe){
        if (event==null) {

            FirebaseCrash.report(new Exception("event was null in subscribe to event deletion, EventName"));
            return;
        }
        ValueEventListener valeventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()==null && eventDeleted != null) {
                    eventDeleted.onEventDeleted(event);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "EventDeleted:onCancelled", databaseError.toException());
            }
        };
        if (event.getUuid()!=null) {
            if (subscribe) {
                fb.child(c.getString(R.string.db_events)).child(event.getUuid().toString()).child(c.getString(R.string.db_uuid)).addValueEventListener(valeventListener);
            } else {
                fb.child(c.getString(R.string.db_events)).child(event.getUuid().toString()).child(c.getString(R.string.db_uuid)).removeEventListener(valeventListener);
            }
        }
    }

    public interface EventDeleted{
        void onEventDeleted(Event e);
    }

}

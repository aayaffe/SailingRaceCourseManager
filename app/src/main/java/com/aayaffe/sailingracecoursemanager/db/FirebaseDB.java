package com.aayaffe.sailingracecoursemanager.db;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.Users.Users;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Avi Marine Innovations - www.avimarine.in
 * <p>
 * Created by Amit Y. on 21/11/2015.
 */
public class FirebaseDB implements IDBManager {
    private static final String TAG = "FirebaseDB";
    public static DatabaseReference fb;
    public static DataSnapshot ds;
    private static Event currentEvent;
    private EventDeleted eventDeleted;
    private final Context c;
    private String uid;
    private final Users users;
    public final List<CommManagerEventListener> listeneres = new ArrayList<>();
    private boolean connected = false;
    private static FirebaseDB db;

    public static FirebaseDB getInstance(Context c){
        if (db==null){
            db = new FirebaseDB(c);
        }
//        if (fb==null){
//            Log.e(TAG, "fb is null in getInstance");
//            fb = FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl(c.getString(R.string.firebase_base_url));
//        }
        return db;

    }
    private FirebaseDB(Context c) {
        this.c = c;
        Users.Init(this, PreferenceManager.getDefaultSharedPreferences(c));
        users = Users.getInstance();
    }

    @Override
    public int login() {
//            Log.d(TAG,"in login function.");
//            fb = FirebaseDatabase.getInstance()
//                    .getReferenceFromUrl(c.getString(R.string.firebase_base_url));
//            fb.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "in onDataChange");
//                ds = dataSnapshot;
//                if (users.getCurrentUser() == null) {
//                    Users.setCurrentUser(findUser(uid));
//                }
//                if ((listeneres != null) && (!connected)) {
//                    for (CommManagerEventListener listener : listeneres) {
//                        if (listener != null)
//                            listener.onConnect(new Date());
//                    }
//                }
//                connected = true;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //Is this really necessary?
//            }
//
//        });
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
//                Log.d(TAG, "in onAuthStateChange");
//                final FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (ds != null)
//                    setUser(user);
//                else {
//                    if (user != null)
//                        uid = user.getUid();
//                    else
//                        uid = null;
//                }
//            }
//        });
        return 0;
    }

    public void setUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        setUser(auth.getCurrentUser());
    }

    public void setUser(FirebaseUser user) {
        if (user != null) {
            uid = user.getUid();
            Log.d(TAG, "Uid " + getLoggedInUid() + " Is logged in.");
            if (findUser(uid) == null) {
                String displayName;
                try {
                    displayName = getDisplayName(user);
                } catch (Exception e) {
                    Log.e(TAG, "Error getting display name", e);
                    displayName = "User" + new Random().nextInt(10000);
                }
                Users.setCurrentUser(uid, displayName);
            }
            Users.setCurrentUser(findUser(uid));
        } else {
            uid = null;
            Users.setCurrentUser(null);
            Log.d(TAG, "User has logged out.");
        }
    }

    @Override
    public void setCommManagerEventListener(CommManagerEventListener listener) {
        this.listeneres.add(listener);
    }

    @Override
    public int writeBoatObject(DBObject o) {
        if (o == null || o.userUid == null || o.userUid.isEmpty() || currentEvent == null)
            return -1;
        if (isEventExist(currentEvent)) {
            getEventDBRef(currentEvent).child(c.getString(R.string.db_boats)).child(o.userUid).setValue(o);
            Log.v(TAG, "writeBoatObject has written boat:" + o.getName());
            return 0;
        }
        return -1;
    }
//    private DatabaseReference getEventDBRef(String eventUUID){
//        return fb.child(c.getString(R.string.db_events)).child(eventUUID);
//    }
    private DatabaseReference getEventDBRef(Event e){
        return fb.child(c.getString(R.string.db_events)).child(e.getUuid());
    }

    @Contract("null -> false")
    private boolean isEventExist(Event e) {
        return !(e == null || e.getUuid() == null) && ds.child(c.getString(R.string.db_events)).hasChild(e.getUuid());
    }

    @Override
    public int writeBuoyObject(DBObject o) {
        if (o == null || o.getUUID() == null || currentEvent == null)
            return -1;
        if (isEventExist(currentEvent)) {
            getEventDBRef(currentEvent).child(c.getString(R.string.db_buoys)).child(o.getUuidString()).setValue(o);
            getEventDBRef(currentEvent).child(c.getString(R.string.db_lastbuoyid)).setValue(o.id);
            Log.d("FirebaseDB class", "writeBuoyObject has written buoy:" + o.toString());
            return 0;
        }
        return -1;
    }

    @Override
    public int updateBoatLocation(Event e, DBObject boat, AviLocation loc) {
        if (loc == null)
            return -1;
        if (isBoatExist(e, boat)) {
            getEventDBRef(e).child(c.getString(R.string.db_boats)).child(boat.userUid).child("aviLocation").setValue(loc);
            return 0;
        }
        return -1;
    }

    @Contract("null, _ -> false")
    private boolean isBoatExist(Event e, DBObject boat) {
        return !(e == null || !isEventExist(e.getUuid())) &&
                !(boat == null || boat.getUUID() == null) &&
                ds.child(c.getString(R.string.db_events)).child(e.getUuid()).child(c.getString(R.string.db_boats)).hasChild(boat.userUid);
        //TODO: Decide to use ds or getEventDBREF()?
    }

    @Contract("null -> false")
    private boolean isEventExist(String uuid) {
        return !(uuid == null || uuid.isEmpty()) && ds.child(c.getString(R.string.db_events)).hasChild(uuid);
    }

    @Override
    public List<DBObject> getAllBoats() {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (ds == null || ds.getValue() == null || currentEvent == null)
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
        if (ds == null || ds.getValue() == null || currentEvent == null)
            return ret;
        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).getChildren()) {
            DBObject o = ps.getValue(DBObject.class);
            ret.add(o);
        }
        return ret;
    }

    @Override
    public long getNewBuoyId() {
        if (ds == null || ds.getValue() == null || currentEvent == null)
            return 1;
        Long id = (Long) ds.child(c.getString(R.string.db_events)).child(getCurrentEvent().getUuid()).child(c.getString(R.string.db_lastbuoyid)).getValue();
        if (id != null) {
            return id + 1;
        } else return 1;
    }

    @Override
    public void removeBuoyObject(String uuid) {
        getEventDBRef(currentEvent).child(c.getString(R.string.db_buoys)).child(uuid).removeValue();
    }

    @Override
    @Nullable
    public User findUser(String uid) {
        if (uid == null || ds == null)
            return null;
        try {
            User u;
            u = ds.child(c.getString(R.string.db_users)).child(uid).getValue(User.class);
            return u;
        } catch (Exception e) {
            Log.e(TAG, "Error finding user", e);
            return null;
        }
    }

    @Override
    public void addUser(User u) {
        if (u == null)
            return;
        if (fb!=null)
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
     *
     * @param eventName
     * @return the first event with the name eventName.
     * null if no event with this name found.
     */
    @Override
    public Event getEvent(String eventName) {
        try {
            for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).getChildren()) {
                Event e = ps.getValue(Event.class);
                if (e.getName().equals(eventName))
                    return e;
            }
            return null;
        } catch (Exception ex) {
            Log.e(TAG, "Failed to get Event: " + eventName, ex);
            return null;
        }
    }

    @Override
    public long getSupportedVersion() {
        if (ds == null || ds.getValue() == null)
            return -1;
        return (long) ds.child(c.getString(R.string.db_compatible_version)).getValue();
    }

    @Override
    public DBObject getObjectByUUID(UUID u) {
        for (DBObject b : getAllBoats()) {
            if (b.getUUID().equals(u))
                return b;
        }
        for (DBObject b : getAllBuoys()) {
            if (b.getUUID().equals(u))
                return b;
        }
        return null;
    }

    @Override
    public void writeEvent(Event neu) {
        getEventDBRef(neu).setValue(neu);
    }

    @Override
    public Event getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public void setCurrentEvent(Event e) {
        //loginToEvent(currentEventName); //TODO: To enable better and finer grained events
        FirebaseDB.currentEvent = e;
    }

//    @Override
//    public List<DBObject> getAssignedBuoys(DBObject b) {
//        ArrayList<DBObject> ret = new ArrayList<>();
//        if (b == null || b.userUid == null)
//            return ret;
//        if (ds == null || ds.getValue() == null || currentEvent == null)
//            return ret;
//
//        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(b.userUid).child(c.getString(R.string.db_assinged)).getChildren()) {
//            DBObject o = getObjectByUUID(UUID.fromString(ps.getValue(String.class)));
//            if (o != null)
//                ret.add(o);
//        }
//        return ret;
//    }

    @Override
    public List<DBObject> getAssignedBuoys(DBObject b) {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (b == null || b.userUid == null)
            return ret;
        if (ds == null || ds.getValue() == null || currentEvent == null)
            return ret;

        Map<String,List<String>> as = (HashMap<String,List<String>>)ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child("Assignments").getValue();
        if (as!=null) {
            List<String> buoys = as.get(b.userUid);
            if (buoys!=null) {
                for (String uuid : buoys) {
                    if (uuid != null) {
                        DBObject buoy = getBuoy(uuid);
                        if (buoy != null)
                            ret.add(buoy);
                    }
                }
            }
        }
        return ret;
    }

//    @Override
//    public List<DBObject> getAssignedBoats(DBObject b) {
//        ArrayList<DBObject> ret = new ArrayList<>();
//        if (ds == null || ds.getValue() == null || currentEvent == null)
//            return ret;
//        for (DataSnapshot ps : ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(b.getUuidString()).child(c.getString(R.string.db_assinged)).getChildren()) {
//            DBObject o = getObjectByUUID(UUID.fromString(ps.getValue(String.class)));
//            if (o != null)
//                ret.add(o);
//        }
//        return ret;
//    }

    @Override
    public List<DBObject> getAssignedBoats(DBObject b) {
        ArrayList<DBObject> ret = new ArrayList<>();
        if (b == null || b.userUid == null)
            return ret;
        if (ds == null || ds.getValue() == null || currentEvent == null)
            return ret;
        Map<String,List<String>> as = (HashMap<String,List<String>>)ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child("Assignments").getValue();
        if (as!=null) {
            for (String userUid : as.keySet()) {
                if (userUid != null) {
                    List<String> buoys = as.get(userUid);
                    if (buoys.contains(b.getUuidString())) {
                        DBObject boat = getBoat(userUid);
                        if (boat != null)
                            ret.add(boat);
                    }
                }
            }
        }
        return ret;
    }


//    @Override
//    public void assignBuoy(DBObject boat, String uuid) {
//        if (ds == null || ds.getValue() == null || currentEvent == null)
//            return;
//        DBObject buoy = getBuoy(uuid);
//        if (buoy == null) {
//            Log.e(TAG, "Error finding buoy for assignment");
//            return;
//        }
//
//        removeAssignments(boat);
//        removeAssignments(buoy);
//        for (DBObject b : getAssignedBoats(buoy)) {
//            removeAssignment(buoy, b);
//            removeAssignments(b);
//        }
//        for (DBObject b : getAssignedBuoys(boat)) {
//            removeAssignment(b, boat);
//            removeAssignments(b);
//        }
//
//        getEventDBRef(currentEvent).child(c.getString(R.string.db_boats)).child(boat.userUid).child(c.getString(R.string.db_assinged)).child(buoy.getUuidString()).setValue(buoy.getUuidString());
//        getEventDBRef(currentEvent).child(c.getString(R.string.db_buoys)).child(buoy.getUuidString()).child(c.getString(R.string.db_assinged)).child(boat.userUid).setValue(boat.getUuidString());
//    }

    @Override
    public void assignBuoy(DBObject boat, String uuid) {
        if (ds == null || ds.getValue() == null || currentEvent == null)
            return;
        DBObject buoy = getBuoy(uuid);
        if (buoy == null) {
            Log.e(TAG, "Error finding buoy for assignment");
            return;
        }

        removeAssignments(boat);
        removeAssignments(buoy);
        for (DBObject b : getAssignedBoats(buoy)) {
            removeAssignment(buoy, b);
            removeAssignments(b);
        }
        for (DBObject b : getAssignedBuoys(boat)) {
            removeAssignment(b, boat);
            removeAssignments(b);
        }


        List<String> buoysUUID = new ArrayList<>();
        /* For Adding more than one buoy:
        List<DBObject> buoys = getAssignedBuoys(boat);
        for(DBObject b: buoys){
            if ((b!=null)&&(b.getUuidString()!=null)){
                buoysUUID.add(b.getUuidString());
            }
        }*/
        buoysUUID.add(uuid);
        getEventDBRef(currentEvent).child("Assignments").child(boat.userUid).setValue(buoysUUID);
    }

    @Override
    public void removeAssignment(DBObject buoy, DBObject boat) {
        List<DBObject> buoys = getAssignedBuoys(boat);
        if (buoys!=null) buoys.remove(buoy);
        List<String> buoysUUID = new ArrayList<>();
        for(DBObject b: buoys){
            if ((b!=null)&&(b.getUuidString()!=null)){
                buoysUUID.add(b.getUuidString());
            }
        }
        getEventDBRef(currentEvent).child("Assignments").child(boat.userUid).setValue(buoysUUID);
    }

//    @Override
//    public void removeAssignments(DBObject b) {
//        for (DBObject o : getAssignedBuoys(b)) {
//            removeAssignment(o, b);
//        }
//        for (DBObject o : getAssignedBoats(b)) {
//            removeAssignment(b, o);
//        }
//        getEventDBRef(currentEvent).child(c.getString(R.string.db_buoys)).child(b.getUuidString()).child(c.getString(R.string.db_assinged)).removeValue();
//        if (b.userUid == null)
//            return;
//        getEventDBRef(currentEvent).child(c.getString(R.string.db_boats)).child(b.userUid).child(c.getString(R.string.db_assinged)).removeValue();
//    }

    @Override
    public void removeAssignments(DBObject b) {
        for (DBObject o : getAssignedBuoys(b)) {
            removeAssignment(o, b);
        }
        for (DBObject o : getAssignedBoats(b)) {
            removeAssignment(b, o);
        }
    }

    @Override
    public DBObject getBoat(String uuid) {
        if (ds == null || ds.getValue() == null || currentEvent == null || uuid == null || uuid.isEmpty())
            return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats)).child(uuid).getValue(DBObject.class);
    }

    @Override
    public DBObject getBoatByUserUid(String uid){
        for (DBObject ao : getAllBoats()) {
            if (isOwnObject(uid, ao)) {
                return ao;
            }
        }
        return null;
    }
    private boolean isOwnObject(String uid, DBObject o) {
        return o != null && o.userUid!=null && o.userUid.equals(uid);
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
        getEventDBRef(currentEvent).child(c.getString(R.string.db_boats)).child(getObjectByUUID(u).userUid).removeValue();
    }

    @Override
    public void deleteEvent(Event e) {
        getEventDBRef(e).removeValue();
    }

    @Override
    public void addRaceCourseDescriptor(RaceCourseDescriptor ct) {
        fb.child(c.getString(R.string.db_race_course_descriptors)).child(ct.name).setValue(ct);
    }

    @Override
    public List<RaceCourseDescriptor> getRaceCourseDescriptors() {
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
        return u != null && ds.child(c.getString(R.string.db_admins)).hasChild(u.Uid);
    }

    @Override
    public synchronized void removeCommManagerEventListener(CommManagerEventListener onConnectEventListener) {
        if (listeneres != null)
            listeneres.remove(onConnectEventListener);
    }

    /***
     *
     * @return the Uid of the currently logged in user.
     * Null if not logged in
     */
    @Override
    public String getLoggedInUid() {
        return uid;
    }

    private String getDisplayName(FirebaseUser user) {
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
        if (displayName == null)
            return convertToAcceptableDisplayName(email);
        return displayName;
    }

    /***
     * FirebaseDB does not accept emails in the database path (i.e. '.' are not allowed)
     * There for we will convert to a better display name
     * @param email
     * @return email's user name only without '.', '#', '$', '[', or ']'
     */
    @NonNull
    private String convertToAcceptableDisplayName(String email) {
        if (email == null)
            return null;
        String e = email;
        e = e.replace('.', ' ');
        e = e.replace('#', ' ');
        e = e.replace('$', ' ');
        e = e.replace('[', ' ');
        e = e.replace(']', ' ');
        int index = e.indexOf('@');
        return e.substring(0, index);
    }

    @Override
    public User findUser(UUID uid) {
        return findUser(uid.toString());
    }

    @Override
    public DatabaseReference getEventBoatsReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_boats));
    }

    @Override
    public DatabaseReference getFireBaseRef() {
        return fb;
    }

    @Override
    public DatabaseReference getEventBuoysReference() {
        return getFireBaseRef().child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys));
    }

    @Override
    public DBObject getBuoy(String uuid) {
        if (ds == null || ds.getValue() == null || currentEvent == null)
            return null;
        return ds.child(c.getString(R.string.db_events)).child(currentEvent.getUuid()).child(c.getString(R.string.db_buoys)).child(uuid).getValue(DBObject.class);
    }

    @Override
    public void subscribeToEventDeletion(final Event e, boolean subscribe) {
        if (e == null) {
            return;
        }
        ValueEventListener valeventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null && eventDeleted != null) {
                    eventDeleted.onEventDeleted(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "EventDeleted:onCancelled", databaseError.toException());
            }
        };
        if (e.getUuid() != null) {
            if (subscribe) {
                getEventDBRef(e).child(c.getString(R.string.db_uuid)).addValueEventListener(valeventListener);
            } else {
                getEventDBRef(e).child(c.getString(R.string.db_uuid)).removeEventListener(valeventListener);
            }
        }
    }

    @Override
    public void writeLeaveEvent(User currentUser, Event currentEvent) {
        if (currentEvent == null || currentUser == null) {
            return;
        }
        DBObject boat = null;
        for (DBObject o : getAllBoats()) {

            if ((o.userUid!=null)&&(o.userUid.equals(currentUser.Uid))) {
                boat = o;
                break;
            }
        }
        if (boat != null) {
            boat.setLeftEvent(new Date());
        }
        writeBoatObject(boat);
    }

    public interface EventDeleted {
        void onEventDeleted(Event e);
    }

    public void setEventDeleted(EventDeleted eventDeleted) {
        this.eventDeleted = eventDeleted;
    }

}

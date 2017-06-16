package com.aayaffe.sailingracecoursemanager.db;

import com.aayaffe.sailingracecoursemanager.events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.UUID;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 22/09/2015.
 */
public interface IDBManager {
    int login();

    void setCommManagerEventListener(CommManagerEventListener listener);

    int writeBoatObject(DBObject o);
    int writeBuoyObject(DBObject o);

    int updateBoatLocation(Event e, DBObject boat, AviLocation loc);

    List<DBObject> getAllBoats();  //ships
    List<DBObject> getAllBuoys();  //Just buoys, without ships


    long getNewBuoyId();

    void removeBuoyObject(String uuid);

    User findUser(String uid);

    void addUser(User u);

    void logout();

    Event getEvent(String eventName);

    long getSupportedVersion();

    DBObject getObjectByUUID(UUID u);

    void writeEvent(Event neu);

    Event getCurrentEvent();

    void setCurrentEvent(Event currentEvent);

    List<DBObject> getAssignedBuoys(DBObject b);

    List<DBObject> getAssignedBoats(DBObject b);

    DBObject getBoat(String currentBoatName);

    void assignBuoy(DBObject boat, String selectedBuoyName);

    void removeAssignment(DBObject buoy, DBObject boat);

    void removeAssignments(DBObject b);

    List<Boat> getBoatTypes();

    void removeBoat(UUID u);

    void deleteEvent(Event event);

    void addRaceCourseDescriptor(RaceCourseDescriptor ct);

    List<RaceCourseDescriptor> getRaceCourseDescriptors();

    boolean isAdmin(User u);

    void removeCommManagerEventListener(CommManagerEventListener onConnectEventListener);

    String getLoggedInUid();

    User findUser(UUID uid);

    DatabaseReference getEventBoatsReference();

    DatabaseReference getFireBaseRef();

    DatabaseReference getEventBuoysReference();

    DBObject getBuoy(String uuid);

    void subscribeToEventDeletion(Event event, boolean subscribe);

    void writeLeaveEvent(User currentUser, Event currentEvent);
}

package com.aayaffe.sailingracecoursemanager.communication;

import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Initializing_Layer.Boat;
import com.aayaffe.sailingracecoursemanager.Users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 22/09/2015.
 */
public interface ICommManager {
    int login();

    void setCommManagerEventListener(CommManagerEventListener listener);

    int writeBoatObject(DBObject o);
    int writeBuoyObject(DBObject o);

    List<DBObject> getAllBoats();  //ships
    List<DBObject> getAllBuoys();  //Just buoys, without ships

    int sendAction(RaceManagerAction a, DBObject o);

    long getNewBuoyId();

    void removeBuoyObject(String title);

    User findUser(String uid);

    void addUser(User u);

    void logout();

    Event getEvent(String eventName);

    long getSupportedVersion();

    DBObject getObjectByUUID(UUID u);

    void writeEvent(Event neu);

    String getCurrentEventName();

    void setCurrentEventName(String currentEventName);

    ArrayList<DBObject> getAssignedBuoys(DBObject b);

    ArrayList<DBObject> getAssignedBoats(DBObject b);

    DBObject getBoat(String currentBoatName);

    void assignBuoy(DBObject boat, String selectedBuoyName);

    void removeAssignment(DBObject buoy, DBObject boat);

    void removeAssignments(DBObject b);

    List<Boat> getBoatTypes();

    void removeBoat(UUID u);

    void deleteEvent(Event event);
}

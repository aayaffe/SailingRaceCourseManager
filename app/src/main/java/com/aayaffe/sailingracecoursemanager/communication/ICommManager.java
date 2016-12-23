package com.aayaffe.sailingracecoursemanager.communication;

import com.aayaffe.sailingracecoursemanager.calclayer.Buoy;
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
    int login(String user, String password, String nickname);

    void setCommManagerEventListener(CommManagerEventListener listener);

    int writeBoatObject(Buoy o);
    int writeBuoyObject(Buoy o);

    List<Buoy> getAllBoats();  //ships
    List<Buoy> getAllBuoys();  //Just buoys, without ships

    int sendAction(RaceManagerAction a, Buoy o);

    long getNewBuoyId();

    void removeBuoyObject(String title);

    User findUser(String uid);

    void addUser(User u);

    void logout();

    Event getEvent(String eventName);

    long getSupportedVersion();

    Buoy getObjectByUUID(UUID u);

    void writeEvent(Event neu);

    String getCurrentEventName();

    void setCurrentEventName(String currentEventName);

    ArrayList<Buoy> getAssignedBuoys(Buoy b);

    ArrayList<Buoy> getAssignedBoats(Buoy b);

    Buoy getBoat(String currentBoatName);

    void assignBuoy(Buoy boat, String selectedBuoyName);

    void removeAssignment(Buoy buoy, Buoy boat);

    List<Boat> getBoatTypes();

    void removeBoat(UUID u);
}

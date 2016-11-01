package com.aayaffe.sailingracecoursemanager.communication;

import com.aayaffe.sailingracecoursemanager.Calc_Layer.Buoy;
import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Users.User;

import java.util.List;

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
}

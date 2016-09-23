package com.aayaffe.sailingracecoursemanager.communication;

import com.aayaffe.sailingracecoursemanager.Events.Event;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseDescriptor;
import com.aayaffe.sailingracecoursemanager.Racecourse.RaceCourseDescriptorGeneral;
import com.aayaffe.sailingracecoursemanager.Users.User;

import java.util.List;

/**
 * Created by aayaffe on 22/09/2015.
 */
public interface ICommManager {
    int login(String user, String password, String nickname);

    void setCommManagerEventListener(CommManagerEventListener listener);

    int writeBoatObject(AviObject o);
    int writeBuoyObject(AviObject o);
    int writeRaceCourseDescriptor(RaceCourseDescriptorGeneral rcd);

    List<RaceCourseDescriptorGeneral> getRaceCourseDescriptors();
    List<AviObject> getAllBoats();
    List<AviObject> getAllBuoys();

    int sendAction(RaceManagerAction a, AviObject o);

    long getNewBuoyId();

    void removeBueyObject(String title);

    User findUser(String uid);

    void addUser(User u);

    void logout();

    Event getEvent(String eventName);

    long getSupportedVersion();
}

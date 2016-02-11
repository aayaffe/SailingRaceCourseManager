package com.aayaffe.sailingracecoursemanager.communication;

import android.location.Location;

import java.util.List;

/**
 * Created by aayaffe on 22/09/2015.
 */
public interface ICommManager {
    public int login (String user, String password, String nickname);
    public int writeBoatObject(AviObject o);
    public int writeBuoyObject(AviObject o);

    public List<AviObject> getAllBoats();
    public List<AviObject> getAllBuoys();

    public int sendAction (RaceManagerAction a, AviObject o);

    public long getNewBuoyId();

    void removeBueyObject(String title);
}

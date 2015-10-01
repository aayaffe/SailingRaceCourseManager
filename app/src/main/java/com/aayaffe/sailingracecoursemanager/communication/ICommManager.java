package com.aayaffe.sailingracecoursemanager.communication;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import java.util.List;

/**
 * Created by aayaffe on 22/09/2015.
 */
public interface ICommManager {
    public int login (String user, String password, String nickname);
    public int sendLoc (Location l);
    public Location getLoc(int id);
    public List<Object> getAllLocs();
    public int sendAction (RaceManagerAction a);
}

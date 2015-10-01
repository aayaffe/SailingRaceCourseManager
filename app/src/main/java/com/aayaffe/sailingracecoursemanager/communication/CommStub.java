package com.aayaffe.sailingracecoursemanager.communication;

import android.location.Location;

import java.util.List;

/**
 * Created by aayaffe on 22/09/2015.
 */
public class CommStub implements ICommManager {
    @Override
    public int login(String user, String password, String nickname) {
        return 0;
    }

    @Override
    public int sendLoc(Location l) {
        return 0;
    }

    @Override
    public Location getLoc(int id) {
        Location ret = new Location("Own Location");
        ret.setLatitude(42.556418);
        ret.setLongitude(1.535633);
        return ret;
    }

    @Override
    public List<Object> getAllLocs() {
        return null;
    }

    @Override
    public int sendAction(RaceManagerAction a) {
        return 0;
    }
}

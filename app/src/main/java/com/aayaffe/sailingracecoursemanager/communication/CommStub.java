package com.aayaffe.sailingracecoursemanager.communication;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.general.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.location.QBLocations;
import com.quickblox.location.model.QBLocation;
import com.quickblox.location.request.QBLocationRequestBuilder;
import com.quickblox.location.request.SortField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aayaffe on 22/09/2015.
 */
public class CommStub implements ICommManager {

    List<Object> objects = new ArrayList<>();

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
        Object o = new Object();
        o.lastUpdate = new Date(System.currentTimeMillis());;
        o.name = "Worker1";
        o.location = GeoUtils.createLocation(32.75, 34.58);
        o.color = "blue";
        o.type = ObjectTypes.WorkerBoat;
        if (objects.contains(o)) {
            objects.remove(o);
            objects.add(o);
        } else objects.add(o);
        o = new Object();
        o.lastUpdate = new Date(System.currentTimeMillis());;
        o.name = "Worker2";
        o.location = GeoUtils.createLocation(32.8, 34.59);
        o.color = "cyan";
        o.type = ObjectTypes.WorkerBoat;
        if (objects.contains(o)) {
            objects.remove(o);
            objects.add(o);
        } else objects.add(o);
        return objects;
    }

    @Override
    public int sendAction(RaceManagerAction a) {
        return 0;
    }
}

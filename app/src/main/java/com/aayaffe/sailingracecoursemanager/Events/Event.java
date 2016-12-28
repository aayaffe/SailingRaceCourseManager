package com.aayaffe.sailingracecoursemanager.Events;

import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by aayaffe on 19/02/2016.
 */
public class Event {
    private String name;
    @Exclude
    private UUID _uuid;
    private int lastBuoyId;
    private String eventManager;
    private HashMap<String,DBObject> boats;
    private HashMap<String,DBObject> buoys;
    public int yearStart;
    public int yearEnd;
    public int monthStart;
    public int monthEnd;
    public int dayStart;
    public int dayEnd;





    public Event(){
        _uuid = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastBuoyId() {
        return lastBuoyId;
    }

    public void setLastBuoyId(int lastBuoyId) {

        this.lastBuoyId = lastBuoyId;
    }

    public HashMap<String, DBObject> getBoats() {
        return boats;
    }

    public void setBoats(HashMap<String, DBObject> boats) {
        this.boats = boats;
    }

    public HashMap<String, DBObject> getBuoys() {
        return buoys;
    }

    public void setBuoys(HashMap<String, DBObject> buoys) {
        this.buoys = buoys;
    }

    public String getUuid() {
        return _uuid.toString();
    }

    public void setUuid(String uuid) {
        this._uuid = UUID.fromString(uuid);
    }
    public String getManagerUuid() {
        return eventManager;
    }

    public void setManagerUuid(String uuid) {
        this.eventManager = uuid;
    }
}

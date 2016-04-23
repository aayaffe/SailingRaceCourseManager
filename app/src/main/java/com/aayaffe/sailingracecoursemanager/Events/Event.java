package com.aayaffe.sailingracecoursemanager.Events;

import android.os.Parcel;
import android.os.Parcelable;

import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Dictionary;
import java.util.UUID;

/**
 * Created by aayaffe on 19/02/2016.
 */
public class Event {
    private String name;
    private UUID uuid;
    private int lastBuoyId;
    private User eventManager;
    @JsonIgnore
    private Dictionary<String,AviObject> boats;
    @JsonIgnore
    private Dictionary<String,AviObject> buoys;

    public Event(){
        setUuid(UUID.randomUUID());
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
        lastBuoyId = lastBuoyId;
    }

    public User getEventManager() {
        return eventManager;
    }

    public void setEventManager(User eventManager) {
        this.eventManager = eventManager;
    }


    public Dictionary<String, AviObject> getBoats() {
        return boats;
    }

    public void setBoats(Dictionary<String, AviObject> boats) {
        this.boats = boats;
    }

    public Dictionary<String, AviObject> getBuoys() {
        return buoys;
    }

    public void setBuoys(Dictionary<String, AviObject> buoys) {
        this.buoys = buoys;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}

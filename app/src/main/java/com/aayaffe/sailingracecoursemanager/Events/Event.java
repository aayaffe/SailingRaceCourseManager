package com.aayaffe.sailingracecoursemanager.Events;

import android.os.Parcel;
import android.os.Parcelable;

import com.aayaffe.sailingracecoursemanager.Users.User;
import com.aayaffe.sailingracecoursemanager.communication.AviObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Dictionary;

/**
 * Created by aayaffe on 19/02/2016.
 */
public class Event implements Parcelable {
    private String name;
    private int lastBuoyId;
    private User eventManager;
    @JsonIgnore
    private Dictionary<String,AviObject> boats;
    @JsonIgnore
    private Dictionary<String,AviObject> buoys;



    private Event(Parcel in) {
        name = in.readString();
        lastBuoyId = in.readInt();
        eventManager = in.readParcelable(User.class.getClassLoader());
    }
    public Event(){}

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(lastBuoyId);
        dest.writeParcelable(eventManager,0);
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
}

package com.aayaffe.sailingracecoursemanager.Events;

import android.os.Parcel;
import android.os.Parcelable;

import com.aayaffe.sailingracecoursemanager.Users.User;

import java.util.ArrayList;

/**
 * Created by aayaffe on 19/02/2016.
 */
public class Event implements Parcelable {
    private String name;
//    private ArrayList<User> users;
    private int lastBuoyId;
    private User eventManager;

//    public ArrayList<User> getUsers() {
//        return users;
//    }
//
//    public void setUsers(ArrayList<User> users) {
//        this.users = users;
//    }

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
}

package com.aayaffe.sailingracecoursemanager.Users;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by aayaffe on 17/02/2016.
 */
public class User{
    public String Uid;
    public String DisplayName;
    public Long joined;
    public Long lastConnection;
    @Exclude
    public Date getJoined() {
        return new Date(joined);
    }
    @Exclude
    public void setJoined(Date joined) {
        this.joined = joined.getTime();
    }
    @Exclude
    public Date getLastConnection() {
        return new Date(lastConnection);
    }
    @Exclude
    public void setLastConnection(Date lastConnection) {
        this.lastConnection = lastConnection.getTime();
    }
}

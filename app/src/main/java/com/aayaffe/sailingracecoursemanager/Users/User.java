package com.aayaffe.sailingracecoursemanager.Users;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 17/02/2016.
 */
public class User{
    public String Uid;
    public String DisplayName;
    //public String Email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Uid != null ? Uid.equals(user.Uid) : user.Uid == null;

    }

    @Override
    public int hashCode() {
        return Uid != null ? Uid.hashCode() : 0;
    }
}

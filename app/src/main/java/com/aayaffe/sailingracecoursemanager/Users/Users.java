package com.aayaffe.sailingracecoursemanager.Users;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.communication.ICommManager;

import java.util.Date;

/**
 * Created by aayaffe on 16/02/2016.
 */
public class Users {
    private static final String TAG = "Users";
    private static User currentUser;
    private ICommManager commManager;

    public Users(ICommManager cm){
        commManager = cm;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        Users.currentUser = currentUser;
    }
    public void setCurrentUser(String Uid, String displayName) {
        Log.d(TAG,"Uid = "+Uid+" displayName = " + displayName);//TODO: to sync with current user found in Firebase class
        User u = commManager.findUser(Uid);
        if (u!=null) {
            currentUser = u;
            u.setLastConnection(new Date());
            commManager.addUser(u);
        }
        else{
            u = new User();
            u.Uid = Uid;
            u.DisplayName = displayName;
            u.setJoined(new Date());
            u.setLastConnection(new Date());
            commManager.addUser(u);
        }
    }

    public void logout() {
        currentUser = null;
        commManager.logout();
    }
}

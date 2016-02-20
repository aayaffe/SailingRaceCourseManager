package com.aayaffe.sailingracecoursemanager.Users;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.communication.ICommManager;

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
        this.currentUser = currentUser;
    }
    public void setCurrentUser(String Uid, String displayName) {
        Log.d(TAG,"Uid = "+Uid+" displayName = " + displayName);
        User u = commManager.findUser(Uid);
        if (u!=null) {
            this.currentUser = u;
       }
        else{
            u = new User();
            u.Uid = Uid;
            u.DisplayName = displayName;
            commManager.addUser(u);
        }
    }
}

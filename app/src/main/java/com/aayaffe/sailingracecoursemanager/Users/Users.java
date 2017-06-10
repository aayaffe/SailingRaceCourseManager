package com.aayaffe.sailingracecoursemanager.Users;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.db.IDBManager;

import java.util.Date;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 16/02/2016.
 */
public class Users {
    private static final String TAG = "Users";
    private static User currentUser;
    private static IDBManager commManager;

    public Users(IDBManager cm){
        if (commManager==null)
            commManager = cm;
    }

    /**
     *
     * @return The currently logged in user, null if no user is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Users.currentUser = currentUser;
        if (currentUser!=null) {
            Users.currentUser.setLastConnection(new Date());
        }
        if (commManager!=null)
            commManager.addUser(Users.currentUser);
    }
    public static void setCurrentUser(String Uid, String displayName) {
        Log.d(TAG,"Uid = "+Uid+" displayName = " + displayName);
        User u = commManager.findUser(Uid);
        if (u!=null) {
            currentUser = u;
            u.setLastConnection(new Date());
            commManager.addUser(u);
        }
        else{ //New user in the system
            u = new User();
            u.Uid = Uid;
            u.DisplayName = displayName;
            u.setJoined(new Date());
            u.setLastConnection(new Date());
            commManager.addUser(u);
        }
    }

    /**
     * Logs out of the db and application
     */
    public void logout() {
        currentUser = null;
        commManager.logout();
    }


    public boolean isAdmin(User u){
        return commManager.isAdmin(u);
    }
}

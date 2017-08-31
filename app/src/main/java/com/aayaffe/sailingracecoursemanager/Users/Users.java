package com.aayaffe.sailingracecoursemanager.Users;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aayaffe.sailingracecoursemanager.db.IDBManager;

import java.util.Date;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 16/02/2016.
 */
public class Users {
    private static Users instance;
    private static final String TAG = "Users";
    private static User currentUser;
    private static IDBManager commManager;
    private static SharedPreferences sharedPreferences = null;


    private Users(IDBManager cm,SharedPreferences sp){
        sharedPreferences = sp;
        if (commManager==null)
            commManager = cm;
        setCurrentUser(null);
    }

    public static void Init(IDBManager cm, SharedPreferences sp){
        if (instance == null){
            instance = new Users(cm,sp);
        }
    }
     public static Users getInstance(){
         return instance;
     }

    /**
     *
     * @return The currently logged in user, null if no user is logged in.
     */
    public User getCurrentUser() {
        if (currentUser==null&&commManager!=null){
            currentUser = commManager.findUser(commManager.getLoggedInUid());
        }
        return currentUser;

    }

    public static void setCurrentUser(User currentUser) {
        Users.currentUser = currentUser;
        if (currentUser!=null) {
            Users.currentUser.setLastConnection(new Date());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UID", currentUser.Uid);
            editor.apply();
        }
        if (commManager!=null)
            commManager.addUser(Users.currentUser);
    }
    public static void setCurrentUser(String Uid, String displayName) {
        Log.d(TAG,"Uid = "+Uid+" displayName = " + displayName);
        User u = commManager.findUser(Uid);
        if (u!=null) {
            setCurrentUser(u);
        }
        else{ //New user in the system
            u = new User();
            u.Uid = Uid;
            u.DisplayName = displayName;
            u.setJoined(new Date());
            setCurrentUser(u);
        }
    }

    /**
     * Logs out of the db and application
     */
    public static void logout() {
        currentUser = null;
        commManager.logout();
    }


    public boolean isAdmin(User u){
        return commManager.isAdmin(u);
    }
}

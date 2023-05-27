package com.aayaffe.sailingracecoursemanager.users

import android.content.SharedPreferences
import android.util.Log
import com.aayaffe.sailingracecoursemanager.db.FirestoreDB.Companion.addUser
import com.aayaffe.sailingracecoursemanager.db.IDBManager
import java.util.Date

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 16/02/2016.
 */
class Users private constructor(cm: IDBManager, sp: SharedPreferences) {
    init {
        sharedPreferences = sp
        if (commManager == null) commManager = cm
        setCurrentUser(null)
    }


    /**
     *
     * @return The currently logged in user, null if no user is logged in.
     */
    val currentUser: User?
        get() {
            if (Companion.currentUser == null && commManager != null) {
                Companion.currentUser = commManager!!.findUser(commManager!!.loggedInUid)
            }
            return Companion.currentUser
        }

    fun isAdmin(u: User?): Boolean {
        return commManager!!.isAdmin(u)
    }

    companion object {
        @JvmStatic
        var instance: Users? = null
            private set
        private const val TAG = "Users"
        private var currentUser: User? = null
        private var commManager: IDBManager? = null
        private var sharedPreferences: SharedPreferences? = null
        @JvmStatic
        fun Init(cm: IDBManager, sp: SharedPreferences) {
            if (instance == null) {
                instance = Users(cm, sp)
            }
        }

        @JvmStatic
        fun setCurrentUser(currentUser: User?) {
            Companion.currentUser = currentUser
            if (currentUser != null) {
                Companion.currentUser!!.setLastConnection(Date())
                val editor = sharedPreferences!!.edit()
                editor.putString("UID", currentUser.uid)
                editor.apply()
            }
            if (commManager != null) {
                commManager!!.addUser(Companion.currentUser)
                if (Companion.currentUser != null) addUser(Companion.currentUser!!)
            }
        }

        @JvmStatic
        fun setCurrentUser(Uid: String, displayName: String) {
            Log.d(TAG, "Uid = $Uid displayName = $displayName")
            var u = commManager!!.findUser(Uid)
            if (u != null) {
                setCurrentUser(u)
            } else { //New user in the system
                u = User(Uid)
//                u.uid = Uid
                u.displayName = displayName
                u.setJoined(Date())
                setCurrentUser(u)
            }
        }

        /**
         * Logs out of the db and application
         */
        @JvmStatic
        fun logout() {
            currentUser = null
            commManager!!.logout()
        }
    }
}
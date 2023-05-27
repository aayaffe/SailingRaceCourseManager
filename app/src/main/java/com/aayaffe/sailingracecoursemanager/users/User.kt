package com.aayaffe.sailingracecoursemanager.users

import com.google.firebase.database.Exclude
import java.util.Date

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 17/02/2016.
 */
class User(@JvmField var uid: String) {
    constructor() : this("0")

    @JvmField
    var displayName: String? = null

    //public String Email;
    private var joined: Long? = null
    private var lastConnection: Long? = null
    @Exclude
    fun getJoined(): Date? {
        return joined?.let { Date(it) }
    }

    @Exclude
    fun setJoined(joined: Date) {
        this.joined = joined.time
    }

    @Exclude
    fun getLastConnection(): Date {
        return Date(lastConnection!!)
    }

    @Exclude
    fun setLastConnection(lastConnection: Date) {
        this.lastConnection = lastConnection.time
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val user = o as User
        return if (uid != null) uid == user.uid else user.uid == null
    }

    override fun hashCode(): Int {
        return if (uid != null) uid.hashCode() else 0
    }
}
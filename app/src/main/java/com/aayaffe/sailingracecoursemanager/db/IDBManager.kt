package com.aayaffe.sailingracecoursemanager.db

import com.aayaffe.sailingracecoursemanager.users.User
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject
import com.aayaffe.sailingracecoursemanager.events.Event
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation
import com.aayaffe.sailingracecoursemanager.initializinglayer.Boat
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor
import com.google.firebase.database.DatabaseReference
import java.util.UUID

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 22/09/2015.
 */
interface IDBManager {
    fun login(): Int
    fun setCommManagerEventListener(listener: CommManagerEventListener?)
    fun writeBoatObject(o: DBObject?): Int
    fun writeBuoyObject(o: DBObject?): Int
    fun updateBoatLocation(e: Event?, boat: DBObject?, loc: AviLocation?): Int

    //ships
    val allBoats: List<DBObject?>?

    //Just buoys, without ships
    val allBuoys: List<DBObject?>?
    val newBuoyId: Long
    fun removeBuoyObject(uuid: String?)
    fun findUser(uid: String?): User?
    fun addUser(u: User?)
    fun logout()
    fun getEvent(eventName: String?): Event?
    val supportedVersion: Long
    fun getObjectByUUID(u: UUID?): DBObject?
    fun writeEvent(neu: Event?)
    var currentEvent: Event?
    fun getAssignedBuoys(b: DBObject?): List<DBObject?>?
    fun getAssignedBoats(b: DBObject?): List<DBObject?>?
    fun getBoat(currentBoatName: String?): DBObject?
    fun getBoatByUserUid(uid: String?): DBObject?
    fun assignBuoy(boat: DBObject?, selectedBuoyName: String?)
    fun removeAssignment(buoy: DBObject?, boat: DBObject?)
    fun removeAssignments(b: DBObject?)
    val boatTypes: List<Boat?>?
    fun removeBoat(u: UUID?)
    fun deleteEvent(event: Event?)
    fun addRaceCourseDescriptor(ct: RaceCourseDescriptor?)
    val raceCourseDescriptors: List<RaceCourseDescriptor?>?
    fun isAdmin(u: User?): Boolean
    fun removeCommManagerEventListener(onConnectEventListener: CommManagerEventListener?)
    val loggedInUid: String?
    fun findUser(uid: UUID?): User?
    val eventBoatsReference: DatabaseReference?
    val fireBaseRef: DatabaseReference?
    val eventBuoysReference: DatabaseReference?
    fun getBuoy(uuid: String?): DBObject?
    fun subscribeToEventDeletion(event: Event?, subscribe: Boolean)
    fun writeLeaveEvent(currentUser: User?, currentEvent: Event?)
}
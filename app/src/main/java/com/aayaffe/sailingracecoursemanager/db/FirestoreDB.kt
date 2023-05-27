package com.aayaffe.sailingracecoursemanager.db

import com.aayaffe.sailingracecoursemanager.users.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class FirestoreDB {
    companion object {
        //    fun writeBoatObject(o: DBObject?): Int
//    fun writeBuoyObject(o: DBObject?): Int
//    fun updateBoatLocation(e: Event?, boat: DBObject?, loc: AviLocation?): Int
//    val allBoats: List<DBObject?>?
//    val allBuoys: List<DBObject?>?
//    val newBuoyId: Long
//    fun removeBuoyObject(uuid: String?)
//    fun findUser(uid: String?): User?
        public fun addUser(u: User) {
            val db = Firebase.firestore
            db.collection("users").add(u)
        }

        public fun getUser(
            uid: UUID,
            onSuccessListener: OnSuccessListener<DocumentSnapshot>,
            onFailureListener: OnFailureListener
        ) {
            getUser(uid.toString(), onSuccessListener, onFailureListener)
        }
        public fun getUser(
            uid: String,
            onSuccessListener: OnSuccessListener<DocumentSnapshot>,
            onFailureListener: OnFailureListener
        ) {
            val db = Firebase.firestore
            val docRef = db.collection("users").document(uid)
            docRef.get().addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener)
        }
//    fun logout()
//    fun getEvent(eventName: String?): Event?
//    val supportedVersion: Long
//    fun getObjectByUUID(u: UUID?): DBObject?
//    fun writeEvent(neu: Event?)
//    var currentEvent: Event?
//    fun getAssignedBuoys(b: DBObject?): List<DBObject?>?
//    fun getAssignedBoats(b: DBObject?): List<DBObject?>?
//    fun getBoat(currentBoatName: String?): DBObject?
//    fun getBoatByUserUid(uid: String?): DBObject?
//    fun assignBuoy(boat: DBObject?, selectedBuoyName: String?)
//    fun removeAssignment(buoy: DBObject?, boat: DBObject?)
//    fun removeAssignments(b: DBObject?)
//    val boatTypes: List<Boat?>?
//    fun removeBoat(u: UUID?)
//    fun deleteEvent(event: Event?)
//    fun addRaceCourseDescriptor(ct: RaceCourseDescriptor?)
//    val raceCourseDescriptors: List<RaceCourseDescriptor?>?
//    fun isAdmin(u: User?): Boolean
//    fun removeCommManagerEventListener(onConnectEventListener: CommManagerEventListener?)
//    val loggedInUid: String?
//    fun findUser(uid: UUID?): User?
//    val eventBoatsReference: DatabaseReference?
//    val fireBaseRef: DatabaseReference?
//    val eventBuoysReference: DatabaseReference?
//    fun getBuoy(uuid: String?): DBObject?
//    fun subscribeToEventDeletion(event: Event?, subscribe: Boolean)
//    fun writeLeaveEvent(currentUser: User?, currentEvent: Event?)
    }
}
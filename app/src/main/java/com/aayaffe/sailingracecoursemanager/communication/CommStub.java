//package com.aayaffe.sailingracecoursemanager.communication;
//
//import android.location.Location;
//
//import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
//import com.aayaffe.sailingracecoursemanager.geographical.GeoUtils;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by aayaffe on 22/09/2015.
// */
//public class CommStub implements ICommManager {
//
//    List<AviObject> aviObjects = new ArrayList<>();
//
//    @Override
//    public int login(String user, String password, String nickname) {
//        return 0;
//    }
//
//    @Override
//    public int writeBoatObject(AviObject o) {
//        return 0;
//    }
//
//    @Override
//    public int writeBuoyObject(AviObject o) {
//        return 0;
//    }
//
//
//    @Override
//    public List<AviObject> getAllBoats() {
//        AviObject o = new AviObject();
//        o.lastUpdate = new Date(System.currentTimeMillis());
//        o.name = "Worker1";
//        o.location = GeoUtils.toAviLocation(GeoUtils.createLocation(32.75, 34.58));
//        o.color = "blue";
//        o.type = ObjectTypes.WorkerBoat;
//        if (aviObjects.contains(o)) {
//            aviObjects.remove(o);
//            aviObjects.add(o);
//        } else aviObjects.add(o);
//        o = new AviObject();
//        o.lastUpdate = new Date(System.currentTimeMillis());;
//        o.name = "Worker2";
//        o.location = GeoUtils.toAviLocation(GeoUtils.createLocation(32.8, 34.59));
//        o.color = "cyan";
//        o.type = ObjectTypes.WorkerBoat;
//        if (aviObjects.contains(o)) {
//            aviObjects.remove(o);
//            aviObjects.add(o);
//        } else aviObjects.add(o);
//        return aviObjects;
//    }
//
//    @Override
//    public List<AviObject> getAllBuoys() {
//        return null;
//    }
//
//    @Override
//    public int sendAction(RaceManagerAction a, AviObject o) {
//        return 0;
//    }
//
//    @Override
//    public long getNewBuoyId() {
//        return 0;
//    }
//
//    @Override
//    public void removeBueyObject(String title) {
//
//    }
//
//
//}

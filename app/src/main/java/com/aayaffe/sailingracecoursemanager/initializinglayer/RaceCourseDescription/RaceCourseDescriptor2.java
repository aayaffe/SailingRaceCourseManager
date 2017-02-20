package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import android.util.Log;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.calclayer.BuoyType;
import com.aayaffe.sailingracecoursemanager.calclayer.DBObject;
import com.aayaffe.sailingracecoursemanager.calclayer.Mark;
import com.aayaffe.sailingracecoursemanager.geographical.AviLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.Legs;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class RaceCourseDescriptor2 {
    public String name;
    public int imageID = R.drawable.racecourse_optimist;
    public List<Legs> legDescriptors;
    private UUID uuid;
    public Long lastUpdate;

    public RaceCourseDescriptor2(){
        uuid = UUID.randomUUID();
    }


    public void setUuidString(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getUuidString() {
        return this.uuid.toString();
    }

    @Exclude
    public Date getLastUpdate() {
        return new Date(lastUpdate);
    }

    @Exclude
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate.getTime();
    }

    public String getName() {
        return name;
    }

    public int getImageID() {
        return imageID;
    }

    public List<Legs> getRaceCourseLegs() {
        return legDescriptors;
    }

    @Exclude
    public String[] getLegsNames() {
        String[] names = new String[legDescriptors.size()];
        for(int i = 0; i< legDescriptors.size(); i++){
            names[i]= legDescriptors.get(i).getName();
        }
        return names;
    }


}

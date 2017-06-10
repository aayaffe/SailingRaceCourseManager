package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import com.aayaffe.sailingracecoursemanager.R;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Avi Marine Innovations - www.avimarine.in
 *
 * Created by Amit Y. on 29/12/2016.
 */

public class RaceCourseDescriptor implements Serializable{
    public String name;
    public int imageID = R.drawable.racecourse_optimist;
    public ArrayList<Legs> legDescriptors;
    private UUID uuid;
    public Long lastUpdate;


    public RaceCourseDescriptor(){
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

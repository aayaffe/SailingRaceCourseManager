package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class RaceCourseDescriptor2 {
    public String name;
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
}

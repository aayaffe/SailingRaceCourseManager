package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Mark2;

import java.util.List;
import java.util.UUID;

/**
 * Created by aayaffe on 29/12/2016.
 */
public class Legs {
    public String name;
    public List<Double> lengthFactors;
    public List<Mark2> marks;

    private UUID uuid;

    public Legs(){
        uuid = UUID.randomUUID();
    }

    public void setUuidString(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getUuidString() {
        return this.uuid.toString();
    }

}

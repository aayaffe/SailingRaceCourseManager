package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import com.aayaffe.sailingracecoursemanager.initializinglayer.GateOption;
import com.aayaffe.sailingracecoursemanager.initializinglayer.GateReference;
import com.aayaffe.sailingracecoursemanager.initializinglayer.GateType;

import java.util.UUID;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class Mark2 {
    public String name; //Display name
    public int id; //ID used to identify in race course
    public MarkLocation ml;
    public boolean isGatable;
    public GateConfiguration go;

    private UUID uuid;

    public Mark2(String name, int id, MarkLocation ml, GateConfiguration go) {
        this(name, id, ml);
        isGatable = true;
        this.go = go;
    }

    public Mark2(String name, int id, MarkLocation ml) {
        this.name = name;
        this.id = id;
        this.ml = ml;
        this.isGatable = false;
        uuid = UUID.randomUUID();

    }

    public void setUuidString(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getUuidString() {
        return this.uuid.toString();
    }
}



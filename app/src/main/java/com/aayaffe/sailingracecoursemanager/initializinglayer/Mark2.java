package com.aayaffe.sailingracecoursemanager.initializinglayer;

import java.util.UUID;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class Mark2 {
    public String name;
    public int direction;
    public double distance;
    public boolean distanceFactor;
    public GateType gateType;
    public GateOption gateOption;
    public int gateDirection;
    public double gateDistance;
    public GateReference gateReference;


    private UUID uuid;


    public void setUuidString(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getUuidString() {
        return this.uuid.toString();
    }
}

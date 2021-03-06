package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import java.io.Serializable;

public class GateConfiguration implements Serializable{
    public GateType gateType; //Type of gate
    public GateOption gateOption; //Gate optional?
    /**
     * Direction of gate relative to direction from last mark
     */
    public int gateDirection;
    public double gateWidth; //gate Width in NM. disregarded if gateRelativeWidth==true
    public GateReference gateReference; //Gate's reference position
    public boolean gateRelativeWidth; //is gate width relative to number of boats.

    public GateConfiguration(GateType gateType, GateOption gateOption, int gateDirection, double gateWidth, GateReference gateReference, boolean gateRelativeWidth) {
        this.gateType = gateType;
        this.gateOption = gateOption;
        this.gateDirection = gateDirection;
        this.gateWidth = gateWidth;
        this.gateReference = gateReference;
        this.gateRelativeWidth = gateRelativeWidth;
    }
}

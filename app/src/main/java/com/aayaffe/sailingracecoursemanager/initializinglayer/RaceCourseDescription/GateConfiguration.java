package com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription;

import com.aayaffe.sailingracecoursemanager.initializinglayer.GateOption;
import com.aayaffe.sailingracecoursemanager.initializinglayer.GateReference;
import com.aayaffe.sailingracecoursemanager.initializinglayer.GateType;

public class GateConfiguration{
    public GateType gateType; //Type of gate
    public GateOption gateOption; //Gate optional?
    public int gateDirection; //Direction of gate relative to direction from last mark?
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

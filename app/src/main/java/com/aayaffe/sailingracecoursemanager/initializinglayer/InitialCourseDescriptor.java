package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.calclayer.Mark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class InitialCourseDescriptor implements Serializable {
    public List<RaceCourseDescriptor2> raceCourseDescriptors;

    public InitialCourseDescriptor(){
        raceCourseDescriptors = new ArrayList<>();
        RaceCourseDescriptor2 trapezoid60120 = new RaceCourseDescriptor2();
        trapezoid60120.setLastUpdate(new Date());
        trapezoid60120.legDescriptors = new ArrayList<>();
        Legs shortedOuter = new Legs();
        shortedOuter.name = "Shorted Outer";
        Mark2 mk1 = new Mark2();
        mk1.name = "1";
        mk1.direction = 0;
        mk1.distance = 1;
        mk1.distanceFactor = true;
        mk1.gateDirection = -125;
        mk1.gateDistance = 0.08;
        mk1.gateOption = GateOption.GATABLE;
        mk1.gateType = GateType.SATELLITE;
        mk1.gateReference = GateReference.LEFT_MARK;







    }
}

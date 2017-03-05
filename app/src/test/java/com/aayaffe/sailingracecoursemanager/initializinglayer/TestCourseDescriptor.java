package com.aayaffe.sailingracecoursemanager.initializinglayer;

import com.aayaffe.sailingracecoursemanager.R;
import com.aayaffe.sailingracecoursemanager.General.GeneralUtils;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateConfiguration;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateOption;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateReference;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.GateType;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Legs;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.LocationOptions;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.Mark2;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkLocation;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.MarkRoundingOrder;
import com.aayaffe.sailingracecoursemanager.initializinglayer.RaceCourseDescription.RaceCourseDescriptor2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

import static java.util.Arrays.asList;

/**
 * Created by aayaffe on 29/12/2016.
 */

public class TestCourseDescriptor implements Serializable {
    public List<RaceCourseDescriptor2> getRaceCourseDescriptors() {
        return raceCourseDescriptors;
    }

    public List<RaceCourseDescriptor2> raceCourseDescriptors;

    public TestCourseDescriptor(){
        raceCourseDescriptors = new ArrayList<>();
        raceCourseDescriptors.add(GenerateSimpleRC());

    }
    private RaceCourseDescriptor2 GenerateSimpleRC(){
        RaceCourseDescriptor2 test1 = new RaceCourseDescriptor2();
        test1.name = "Test1";
        test1.setLastUpdate(new Date());
        test1.legDescriptors = new ArrayList<>();
        test1.imageID = R.drawable.trapzoid_shorted_inner;

        Legs testLeg1 = new Legs();
        testLeg1.marks = new ArrayList<>();
        testLeg1.name = "Test1";
        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,true, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
        Mark2 mk1 = new Mark2("Mk1", 1, new MarkLocation(0, 1, true, LocationOptions.FROM_MARK_ID,4), new GateConfiguration(GateType.SATELLITE, GateOption.GATABLE, -125, 0.08, GateReference.LEFT_MARK, false));
        Mark2 mk2 = new Mark2("Mk2", 2, new MarkLocation(-120, 0.67, true, LocationOptions.FROM_MARK_ID,1));
        Mark2 mk3 = new Mark2("Mk3", 3, new MarkLocation(-180, 1, true, LocationOptions.FROM_MARK_ID,2), new GateConfiguration(GateType.GATE, GateOption.GATABLE, -90, 0.027, GateReference.GATE_CENTER, true));
        Mark2 finish = new Mark2("Finish", 5, new MarkLocation(120, 2.5, false, LocationOptions.FROM_MARK_ID,3), new GateConfiguration(GateType.FINISH_LINE, GateOption.ALWAYS_GATED, -180, 0.032, GateReference.LEFT_MARK, false));
        GeneralUtils.addAll(testLeg1.marks,start,mk4,mk1,mk2,mk3,finish);
        GeneralUtils.addAll(test1.legDescriptors,testLeg1);
        testLeg1.defaultMarkRounding = new MarkRoundingOrder("Inner2", new ArrayList<>(asList(0,4,1,4,1,2,3,5)));
        return test1;
    }

    //    private RaceCourseDescriptor2 GenerateSimpleRC(){
//        RaceCourseDescriptor2 trapezoid60120 = new RaceCourseDescriptor2();
//        trapezoid60120.name = "Test1";
//        trapezoid60120.setLastUpdate(new Date());
//        trapezoid60120.legDescriptors = new ArrayList<>();
//        trapezoid60120.imageID = R.drawable.trapzoid_shorted_inner;
//
//        Legs twoThirdsBeat = new Legs();
//        twoThirdsBeat.marks = new ArrayList<>();
//        twoThirdsBeat.name = "2/3 Beat";
//        Mark2 start = new Mark2("Start",0,new MarkLocation(0,0,false, LocationOptions.FROM_RACE_COMMITTEE),new GateConfiguration(GateType.START_LINE,GateOption.ALWAYS_GATED,-90,0.027,GateReference.RIGHT_MARK,true));
//        Mark2 mk4 = new Mark2("Mk4",4,new MarkLocation(0,0.05,false, LocationOptions.FROM_MARK_ID,0),new GateConfiguration(GateType.GATE,GateOption.GATABLE,-90,0.027,GateReference.GATE_CENTER,true));
//        Mark2 mk1 = new Mark2("Mk1", 1, new MarkLocation(0, 1, true, LocationOptions.FROM_MARK_ID,4), new GateConfiguration(GateType.SATELLITE, GateOption.GATABLE, -125, 0.08, GateReference.LEFT_MARK, false));
//        Mark2 mk2 = new Mark2("Mk2", 2, new MarkLocation(-120, 0.67, true, LocationOptions.FROM_MARK_ID,1));
//        Mark2 mk3 = new Mark2("Mk3", 3, new MarkLocation(-180, 1, true, LocationOptions.FROM_MARK_ID,2), new GateConfiguration(GateType.GATE, GateOption.GATABLE, -90, 0.027, GateReference.GATE_CENTER, true));
//        Mark2 finish = new Mark2("Finish", 5, new MarkLocation(0, 0, false, LocationOptions.FROM_RACE_COMMITTEE), new GateConfiguration(GateType.FINISH_LINE, GateOption.ALWAYS_GATED, -180, 0.032, GateReference.LEFT_MARK, false));
//        GeneralUtils.addAll(twoThirdsBeat.marks,start,mk4,mk1,mk2,mk3,finish);
//
//
//        GeneralUtils.addAll(trapezoid60120.legDescriptors,twoThirdsBeat);
//        return trapezoid60120;
//    }




}
